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
 * Maneja la lógica de autenticación en tres pasos:
 *   1. Valida el formato del username con regex
 *   2. Verifica las credenciales contra el hash almacenado
 *   3. Genera y retorna el JWT si todo es correcto
 */
@Service
public class AuthService {

    /**
     * Regex del username según especificación de la prueba:
     * ^[A-Z]        → empieza con mayúscula
     * [a-zA-Z0-9]{11,} → al menos 11 caracteres alfanuméricos en el medio
     * [0-9]{3}$     → termina con exactamente 3 dígitos
     * Total mínimo: 1 + 11 + 3 = 15 caracteres
     */
    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[A-Z][a-zA-Z0-9]{11,}[0-9]{3}$");

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository, JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
    }

    public String login(String username, String password) {
        // Paso 1: formato del username
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException(
                "El nombre de usuario no cumple el formato requerido."
            );
        }

        // Paso 2: el usuario debe existir en la base de datos
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new SecurityException("Credenciales incorrectas.");
        }

        // Paso 3: comparamos el hash generado con el almacenado
        String hashIngresado = generarHash(username, password);
        if (!hashIngresado.equals(usuarioOpt.get().getPasswordHash())) {
            throw new SecurityException("Credenciales incorrectas.");
        }

        // Todo correcto — generamos y retornamos el JWT
        return jwtUtil.generateToken(username);
    }

    /**
     * Replica exactamente la lógica de login_hash_example.py:
     * SHA-256("username:password") → string hexadecimal
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
