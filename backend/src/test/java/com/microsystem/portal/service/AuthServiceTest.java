package com.microsystem.portal.service;

import com.microsystem.portal.model.Usuario;
import com.microsystem.portal.repository.UsuarioRepository;
import com.microsystem.portal.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuthService.
 *
 * Usamos @ExtendWith(MockitoExtension.class) para que Mockito
 * inyecte los mocks automáticamente sin levantar el contexto de Spring.
 * Esto hace los tests muy rápidos (milisegundos).
 *
 * @Mock      → crea un objeto falso que podemos controlar
 * @InjectMocks → crea la clase real e inyecta los mocks en su constructor
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    // Usuario de prueba que usaremos en varios tests
    private static final String USERNAME_VALIDO = "JuanPerezDelCampo001";
    private static final String PASSWORD = "password123";

    private Usuario usuarioDePrueba;

    @BeforeEach
    void setUp() throws Exception {
        // Creamos el hash exactamente igual que lo hace AuthService:
        // SHA-256("username:password") → hex string
        String combined = USERNAME_VALIDO + ":" + PASSWORD;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
        String hash = HexFormat.of().formatHex(hashBytes);

        usuarioDePrueba = new Usuario();
        usuarioDePrueba.setUsername(USERNAME_VALIDO);
        usuarioDePrueba.setPasswordHash(hash);
    }

    // ---------------------------------------------------------------
    // CASO 1: Login exitoso con credenciales correctas
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Login exitoso → retorna token JWT")
    void login_credencialesCorrectas_retornaToken() {
        // Arrange: el repositorio "encuentra" al usuario y JwtUtil genera un token falso
        when(usuarioRepository.findByUsername(USERNAME_VALIDO))
                .thenReturn(Optional.of(usuarioDePrueba));
        when(jwtUtil.generateToken(USERNAME_VALIDO))
                .thenReturn("token.jwt.falso");

        // Act
        String token = authService.login(USERNAME_VALIDO, PASSWORD);

        // Assert
        assertThat(token).isEqualTo("token.jwt.falso");

        // Verificamos que sí se llamó al repositorio y al generador de token
        verify(usuarioRepository).findByUsername(USERNAME_VALIDO);
        verify(jwtUtil).generateToken(USERNAME_VALIDO);
    }

    // ---------------------------------------------------------------
    // CASO 2: Username con formato inválido
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Username sin mayúscula inicial → lanza IllegalArgumentException")
    void login_usernameFormatoInvalido_lanzaExcepcion() {
        // "juanPerezDelCampo001" empieza con minúscula → no cumple el regex
        assertThatThrownBy(() -> authService.login("juanPerezDelCampo001", PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("formato requerido");

        // El repositorio nunca debería consultarse si el formato ya es inválido
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Username muy corto (menos de 15 chars) → lanza IllegalArgumentException")
    void login_usernameMuyCorto_lanzaExcepcion() {
        // "JuanP001" tiene solo 8 caracteres, mínimo es 15
        assertThatThrownBy(() -> authService.login("JuanP001", PASSWORD))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Username sin 3 dígitos al final → lanza IllegalArgumentException")
    void login_usernameSinDigitosAlFinal_lanzaExcepcion() {
        // "JuanPerezDelCampoXYZ" termina en letras, no en 3 dígitos
        assertThatThrownBy(() -> authService.login("JuanPerezDelCampoXYZ", PASSWORD))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(usuarioRepository);
    }

    // ---------------------------------------------------------------
    // CASO 3: Usuario no existe en la base de datos
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Usuario no registrado → lanza SecurityException")
    void login_usuarioNoExiste_lanzaSecurityException() {
        // El repositorio devuelve vacío → usuario no encontrado
        when(usuarioRepository.findByUsername(USERNAME_VALIDO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(USERNAME_VALIDO, PASSWORD))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Credenciales incorrectas");

        // Si el usuario no existe, nunca debemos generar un token
        verifyNoInteractions(jwtUtil);
    }

    // ---------------------------------------------------------------
    // CASO 4: Contraseña incorrecta (hash no coincide)
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Contraseña incorrecta → lanza SecurityException")
    void login_passwordIncorrecta_lanzaSecurityException() {
        // El usuario existe pero la contraseña enviada es diferente
        when(usuarioRepository.findByUsername(USERNAME_VALIDO))
                .thenReturn(Optional.of(usuarioDePrueba));

        assertThatThrownBy(() -> authService.login(USERNAME_VALIDO, "contraseñaEquivocada"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Credenciales incorrectas");

        verifyNoInteractions(jwtUtil);
    }
}
