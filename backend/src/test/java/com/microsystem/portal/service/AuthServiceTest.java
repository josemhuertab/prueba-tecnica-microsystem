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
 * Aquí pruebo la lógica de autenticación de AuthService de forma aislada.
 *
 * Usé @ExtendWith(MockitoExtension.class) para que Mockito inyecte los mocks
 * automáticamente sin necesidad de levantar el contexto de Spring.
 * Esto hace que los tests corran en milisegundos.
 *
 * @Mock        → creo un objeto falso que puedo controlar en cada test
 * @InjectMocks → creo la clase real e inyecto los mocks en su constructor
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    // Datos de prueba que reutilizo en varios tests
    private static final String USERNAME_VALIDO = "JuanPerezDelCampo001";
    private static final String PASSWORD = "password123";

    private Usuario usuarioDePrueba;

    @BeforeEach
    void setUp() throws Exception {
        // Genero el hash exactamente igual que lo hace AuthService internamente:
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
        // Le digo al mock que "encuentre" al usuario y que genere un token de prueba
        when(usuarioRepository.findByUsername(USERNAME_VALIDO))
                .thenReturn(Optional.of(usuarioDePrueba));
        when(jwtUtil.generateToken(USERNAME_VALIDO))
                .thenReturn("token.jwt.falso");

        // Act
        String token = authService.login(USERNAME_VALIDO, PASSWORD);

        // Assert
        assertThat(token).isEqualTo("token.jwt.falso");

        // Verifico que sí se consultó el repositorio y se generó el token
        verify(usuarioRepository).findByUsername(USERNAME_VALIDO);
        verify(jwtUtil).generateToken(USERNAME_VALIDO);
    }

    // ---------------------------------------------------------------
    // CASO 2: Username con formato inválido
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Username sin mayúscula inicial → lanza IllegalArgumentException")
    void login_usernameFormatoInvalido_lanzaExcepcion() {
        // "juanPerezDelCampo001" empieza con minúscula, no cumple el regex que definí
        assertThatThrownBy(() -> authService.login("juanPerezDelCampo001", PASSWORD))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("formato requerido");

        // Si el formato ya es inválido, no debería llegar a consultar la base de datos
        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Username muy corto (menos de 15 chars) → lanza IllegalArgumentException")
    void login_usernameMuyCorto_lanzaExcepcion() {
        // "JuanP001" tiene solo 8 caracteres, el mínimo que definí es 15
        assertThatThrownBy(() -> authService.login("JuanP001", PASSWORD))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(usuarioRepository);
    }

    @Test
    @DisplayName("Username sin 3 dígitos al final → lanza IllegalArgumentException")
    void login_usernameSinDigitosAlFinal_lanzaExcepcion() {
        // "JuanPerezDelCampoXYZ" termina en letras, no en los 3 dígitos que exijo
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
        // El mock devuelve vacío para simular que el usuario no está registrado
        when(usuarioRepository.findByUsername(USERNAME_VALIDO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(USERNAME_VALIDO, PASSWORD))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Credenciales incorrectas");

        // Si el usuario no existe, no debo generar ningún token
        verifyNoInteractions(jwtUtil);
    }

    // ---------------------------------------------------------------
    // CASO 4: Contraseña incorrecta (hash no coincide)
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Contraseña incorrecta → lanza SecurityException")
    void login_passwordIncorrecta_lanzaSecurityException() {
        // El usuario existe en la BD pero envío una contraseña diferente
        when(usuarioRepository.findByUsername(USERNAME_VALIDO))
                .thenReturn(Optional.of(usuarioDePrueba));

        assertThatThrownBy(() -> authService.login(USERNAME_VALIDO, "contraseñaEquivocada"))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Credenciales incorrectas");

        // Con contraseña incorrecta tampoco debo generar token
        verifyNoInteractions(jwtUtil);
    }
}
