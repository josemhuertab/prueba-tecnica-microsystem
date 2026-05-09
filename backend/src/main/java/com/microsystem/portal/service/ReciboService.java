package com.microsystem.portal.service;

import com.microsystem.portal.model.ReciboPago;
import com.microsystem.portal.repository.ReciboPagoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Lógica de negocio para consultar recibos de pago.
 * Siempre verifica que el recibo pertenezca al usuario autenticado.
 */
@Service
public class ReciboService {

    private final ReciboPagoRepository reciboPagoRepository;

    public ReciboService(ReciboPagoRepository reciboPagoRepository) {
        this.reciboPagoRepository = reciboPagoRepository;
    }

    // Retorna los últimos 20 recibos del usuario
    public List<ReciboPago> obtenerUltimos20(String username) {
        return reciboPagoRepository.findTop20ByUsername(username, PageRequest.of(0, 20));
    }

    // Retorna el detalle de un recibo, validando que pertenezca al usuario
    public ReciboPago obtenerDetalle(Long id, String username) {
        return reciboPagoRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new SecurityException("Recibo no encontrado o no autorizado."));
    }
}
