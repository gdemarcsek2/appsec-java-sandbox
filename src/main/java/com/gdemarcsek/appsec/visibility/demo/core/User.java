package com.gdemarcsek.appsec.visibility.demo.core;

import java.security.Principal;

import lombok.ToString;

@ToString(callSuper=true, includeFieldNames=true)
public class User implements Principal {

    private String Name;

    public User(String name) {
        this.Name = name;
    }

    @Override
    public String getName() {
        return this.Name;
    }

    public String getId() {
        return this.getName();
    }
}
