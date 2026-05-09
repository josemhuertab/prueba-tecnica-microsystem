package com.microsystem.portal.service;

import com.microsystem.portal.model.Usuario;
import com.microsystem.portal.repository.UsuarioRepository;
import com.microsystem.portal.security.JwtUtil;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Lógica de autenticación: validación de formato, hash y generación de token.
 */
@Service
public class AuthService {

    // Regex del username: UpperCamelCase, mínimo 15 chars, empieza con letra, termina con 3 dígitos
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[A-Z][a-zA-Z0-9]{11,}[0-9]{3}$");

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Intenta autenticar al usuario. Retorna el JWT si las credenciales son válidas.
     * Lanza IllegalArgumentException si el formato del username no es correcto.
     * Lanza SecurityException si las credenciales no coinciden.
     */
    public String login(String username, String password) {
        // Primero validamos el formato del username con regex
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException(
                "El nombre de usuario no cumple el formato requerido. " +
                "Debe ser UpperCamelCase, mínimo 15 caracteres, comenzar con letra y terminar con 3 dígitos."
            );
        }

        // Buscamos el usuario en la base de datos
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new SecurityException("Credenciales incorrectas.");
        }

        // Generamos el hash de la contraseña ingresada y comparamos
        String hashIngresado = generarHash(username, password);
        if (!hashIngresado.equals(usuarioOpt.get().getPasswordHash())) {
            throw new SecurityException("Credenciales incorrectas.");
        }

        return jwtUtil.generateToken(username);
    }

    /**
     * Genera el hash SHA-256 combinando username:password,
     * igual que en login_hash_example.py.
     */
    private String generarHash(String username, String password) {
        try {
            String combined = username + ":" + password;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash SHA-256", e);
        }
    }
}
