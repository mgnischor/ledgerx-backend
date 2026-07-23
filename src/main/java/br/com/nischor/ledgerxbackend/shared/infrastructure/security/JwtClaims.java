package br.com.nischor.ledgerxbackend.shared.infrastructure.security;

import java.util.List;

/** Decoded, signature-verified claims of a JWT access token. */
public record JwtClaims(String subject, List<String> roles) {
}
