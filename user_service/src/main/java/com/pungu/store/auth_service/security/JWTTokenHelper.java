package com.pungu.store.auth_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTTokenHelper {
    public static final long JWT_TOKEN_VALIDITY = 30 * 60 * 60;
    private final String secret = "jwtTokenKey";

    //generate token for user
    // While creating token
    // 1. Define claims of the token, like issuer, expiration, subject and id
    // 2. sign the JWT using the HS512 algorithm and secret key
    // 3. According to JWS compact serialization
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims) // Sets custom claims (payload data)
                .setSubject(userDetails.getUsername()) // Sets the subject (usually the username or user ID)
                .setIssuedAt(new Date(System.currentTimeMillis())) // Sets the issued time
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)) // Sets the expiry time
                .signWith(SignatureAlgorithm.HS256, secret) // Signs the token using HS256 and a secret key
                .compact(); // Builds and returns the final JWT string
    }

    //retrieve username from jwt token..
    public String getUserNameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //Validate token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String tokenUsername = getUserNameFromToken(token);
        return (tokenUsername.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    //check if the token has expired...
    public boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // for retrieving any information from token we need secret key...
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}
