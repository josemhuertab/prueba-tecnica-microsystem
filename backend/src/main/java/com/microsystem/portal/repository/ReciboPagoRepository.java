package com.microsystem.portal.repository;

import com.microsystem.portal.model.ReciboPago;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReciboPagoRepository extends JpaRepository<ReciboPago, Long> {

    // Últimos 20 recibos del usuario, ordenados por fecha descendente
    // Usamos Pageable para que sea compatible con cualquier dialecto JPA
    @Query("SELECT r FROM ReciboPago r WHERE r.username = :username ORDER BY r.fechaPago DESC")
    List<ReciboPago> findTop20ByUsername(@Param("username") String username, Pageable pageable);

    // Busca un recibo específico verificando que pertenezca al usuario autenticado
    Optional<ReciboPago> findByIdAndUsername(Long id, String username);
}
