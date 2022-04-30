package com.gdemarcsek.appsec.visibility.demo.util;

import io.dropwizard.auth.*;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Optional;

import com.gdemarcsek.appsec.visibility.demo.core.User;

public class ExampleAuthenticator implements Authenticator<BasicCredentials, User> {
    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
        if ("secret".equals(credentials.getPassword())) {
            return Optional.of(new User(credentials.getUsername()));
        }
        return Optional.empty();
    }
}