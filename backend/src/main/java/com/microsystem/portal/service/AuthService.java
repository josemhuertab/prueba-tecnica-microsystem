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
 * Aquí implementé la lógica de autenticación en tres pasos secuenciales:
 *   1. Valido el formato del username con regex antes de tocar la base de datos
 *   2. Verifico que el usuario exista y que el hash de la contraseña coincida
 *   3. Si todo es correcto, genero y retorno el JWT
 */
@Service
public class AuthService {

    /**
     * Definí este regex según la especificación de la prueba:
     * ^[A-Z]           → debe empezar con mayúscula
     * [a-zA-Z0-9]{11,} → al menos 11 caracteres alfanuméricos en el medio
     * [0-9]{3}$        → debe terminar con exactamente 3 dígitos
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
        // Paso 1: rechazo el username si no cumple el formato antes de consultar la BD
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException(
                "El nombre de usuario no cumple el formato requerido."
            );
        }

        // Paso 2: busco el usuario en la base de datos
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            throw new SecurityException("Credenciales incorrectas.");
        }

        // Paso 3: comparo el hash que genero ahora con el que está almacenado en la BD
        String hashIngresado = generarHash(username, password);
        if (!hashIngresado.equals(usuarioOpt.get().getPasswordHash())) {
            throw new SecurityException("Credenciales incorrectas.");
        }

        // Todo correcto — genero y retorno el JWT
        return jwtUtil.generateToken(username);
    }

    /**
     * Repliqué exactamente la lógica del archivo login_hash_example.py que vino con la prueba:
     * SHA-256("username:password") convertido a string hexadecimal
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
