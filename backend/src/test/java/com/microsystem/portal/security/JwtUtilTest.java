package com.microsystem.portal.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

/**
 * Aquí pruebo JwtUtil de forma completamente aislada.
 *
 * Como JwtUtil no tiene dependencias externas, no necesité Mockito.
 * Creo la instancia directamente y uso ReflectionTestUtils para inyectar
 * los valores de @Value que normalmente Spring inyecta desde application.properties.
 *
 * Lo que verifico en estos tests es el contrato del token:
 *   - Un token generado contiene el username correcto
 *   - Un token válido pasa la validación
 *   - Un token manipulado o vencido es rechazado
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    // Uso los mismos valores que están en application.properties
    private static final String SECRET = "MicrosystemPortalSecretKey2024SuperSegura";
    private static final long EXPIRATION_24H = 86_400_000L; // 24 horas en ms

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Inyecto los campos @Value manualmente para no tener que levantar Spring
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", EXPIRATION_24H);
    }

    // ---------------------------------------------------------------
    // CASO 1: El token generado contiene el username correcto
    // ---------------------------------------------------------------
    @Test
    @DisplayName("generateToken → el token contiene el username")
    void generateToken_contieneUsernameEnElPayload() {
        String username = "JuanPerezDelCampo001";

        String token = jwtUtil.generateToken(username);

        // El token no debe ser nulo ni vacío
        assertThat(token).isNotBlank();

        // Al extraer el username del token debe coincidir exactamente con el que puse
        assertThat(jwtUtil.extractUsername(token)).isEqualTo(username);
    }

    // ---------------------------------------------------------------
    // CASO 2: Un token recién generado es válido
    // ---------------------------------------------------------------
    @Test
    @DisplayName("isTokenValid → token recién generado es válido")
    void isTokenValid_tokenRecienGenerado_esValido() {
        String token = jwtUtil.generateToken("MariaLopezContreras002");

        assertThat(jwtUtil.isTokenValid(token)).isTrue();
    }

    // ---------------------------------------------------------------
    // CASO 3: Un token manipulado (firma inválida) es rechazado
    // ---------------------------------------------------------------
    @Test
    @DisplayName("isTokenValid → token con firma alterada retorna false")
    void isTokenValid_tokenManipulado_retornaFalse() {
        String tokenReal = jwtUtil.generateToken("JuanPerezDelCampo001");

        // Altero el último carácter de la firma para simular que alguien manipuló el token
        String tokenManipulado = tokenReal.substring(0, tokenReal.length() - 1) + "X";

        assertThat(jwtUtil.isTokenValid(tokenManipulado)).isFalse();
    }

    // ---------------------------------------------------------------
    // CASO 4: Un token ya vencido es rechazado
    // ---------------------------------------------------------------
    @Test
    @DisplayName("isTokenValid → token vencido retorna false")
    void isTokenValid_tokenVencido_retornaFalse() {
        // Creo una instancia con expiración de -1ms para que el token nazca ya vencido
        JwtUtil jwtUtilVencido = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtilVencido, "secret", SECRET);
        ReflectionTestUtils.setField(jwtUtilVencido, "expiration", -1L);

        String tokenVencido = jwtUtilVencido.generateToken("JuanPerezDelCampo001");

        assertThat(jwtUtil.isTokenValid(tokenVencido)).isFalse();
    }

    // ---------------------------------------------------------------
    // CASO 5: Un string que no es JWT retorna false sin lanzar excepción
    // ---------------------------------------------------------------
    @Test
    @DisplayName("isTokenValid → string basura retorna false sin lanzar excepción")
    void isTokenValid_stringBasura_retornaFalseSinExcepcion() {
        // Verifico que el método no explote con un input inválido, sino que retorne false limpiamente
        assertThatCode(() -> {
            boolean resultado = jwtUtil.isTokenValid("esto.no.es.un.jwt");
            assertThat(resultado).isFalse();
        }).doesNotThrowAnyException();
    }
}
