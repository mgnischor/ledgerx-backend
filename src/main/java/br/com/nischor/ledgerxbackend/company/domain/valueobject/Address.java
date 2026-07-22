package br.com.nischor.ledgerxbackend.company.domain.valueobject;

public record Address(String street, String number, String city, String state, String zipCode, String country) {
}
