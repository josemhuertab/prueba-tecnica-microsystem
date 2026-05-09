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
 * Aquí centralizo toda la lógica de generación y validación de tokens JWT.
 *
 * Un JWT tiene 3 partes separadas por puntos: header.payload.firma
 * En el payload guardo el username y la fecha de expiración.
 * La firma es lo que garantiza que nadie puede alterar el token sin conocer la clave secreta.
 */
@Component
public class JwtUtil {

    // Leo la clave secreta desde application.properties para no hardcodearla aquí
    @Value("${jwt.secret}")
    private String secret;

    // Tiempo de vida del token en milisegundos — uso 86400000 que equivale a 24 horas
    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Genero el token firmado con el username como subject y la fecha de expiración calculada
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Extraigo el username que guardé dentro del payload del token
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Retorno true solo si el token tiene firma válida y todavía no ha expirado
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            // Si la firma es inválida o el token está malformado, lo trato directamente como inválido
            return false;
        }
    }

    // Parseo el token y extraigo su contenido (claims) verificando la firma al mismo tiempo
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
