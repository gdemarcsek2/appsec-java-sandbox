package com.gdemarcsek.appsec.visibility.demo.resources;

import org.junit.jupiter.api.Test;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.swagger.v3.oas.annotations.Operation;

import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import com.gdemarcsek.appsec.visibility.demo.ORMAuditLoggingDemoApplication;
import com.gdemarcsek.appsec.visibility.demo.ORMAuditLoggingDemoConfiguration;

// TODO: Turn some of the checks into custom error-prone plugins
@ExtendWith(DropwizardExtensionsSupport.class)
public class ResourceTest {
        private static DropwizardAppExtension<ORMAuditLoggingDemoConfiguration> EXT = new DropwizardAppExtension<>(
                        ORMAuditLoggingDemoApplication.class,
                        ResourceHelpers.resourceFilePath("config.yml"));

        @Test
        void testResourceClassProperties() throws Exception {
                EXT.getEnvironment().jersey().getResourceConfig().getClasses().stream().forEach(
                                cl -> assertThat((cl.getPackage() != ResourceTest.class.getPackage())
                                                || (cl.getDeclaredAnnotationsByType(PermitAll.class).length == 1))
                                                .as("class %s must not be a resource or have the PermitAll annotation",
                                                                cl.getCanonicalName())
                                                .isTrue());
        }

        @Test
        void testResourceMethods() throws Exception {
                EXT.getEnvironment().jersey().getResourceConfig().getClasses().stream().forEach(
                                cl -> Arrays.stream(cl.getMethods()).filter(
                                                m -> Modifier.isPublic(m.getModifiers()) && m.getDeclaringClass() == cl && cl.getPackage() == ResourceTest.class.getPackage())
                                                .forEach(
                                                                m -> assertThat(m.getDeclaredAnnotationsByType(
                                                                                Valid.class).length == 1)
                                                                                .as("method %s.%s must have the Valid annotation",
                                                                                                cl.getCanonicalName(), m.getName())
                                                                                .isTrue()));

                EXT.getEnvironment().jersey().getResourceConfig().getClasses().stream().forEach(
                                cl -> Arrays.stream(cl.getMethods()).filter(
                                                m -> Modifier.isPublic(m.getModifiers()) && m.getDeclaringClass() == cl && cl.getPackage() == ResourceTest.class.getPackage())
                                                .forEach(
                                                                m -> assertThat(m.getDeclaredAnnotationsByType(
                                                                                Operation.class).length == 1)
                                                                                .as("method %s.%s must have the Operation annotation",
                                                                                                cl.getCanonicalName(), m.getName())
                                                                                .isTrue()));
        }
}
