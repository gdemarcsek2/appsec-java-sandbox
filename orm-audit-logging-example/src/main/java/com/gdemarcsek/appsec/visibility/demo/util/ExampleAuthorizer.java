package com.gdemarcsek.appsec.visibility.demo.util;

import com.gdemarcsek.appsec.visibility.demo.core.User;

import io.dropwizard.auth.Authorizer;

public class ExampleAuthorizer implements Authorizer<User> {
    @Override
    public boolean authorize(User user, String role) {
        return user.getName().equals("good-guy") && role.equals("ADMIN");
    }
}