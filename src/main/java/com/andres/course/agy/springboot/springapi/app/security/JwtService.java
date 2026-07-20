package com.andres.course.agy.springboot.springapi.app.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.SecretKey;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

@Component
public class JwtService {

    @Value("${jwt.cookie.secure}")
    private boolean cookieSecure;

    @Value("${jwt.cookie.same-site}")
    private String cookieSameSite;

    // A secure base64-encoded or simple plain secret string (at least 256 bits / 32 bytes)
    private final String secretString = "v9y$B&E)H@McQfTjWnZr4u7x!z%C*F-JaNdRgUkXp2s5v8y/B?D(G+KbPeShVmYq";
    
    // Cookie name to store JWT
    private final String cookieName = "jwt_token";
    
    // Duration of token validity (1 day in milliseconds)
    private final long expirationMs = 86400000;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretString.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate a signed JWT token for the specified user email.
     *
     * @param email user email address
     * @return signed JWT token string
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract email (subject) from a given JWT token.
     *
     * @param token JWT token string
     * @return email subject
     */
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Validate the token email matches and the token is not expired.
     *
     * @param token JWT token string
     * @param email expected email
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(String token, String email) {
        try {
            String tokenEmail = extractEmail(token);
            return tokenEmail.equals(email) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Create an HttpOnly response cookie to hold the JWT token.
     *
     * @param jwtToken signed JWT token
     * @return ResponseCookie instance
     */
    public ResponseCookie createJwtCookie(String jwtToken) {
        return ResponseCookie.from(cookieName, jwtToken)
                .path("/")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .maxAge(expirationMs / 1000)
                .build();
    }

    /**
     * Create a blank, expired HttpOnly response cookie to clear JWT on logout.
     *
     * @return ResponseCookie instance
     */
    public ResponseCookie createCleanJwtCookie() {
        return ResponseCookie.from(cookieName, "")
                .path("/")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .maxAge(0) // Expire immediately
                .build();
    }

    /**
     * Extract JWT token from the HttpOnly cookie of a request.
     *
     * @param request servlet request
     * @return JWT token string, or null if not found
     */
    public String extractJwtFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
