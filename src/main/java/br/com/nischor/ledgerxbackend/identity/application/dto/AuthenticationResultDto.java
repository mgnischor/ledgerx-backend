package br.com.nischor.ledgerxbackend.identity.application.dto;

/**
 * Result of a successful authentication, carrying the issued access token and the information a
 * client needs to use it as a Bearer credential.
 *
 * @param accessToken       the signed JWT access token.
 * @param tokenType         the HTTP authorization scheme to use with the token (e.g. {@code "Bearer"}).
 * @param expiresInSeconds  the token's time to live, in seconds, from the moment it was issued.
 */
public record AuthenticationResultDto(String accessToken, String tokenType, long expiresInSeconds) {
}
