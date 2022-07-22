package com.gdemarcsek.appsec.visibility.demo.db;

import io.dropwizard.hibernate.AbstractDAO;

import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.gdemarcsek.appsec.visibility.demo.core.Person;

@Singleton
public class PersonDAO extends AbstractDAO<Person> {
    @Inject
    public PersonDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Person> findById(UUID id) {
        return Optional.ofNullable(get(id));
    }

    public Person create(Person person) {
        return persist(person);
    }

    public List<Person> findAll() {
        return list(namedTypedQuery("com.example.helloworld.core.Person.findAll"));
    }
}