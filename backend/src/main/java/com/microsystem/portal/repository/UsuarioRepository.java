package com.microsystem.portal.repository;

import com.microsystem.portal.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Acceso a la tabla de usuarios en SQLite.
 * Spring Data JPA genera la implementación automáticamente en tiempo de ejecución.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    // Spring traduce este nombre de método a: SELECT * FROM usuarios WHERE username = ?
    Optional<Usuario> findByUsername(String username);
}
