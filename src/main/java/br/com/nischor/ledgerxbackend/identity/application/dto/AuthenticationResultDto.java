package br.com.nischor.ledgerxbackend.identity.application.dto;

public record AuthenticationResultDto(String accessToken, String tokenType, long expiresInSeconds) {
}
