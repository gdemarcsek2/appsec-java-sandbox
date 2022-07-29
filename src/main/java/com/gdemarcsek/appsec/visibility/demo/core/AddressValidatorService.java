package com.gdemarcsek.appsec.visibility.demo.core;

public interface AddressValidatorService {
    public void ensureValidPhysicalAddress(String country, String state, String city, String zipCode, String street, String number) throws InvalidLocation;
}
