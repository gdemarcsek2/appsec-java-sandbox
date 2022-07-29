package com.gdemarcsek.appsec.visibility.demo;

import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.gdemarcsek.appsec.visibility.demo.db.PersonDAO;
import com.gdemarcsek.appsec.visibility.demo.resources.PersonResource;
import com.gdemarcsek.appsec.visibility.demo.util.DependencyInjectionConfiguration;

import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.client.Client;

import io.dropwizard.db.DataSourceFactory;


class URLSerializer extends StdSerializer<URL> {
    public URLSerializer() {
        this(null);
    }

    public URLSerializer(Class<URL> t) {
        super(t);
    }

    @Override
    public void serialize(URL value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.toString());
    }
}

class URLDeSerializer extends StdDeserializer<URL> {
    public URLDeSerializer() {
        this(null);
    }

    public URLDeSerializer(Class<URL> vc) {
        super(vc);
    }

    @Override
    public URL deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return new URL(p.getValueAsString());
    }

}

public class ORMAuditLoggingDemoConfiguration extends Configuration implements DependencyInjectionConfiguration {
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @NotNull
    private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    @Valid
    @NotNull
    private URL identityTrustRootUrl;

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

    @JsonProperty("identityTrustRootUrl")
    @JsonDeserialize(using = URLDeSerializer.class)
    public void setIdentituTrustRootUrl(URL url) {
        this.identityTrustRootUrl = url;
    }

    @JsonProperty("identityTrustRootUrl")
    @JsonSerialize(using = URLSerializer.class)
    public URL getIdentituTrustRootUrl() {
        return this.identityTrustRootUrl;
    }
}
