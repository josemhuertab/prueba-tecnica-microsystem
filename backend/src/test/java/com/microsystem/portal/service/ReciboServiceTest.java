package com.microsystem.portal.service;

import com.microsystem.portal.model.ReciboPago;
import com.microsystem.portal.repository.ReciboPagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Aquí pruebo la lógica de consulta de recibos y, lo más importante,
 * el control de acceso por propietario.
 *
 * El test de ownership es el más crítico de este archivo:
 * un usuario NO debe poder ver recibos de otro aunque conozca el ID.
 * Esto se conoce como IDOR (Insecure Direct Object Reference) y es
 * uno de los riesgos más comunes en APIs REST.
 */
@ExtendWith(MockitoExtension.class)
class ReciboServiceTest {

    @Mock
    private ReciboPagoRepository reciboPagoRepository;

    @InjectMocks
    private ReciboService reciboService;

    private ReciboPago reciboDePrueba;

    private static final String USUARIO_JUAN   = "JuanPerezDelCampo001";
    private static final String USUARIO_MARIA  = "MariaLopezContreras002";

    @BeforeEach
    void setUp() {
        // Preparo un recibo de prueba que pertenece a Juan
        reciboDePrueba = new ReciboPago();
        reciboDePrueba.setId(1L);
        reciboDePrueba.setUsername(USUARIO_JUAN);
        reciboDePrueba.setNroRecibo(10);
        reciboDePrueba.setPeriodo("04-2025");
        reciboDePrueba.setSueldoBase(1_000_000L);
        reciboDePrueba.setBonoProduccion(100_000L);
        reciboDePrueba.setDescuentoSalud(70_000L);
        reciboDePrueba.setDescuentoAfp(100_000L);
        reciboDePrueba.setOtrosDescuentos(0L);
        reciboDePrueba.setSueldoLiquido(930_000L);
    }

    // ---------------------------------------------------------------
    // CASO 1: El dueño del recibo puede verlo sin problemas
    // ---------------------------------------------------------------
    @Test
    @DisplayName("obtenerDetalle → el dueño del recibo recibe el detalle correctamente")
    void obtenerDetalle_usuarioCorrecto_retornaRecibo() {
        // Le digo al mock que devuelva el recibo cuando Juan consulta el id=1
        when(reciboPagoRepository.findByIdAndUsername(1L, USUARIO_JUAN))
                .thenReturn(Optional.of(reciboDePrueba));

        // Act
        ReciboPago resultado = reciboService.obtenerDetalle(1L, USUARIO_JUAN);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getUsername()).isEqualTo(USUARIO_JUAN);
        assertThat(resultado.getPeriodo()).isEqualTo("04-2025");
        assertThat(resultado.getSueldoLiquido()).isEqualTo(930_000L);
    }

    // ---------------------------------------------------------------
    // CASO 2: Otro usuario intenta acceder al recibo de Juan (IDOR)
    // ---------------------------------------------------------------
    @Test
    @DisplayName("obtenerDetalle → usuario diferente al dueño lanza SecurityException (IDOR)")
    void obtenerDetalle_usuarioNoPropietario_lanzaSecurityException() {
        // El repositorio no encuentra el recibo id=1 para María porque es de Juan
        when(reciboPagoRepository.findByIdAndUsername(1L, USUARIO_MARIA))
                .thenReturn(Optional.empty());

        // María intenta ver el recibo de Juan — verifico que sea bloqueada
        assertThatThrownBy(() -> reciboService.obtenerDetalle(1L, USUARIO_MARIA))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("no autorizado");
    }

    // ---------------------------------------------------------------
    // CASO 3: obtenerUltimos20 retorna solo los recibos del usuario
    // ---------------------------------------------------------------
    @Test
    @DisplayName("obtenerUltimos20 → retorna la lista de recibos del usuario")
    void obtenerUltimos20_retornaRecibosDelUsuario() {
        // Preparo 3 recibos de prueba para Juan
        List<ReciboPago> recibosEsperados = List.of(
                crearRecibo(1L, USUARIO_JUAN, "04-2025"),
                crearRecibo(2L, USUARIO_JUAN, "03-2025"),
                crearRecibo(3L, USUARIO_JUAN, "02-2025")
        );

        // El mock devuelve esos 3 recibos cuando consulto por Juan
        when(reciboPagoRepository.findTop20ByUsername(
                eq(USUARIO_JUAN),
                eq(PageRequest.of(0, 20))
        )).thenReturn(recibosEsperados);

        // Act
        List<ReciboPago> resultado = reciboService.obtenerUltimos20(USUARIO_JUAN);

        // Assert
        assertThat(resultado).hasSize(3);
        // Me aseguro de que todos los recibos pertenezcan a Juan y ninguno a otro usuario
        assertThat(resultado).allMatch(r -> r.getUsername().equals(USUARIO_JUAN));
    }

    // ---------------------------------------------------------------
    // CASO 4: Si el usuario no tiene recibos, retorna lista vacía
    // ---------------------------------------------------------------
    @Test
    @DisplayName("obtenerUltimos20 → usuario sin recibos retorna lista vacía")
    void obtenerUltimos20_sinRecibos_retornaListaVacia() {
        when(reciboPagoRepository.findTop20ByUsername(
                eq(USUARIO_MARIA),
                eq(PageRequest.of(0, 20))
        )).thenReturn(List.of());

        List<ReciboPago> resultado = reciboService.obtenerUltimos20(USUARIO_MARIA);

        assertThat(resultado).isEmpty();
    }

    // ---------------------------------------------------------------
    // Método auxiliar que uso para crear recibos de prueba rápidamente
    // ---------------------------------------------------------------
    private ReciboPago crearRecibo(Long id, String username, String periodo) {
        ReciboPago r = new ReciboPago();
        r.setId(id);
        r.setUsername(username);
        r.setPeriodo(periodo);
        r.setSueldoLiquido(900_000L);
        return r;
    }
}
