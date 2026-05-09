package com.microsystem.portal.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Genera y valida tokens JWT.
 *
 * Un JWT tiene 3 partes separadas por puntos:
 *   header.payload.firma
 * El payload contiene el username y la fecha de expiración.
 * La firma garantiza que nadie puede modificar el token sin la clave secreta.
 */
@Component
public class JwtUtil {

    // Clave secreta leída desde application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Tiempo de vida del token en milisegundos (86400000 = 24 horas)
    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Crea un token firmado con el username y la fecha de expiración
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Extrae el username guardado dentro del token
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Retorna true si el token tiene firma válida y no ha expirado
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            // Cualquier error (firma inválida, token malformado) se trata como inválido
            return false;
        }
    }

    // Parsea el token y extrae su contenido (claims)
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
