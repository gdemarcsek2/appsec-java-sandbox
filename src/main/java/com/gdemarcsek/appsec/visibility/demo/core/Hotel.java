package com.gdemarcsek.appsec.visibility.demo.core;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.Singular;
import lombok.ToString;

@Data
@Entity
@ToString(onlyExplicitlyIncluded = true)
public class Hotel extends EntityBase {
    @Embedded
    private final HotelName name;

    @NotNull @ManyToOne
    private final HotelAccount ownerAccount;

    private Date registrationDate;

    private boolean acceptsReservations = false;

    private boolean searchable = false;

    private boolean blocked = false;

    @Singular @OneToMany
    private List<HotelRoom> rooms;

    @Singular @Embedded
    private Set<HotelFeature> features;


    @Override
    public String toString() {
        return String.format("Hotel %s", this.name.toString());
    }

    public Hotel(HotelName name, HotelAccount owner) {
        this.name = name;
        this.ownerAccount = owner;
    }
}
