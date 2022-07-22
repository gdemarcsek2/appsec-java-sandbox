package com.gdemarcsek.appsec.visibility.demo;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gdemarcsek.appsec.visibility.demo.db.PersonDAO;
import com.gdemarcsek.appsec.visibility.demo.resources.PersonResource;
import com.gdemarcsek.appsec.visibility.demo.util.DependencyInjectionConfiguration;

import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.client.Client;

import io.dropwizard.db.DataSourceFactory;


public class ORMAuditLoggingDemoConfiguration extends Configuration implements DependencyInjectionConfiguration {
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @NotNull
    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }

    @Override
    public List<Class<?>> getSingletons() {
        final List<Class<?>> result = new ArrayList<Class<?>>();
        result.add(PersonResource.class);
        result.add(ModelMapper.class);
        result.add(PersonDAO.class);
        result.add(Client.class);

        return result;
    }

    @JsonProperty("jerseyClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClient;
    }

    @JsonProperty("jerseyClient")
    public void setJerseyClientConfiguration(JerseyClientConfiguration jerseyClient) {
        this.jerseyClient = jerseyClient;
    }
}
