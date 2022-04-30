package com.gdemarcsek.appsec.visibility.demo.core;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import lombok.Getter;
import java.util.UUID;
import java.util.Date;


@MappedSuperclass
public abstract class EntityBase {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected @Getter UUID id;

    @CreationTimestamp
    protected @Getter Date createdAt;

    @UpdateTimestamp
    protected @Getter Date updatedAt;
}
