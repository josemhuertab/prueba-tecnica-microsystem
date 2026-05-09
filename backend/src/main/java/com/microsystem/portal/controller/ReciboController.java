package com.microsystem.portal.controller;

import com.microsystem.portal.model.ReciboPago;
import com.microsystem.portal.service.ReciboService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Expongo los endpoints para consultar recibos de pago.
 * Todos requieren autenticación JWT — lo configuré así en SecurityConfig.
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
     * Leo el username del token JWT (que JwtFilter ya dejó en el contexto de seguridad)
     * y retorno los últimos 20 recibos de ese usuario.
     */
    @GetMapping
    public ResponseEntity<List<ReciboPago>> listarRecibos(Authentication auth) {
        String username = auth.getName();
        List<ReciboPago> recibos = reciboService.obtenerUltimos20(username);
        return ResponseEntity.ok(recibos);
    }

    /**
     * GET /api/recibos/{id}
     * Retorno el detalle de un recibo específico.
     * Paso el username del token para que el servicio verifique que el recibo le pertenece.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> detalleRecibo(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();

        try {
            ReciboPago recibo = reciboService.obtenerDetalle(id, username);
            return ResponseEntity.ok(recibo);
        } catch (SecurityException e) {
            // El recibo no existe o no pertenece a este usuario — devuelvo 403
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }
}
