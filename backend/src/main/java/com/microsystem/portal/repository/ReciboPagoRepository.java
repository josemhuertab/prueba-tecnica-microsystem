package com.microsystem.portal.repository;

import com.microsystem.portal.model.ReciboPago;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Accedo a la tabla de recibos en SQLite desde aquí.
 */
@Repository
public interface ReciboPagoRepository extends JpaRepository<ReciboPago, Long> {

    /**
     * Traigo los recibos de un usuario ordenados del más reciente al más antiguo.
     * Usé Pageable en lugar de LIMIT porque es más compatible con todos los dialectos JPA.
     * El límite de 20 lo aplico desde el servicio con PageRequest.of(0, 20).
     */
    @Query("SELECT r FROM ReciboPago r WHERE r.username = :username ORDER BY r.fechaPago DESC")
    List<ReciboPago> findTop20ByUsername(@Param("username") String username, Pageable pageable);

    /**
     * Busco un recibo por ID verificando al mismo tiempo que pertenezca al usuario indicado.
     * Así evito que alguien pueda ver recibos de otro usuario aunque conozca el ID.
     */
    Optional<ReciboPago> findByIdAndUsername(Long id, String username);
}
