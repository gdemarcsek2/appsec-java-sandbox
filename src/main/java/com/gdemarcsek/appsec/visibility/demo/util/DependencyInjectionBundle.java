package com.gdemarcsek.appsec.visibility.demo.util;

import javax.inject.Singleton;

import com.gdemarcsek.appsec.visibility.demo.ORMAuditLoggingDemoConfiguration;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Environment;

public class DependencyInjectionBundle implements ConfiguredBundle<ORMAuditLoggingDemoConfiguration> {

    @Override
    public void run(ORMAuditLoggingDemoConfiguration configuration, Environment environment) throws Exception {
        environment
                .jersey()
                .register(
                        new AbstractBinder() {
                            @Override
                            protected void configure() {
                                for (Class<?> singletonClass : configuration.getSingletons()) {
                                    bindAsContract(singletonClass).in(Singleton.class);
                                }
                            }
                        });
    }
}
