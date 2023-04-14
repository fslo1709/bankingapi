package com.synpulsebankapi.auxiliary;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Class that manages jwt tokens, creating and validating them
 */
@Component
public class JwtTokens {
    /**
     * Normally secrets wouldn't be stored this way, but since the token generation
     * is outside the scope of the requirements, we just stored it in the class
     * for generation and validation
     */
    private String secret = "614E645267556B586E3272357538782F413F4428472B4B6250655368566D5971";
    
    /**
     * Used by a get request to generate a new token with arbitrary information
     * 
     * @return String equal to a valid token to be used by the API entry points
     */
    public String getToken() {
        Key hmacKey = new SecretKeySpec(
            Base64.getDecoder().decode(secret),
            SignatureAlgorithm.HS256.getJcaName());
        
        Instant now = Instant.now();
        String jwtToken = Jwts.builder()
            .claim("name", "Alice")
            .claim("email", "Bob@abc.com")
            .setSubject("Alice")
            .setId(UUID.randomUUID().toString())
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(6l, ChronoUnit.DAYS)))
            .signWith(hmacKey)
            .compact();
        return jwtToken;
    }

    /**
     * Parses the JWT and checks for validity. It checks against the secret
     * to see if it was signed correctly.
     * 
     * @param jwtString The JWT token
     * @return JWS<Claims> object to check for validity by the controller
     */
    public Jws<Claims> parseJwt(String jwtString) {
        Key hmacKey = new SecretKeySpec(
            Base64.getDecoder().decode(secret),
            SignatureAlgorithm.HS256.getJcaName());
        
        try {
            Jws<Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(jwtString);
            return jwt;
        } catch (JwtException | IllegalArgumentException e) {
            throw(e);
        }
    }
}
