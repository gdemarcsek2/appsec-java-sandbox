package com.gdemarcsek.appsec.visibility.demo.util;

import io.dropwizard.auth.*;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Optional;

import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtContext;

import com.gdemarcsek.appsec.visibility.demo.core.User;

public class ExampleAuthenticator implements Authenticator<JwtContext, User> {
    @Override
    public Optional<User> authenticate(JwtContext context) throws AuthenticationException {
        try {
            final String subject = context.getJwtClaims().getSubject();
            return Optional.of(new User(subject));
        } catch (MalformedClaimException e) {}
        return Optional.empty();
    }
}