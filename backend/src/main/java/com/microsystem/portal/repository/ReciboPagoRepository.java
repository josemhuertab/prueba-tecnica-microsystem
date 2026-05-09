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
 * Acceso a la tabla de recibos en SQLite.
 */
@Repository
public interface ReciboPagoRepository extends JpaRepository<ReciboPago, Long> {

    /**
     * Trae los recibos de un usuario ordenados del más reciente al más antiguo.
     * Usamos Pageable en lugar de LIMIT para compatibilidad con todos los dialectos JPA.
     * El límite de 20 se aplica desde el servicio con PageRequest.of(0, 20).
     */
    @Query("SELECT r FROM ReciboPago r WHERE r.username = :username ORDER BY r.fechaPago DESC")
    List<ReciboPago> findTop20ByUsername(@Param("username") String username, Pageable pageable);

    /**
     * Busca un recibo por ID y verifica que pertenezca al usuario indicado.
     * Esto evita que un usuario pueda ver recibos de otro aunque conozca el ID.
     */
    Optional<ReciboPago> findByIdAndUsername(Long id, String username);
}
