package br.com.nischor.ledgerxbackend.company.domain.valueobject;

/**
 * Immutable value object representing a postal address.
 *
 * @param street street name
 * @param number street number
 * @param city city name
 * @param state state or federative unit (UF)
 * @param zipCode postal code (CEP)
 * @param country country name
 */
public record Address(String street, String number, String city, String state, String zipCode, String country) {
}
