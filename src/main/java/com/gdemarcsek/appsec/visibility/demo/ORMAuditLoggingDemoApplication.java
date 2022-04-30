package com.gdemarcsek.appsec.visibility.demo;

import com.gdemarcsek.appsec.visibility.demo.core.HashRedactionStrategy;
import com.gdemarcsek.appsec.visibility.demo.core.Person;
import com.gdemarcsek.appsec.visibility.demo.core.Sensitive;
import com.gdemarcsek.appsec.visibility.demo.core.User;
import com.gdemarcsek.appsec.visibility.demo.db.PersonDAO;
import com.gdemarcsek.appsec.visibility.demo.presentation.GetPersonDto;
import com.gdemarcsek.appsec.visibility.demo.resources.*;
import com.gdemarcsek.appsec.visibility.demo.util.*;

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
import io.dropwizard.auth.AuthValueFactoryProvider;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import com.google.common.collect.ImmutableList;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

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
        new ORMAuditLoggingDemoApplication().run(args);
    }

    @Override
    public String getName() {
        return "ORMAuditLoggingDemo";
    }

    @Override
    public void initialize(final Bootstrap<ORMAuditLoggingDemoConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new MigrationsBundle<ORMAuditLoggingDemoConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ORMAuditLoggingDemoConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    private static String getEnvironment() {
        String vendor = System.getProperty("java.vm.vendor");
        String vm = System.getProperty("java.vm.name");
        String version = System.getProperty("java.vm.version");

        return String.format("%s %s %s", vendor, vm, version);
    }

    @Override
    public void run(final ORMAuditLoggingDemoConfiguration configuration, final Environment environment) {
        final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());
        final ModelMapper mm = new ModelMapper();
        final OpenAPI oas = new OpenAPI();

        log.info(String.format("Starting %s in environment %s", getClass().getName(), getEnvironment()));
        Info openApiInfo = new Info().title("Simple REST API example")
                                    .description("Example");
        oas.info(openApiInfo);
        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                                            .openAPI(oas)
                                            .prettyPrint(true)
                                            .resourcePackages(Stream.of("com.gdemarcsek.appsec.visibility.demo.resources").collect(Collectors.toSet()));

        
        Converter<UUID, String> uuidToString = ctx -> ctx.getSource() == null ? null : ctx.getSource().toString();
        mm.typeMap(Person.class, GetPersonDto.class)
                .addMappings(mapper -> mapper.using(uuidToString).map(Person::getId, GetPersonDto::setId));
        Converter<String, Sensitive<String>> sensitiveConverter = ctx -> ctx.getSource() == null ? null
                : new Sensitive<String>(ctx.getSource(), HashRedactionStrategy.getInstance());
        mm.typeMap(Person.class, GetPersonDto.class).addMappings(
                mapper -> mapper.using(sensitiveConverter).map(Person::getFullName, GetPersonDto::setFullName));

        environment.jersey()
                .register(new OpenApiResource().openApiConfiguration(oasConfig));
        environment.jersey()
                .register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(new ExampleAuthenticator()).setAuthorizer(new ExampleAuthorizer())
                        .setRealm("SUPER SECRET STUFF").buildAuthFilter()));
    
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
        environment.jersey().register(new ThreadLocalContextFilter());
        environment.jersey().register(new AuditLoggingResponseFilter());
        environment.jersey().register(new PersonResource(dao, mm));
    }

}