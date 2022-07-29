package com.gdemarcsek.appsec.visibility.demo.core;

import java.util.regex.Pattern;

import javax.persistence.Embeddable;

@Embeddable
public class HotelName {
    private final String name;

    public static final Pattern pattern = Pattern.compile("^[A-Z0-9][a-zA-Z0-9 '&,.:\\-]+$");
    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 30;

    public HotelName(String hotelName) {
        if (hotelName == null || hotelName.isBlank() || hotelName.isEmpty()) {
            throw new IllegalArgumentException("Hotel name must be specified");
        }

        if (hotelName.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Hotel name too short - must be at least " + MIN_LENGTH + " characters");
        }

        if (hotelName.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Hotel name too long - must be at fewer than " + MAX_LENGTH + " characters");
        }

        if (!pattern.matcher(hotelName).find()) {
            throw new IllegalArgumentException("Hotel name must begin with an uppercase letter or number and may only contain a limited set of special characters");
        }

        this.name = hotelName;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
