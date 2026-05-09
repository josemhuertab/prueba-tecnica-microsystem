package com.microsystem.portal.controller;

import com.microsystem.portal.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints de autenticación: login y logout.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/login
     * Recibe username y password, retorna JWT si las credenciales son válidas.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Usuario y contraseña son requeridos."));
        }

        try {
            String token = authService.login(username, password);
            return ResponseEntity.ok(Map.of("token", token, "username", username));

        } catch (IllegalArgumentException e) {
            // Error de formato del username
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));

        } catch (SecurityException e) {
            // Credenciales incorrectas
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/auth/logout
     * El logout en JWT es stateless: el frontend elimina el token.
     * Este endpoint confirma la operación.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Sesión cerrada correctamente."));
    }
}
