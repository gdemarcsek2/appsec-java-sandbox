package com.gdemarcsek.appsec.visibility.demo.util;

import java.io.IOException;

import java.lang.reflect.Type;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.slf4j.MDC;

public class AuditLoggingResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException, IllegalArgumentException {

        Type entityType = responseContext.getEntityType();

        if (entityType == null) {
            return;
        }

        MDC.put("accessedEntityType", entityType.getTypeName());

        try {
            if (Class.forName(entityType.getTypeName()).getAnnotation(AuditAccess.class) != null) {
                MDC.put("accessedEntity", responseContext.getEntity().toString());
            }
        } catch (ClassNotFoundException e) {
        }
    }

}
