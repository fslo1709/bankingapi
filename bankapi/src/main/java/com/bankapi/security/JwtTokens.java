package com.bankapi.security;

import java.security.Key;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Class that manages jwt tokens, creating and validating them
 */
@Service
public class JwtTokens {
    /**
     * Normally secrets wouldn't be stored this way, but since the token generation
     * is outside the scope of the requirements, we just store a random key here
     */
    private String secret = "614E645267556B586E3272357538782F413F4428472B4B6250655368566D5971";

    /**
     * Parses the JWT and checks for validity. It checks against the secret
     * to see if it was signed correctly.
     * 
     * @param jwtString The JWT token
     * @return true if it is a valid token
     */
    public String parseJwt(String jwtString) throws Exception {
        Key hmacKey = new SecretKeySpec(
            Base64.getDecoder().decode(secret),
            SignatureAlgorithm.HS256.getJcaName());
        
        try {
            String username = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(jwtString)
                .getBody()
                .getSubject();
            return username;
        } catch (JwtException | IllegalArgumentException e) {
            throw(e);
        }
    }
}
