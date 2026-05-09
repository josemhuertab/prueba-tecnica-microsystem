package com.microsystem.portal.controller;

import com.microsystem.portal.model.ReciboPago;
import com.microsystem.portal.service.ReciboService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Endpoints para consultar recibos de pago.
 * Todos requieren autenticación JWT (configurado en SecurityConfig).
 */
@RestController
@RequestMapping("/api/recibos")
public class ReciboController {

    private final ReciboService reciboService;

    public ReciboController(ReciboService reciboService) {
        this.reciboService = reciboService;
    }

    /**
     * GET /api/recibos
     * Retorna los últimos 20 recibos del usuario autenticado.
     */
    @GetMapping
    public ResponseEntity<List<ReciboPago>> listarRecibos(Authentication auth) {
        String username = auth.getName();
        List<ReciboPago> recibos = reciboService.obtenerUltimos20(username);
        return ResponseEntity.ok(recibos);
    }

    /**
     * GET /api/recibos/{id}
     * Retorna el detalle de un recibo específico.
     * Verifica que el recibo pertenezca al usuario autenticado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> detalleRecibo(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();

        try {
            ReciboPago recibo = reciboService.obtenerDetalle(id, username);
            return ResponseEntity.ok(recibo);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }
}
