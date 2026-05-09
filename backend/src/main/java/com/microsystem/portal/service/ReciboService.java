package com.microsystem.portal.service;

import com.microsystem.portal.model.ReciboPago;
import com.microsystem.portal.repository.ReciboPagoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Aquí centralizo la lógica de negocio para consultar recibos de pago.
 * En cada método verifico que el recibo pertenezca al usuario autenticado
 * para evitar que alguien pueda ver datos de otro.
 */
@Service
public class ReciboService {

    private final ReciboPagoRepository reciboPagoRepository;

    public ReciboService(ReciboPagoRepository reciboPagoRepository) {
        this.reciboPagoRepository = reciboPagoRepository;
    }

    // Retorno los últimos 20 recibos del usuario, ordenados del más reciente al más antiguo
    public List<ReciboPago> obtenerUltimos20(String username) {
        return reciboPagoRepository.findTop20ByUsername(username, PageRequest.of(0, 20));
    }

    // Retorno el detalle de un recibo solo si pertenece al usuario que lo pide
    // Si el ID existe pero es de otro usuario, lanzo SecurityException igual que si no existiera
    public ReciboPago obtenerDetalle(Long id, String username) {
        return reciboPagoRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new SecurityException("Recibo no encontrado o no autorizado."));
    }
}
