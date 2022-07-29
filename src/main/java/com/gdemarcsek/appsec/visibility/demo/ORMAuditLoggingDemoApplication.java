package com.gdemarcsek.appsec.visibility.demo;

import com.gdemarcsek.appsec.visibility.demo.core.Person;
import com.gdemarcsek.appsec.visibility.demo.core.User;
import com.gdemarcsek.appsec.visibility.demo.db.PersonDAO;
import com.gdemarcsek.appsec.visibility.demo.presentation.GetPersonDto;
import com.gdemarcsek.appsec.visibility.demo.resources.*;
import com.gdemarcsek.appsec.visibility.demo.util.*;
import com.github.toastshaman.dropwizard.auth.jwt.JwtAuthFilter;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.SessionFactoryFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.auth.AuthValueFactoryProvider;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwa.AlgorithmConstraints.ConstraintType;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwx.JsonWebStructure;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import org.jose4j.keys.resolvers.VerificationKeyResolver;
import org.jose4j.lang.UnresolvableKeyException;

import com.google.common.collect.ImmutableList;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.io.ObjectInputFilter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.client.Client;

import lombok.extern.slf4j.Slf4j;

import java.net.URLClassLoader;
import java.security.Key;
import java.net.URL;

@Slf4j
public class ORMAuditLoggingDemoApplication extends Application<ORMAuditLoggingDemoConfiguration> {
    private final HibernateBundle<ORMAuditLoggingDemoConfiguration> hibernateBundle = new HibernateBundle<ORMAuditLoggingDemoConfiguration>(
            ImmutableList.<Class<?>>of(Person.class), new SessionFactoryFactory() {
                @Override
                protected void configure(org.hibernate.cfg.Configuration configuration,
                        org.hibernate.service.ServiceRegistry registry) {
                    super.configure(configuration, registry);
                    configuration.setInterceptor(new AuditInterceptor());
                }
            }) {
        @Override
        public DataSourceFactory getDataSourceFactory(ORMAuditLoggingDemoConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(final String[] args) throws Exception {
        ObjectInputFilter.Config.setSerialFilter(new ObjectInputFilter() {
            @Override
            public Status checkInput(FilterInfo filterInfo) {
                return Status.REJECTED;
            }

        });

        new ORMAuditLoggingDemoApplication().run(args);
    }

    @Override
    public String getName() {
        return "ORMAuditLoggingDemo";
    }

    @Override
    public void initialize(final Bootstrap<ORMAuditLoggingDemoConfiguration> bootstrap) {
        // Add Hibernate bundle
        bootstrap.addBundle(hibernateBundle);
        
        // Add Migration bundle
        bootstrap.addBundle(new MigrationsBundle<ORMAuditLoggingDemoConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ORMAuditLoggingDemoConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

        // Allow configuration variable interpolation from the enviornment
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
    }

    private static String getEnvironment() {
        String vendor = System.getProperty("java.vm.vendor");
        String vm = System.getProperty("java.vm.name");
        String version = System.getProperty("java.vm.version");
        return String.format("%s %s %s", vendor, vm, version);
    }

    @Override
    public void run(final ORMAuditLoggingDemoConfiguration configuration, final Environment environment)
            throws Exception {
        final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());
        final ModelMapper mm = new ModelMapper();
        final OpenAPI oas = new OpenAPI();
        final DependencyInjectionBundle dependencyInjectionBundle = new DependencyInjectionBundle();
        final Client client = new JerseyClientBuilder(environment).using(configuration.getJerseyClientConfiguration())
                .build(getName());
        final JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setAllowedClockSkewInSeconds(30)
                .setRequireExpirationTime()
                .setRequireIssuedAt()
                .setRequireSubject()
                .setJweAlgorithmConstraints(new AlgorithmConstraints(ConstraintType.WHITELIST, AlgorithmIdentifiers.ECDSA_USING_P256_CURVE_AND_SHA256))
                .setVerificationKeyResolver(new HttpsJwksVerificationKeyResolver(new HttpsJwks(configuration.getIdentituTrustRootUrl().toString())))
                .build();

        log.info(String.format("Starting %s in environment %s", getClass().getName(), getEnvironment()));
        Info openApiInfo = new Info().title("Simple REST API example")
                .description("Example");
        oas.info(openApiInfo);

        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                .openAPI(oas)
                .prettyPrint(true)
                .resourcePackages(
                        Stream.of("com.gdemarcsek.appsec.visibility.demo.resources").collect(Collectors.toSet()));

        Converter<UUID, String> uuidToString = ctx -> ctx.getSource() == null ? null : ctx.getSource().toString();
        mm.typeMap(Person.class, GetPersonDto.class)
                .addMappings(mapper -> mapper.using(uuidToString).map(Person::getId, GetPersonDto::setId));

        environment.jersey()
                .register(new OpenApiResource().openApiConfiguration(oasConfig));

        environment.jersey()
                .register(new AuthDynamicFeature(new JwtAuthFilter.Builder<User>()
                        .setJwtConsumer(jwtConsumer)
                        .setAuthenticator(new ExampleAuthenticator())
                        .setAuthorizer(new ExampleAuthorizer())
                        .setPrefix("Bearer")
                        .setRealm("SUPER SECRET STUFF")
                        .buildAuthFilter()));
        
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        environment.jersey().register(new ThreadLocalContextFilter());
        environment.jersey().register(new AuditLoggingResponseFilter());

        environment.jersey().register(dao);
        environment.jersey().register(client);

        dependencyInjectionBundle.run(configuration, environment);

        environment.jersey().register(PersonResource.class);
        environment.jersey().getResourceConfig().getClasses().forEach(
                cl -> log.info(cl.getCanonicalName()));

    }

}
