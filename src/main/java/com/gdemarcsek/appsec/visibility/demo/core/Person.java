package com.gdemarcsek.appsec.visibility.demo.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Entity
@Data
@Table(name = "people")
public class Person extends EntityBase {
        @Column(name = "fullName", nullable = false)
        @NotNull
        private String fullName;

        @Column(name = "jobTitle", nullable = false)
        @NotNull
        private String jobTitle;

        @Column(name = "yearBorn")
        @Min(value = 0)
        @Max(value = 9999)
        private int yearBorn;
}