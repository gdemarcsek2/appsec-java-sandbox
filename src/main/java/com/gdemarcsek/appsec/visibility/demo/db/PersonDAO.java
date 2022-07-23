package com.gdemarcsek.appsec.visibility.demo.db;

import org.hibernate.SessionFactory;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;

import com.gdemarcsek.appsec.visibility.demo.core.Person;

@Singleton
public class PersonDAO extends SafeDAO<Person> {
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
}