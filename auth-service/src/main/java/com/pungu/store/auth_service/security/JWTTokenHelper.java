package com.pungu.store.auth_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for generating and validating JWT tokens.
 */
@Component
public class JWTTokenHelper {

    // Token validity duration
    @Value("${jwt.token.validity.hours}")
    private int validityInHours;
    // Injected secret key from application.properties
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails Spring Security user details
     * @return Signed JWT token as a string
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + getJwtTokenValidity().toMillis()))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts username from the JWT token.
     *
     * @param token JWT token string
     * @return Username (subject) embedded in token
     */
    public String getUserNameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Retrieves roles stored in the token claims.
     *
     * @param token JWT token
     * @return List of roles as strings
     */
    public List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("roles", List.class);
    }

    /**
     * Validates token against user details.
     *
     * @param token       JWT token string
     * @param userDetails User details for comparison
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUserNameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Checks if the token has expired.
     *
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Determines if a token is eligible to be refreshed.
     *
     * @param token JWT token
     * @return true if it can be refreshed
     */
    public boolean canTokenBeRefreshed(String token) {
        return !isTokenExpired(token);
    }

    /**
     * Extracts expiration date from token.
     *
     * @param token JWT token
     * @return Expiration date
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Generic claim extractor using a resolver function.
     *
     * @param token          JWT token
     * @param claimsResolver Function to extract specific claim
     * @param <T>            Type of the claim
     * @return Claim value
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses and returns all claims from token.
     *
     * @param token JWT token
     * @return Claims object
     */
    private Claims getAllClaimsFromToken(String token) {
//        return Jwts.parser()
//                .setSigningKey(getSigningKey())
//                .parseClaimsJws(token)
//                .getBody();

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Returns a secure signing key from the secret.
     *
     * @return Signing key
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
    }

    /**
     * Returns token validity hours.
     *
     * @return token validity hours
     */
    public Duration getJwtTokenValidity() {
        return Duration.ofHours(validityInHours);
    }
}