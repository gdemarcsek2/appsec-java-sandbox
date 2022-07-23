package com.gdemarcsek.appsec.visibility.demo.db;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.google.errorprone.annotations.CompileTimeConstant;
import com.google.errorprone.annotations.DoNotCall;

import io.dropwizard.hibernate.AbstractDAO;

public abstract class SafeDAO<E> extends AbstractDAO<E> {
    public SafeDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    protected final Query<E> safeQuery(@CompileTimeConstant final String queryString) {
        return super.query(queryString);
    }

    @Override
    @DoNotCall
    protected final Query<E> query(final String queryString) {
        return super.query(queryString);
    }
}
