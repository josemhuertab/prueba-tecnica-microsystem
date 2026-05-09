package com.microsystem.portal.repository;

import com.microsystem.portal.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Accedo a la tabla de usuarios en SQLite desde aquí.
 * Spring Data JPA genera la implementación automáticamente en tiempo de ejecución, no tengo que escribirla yo.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    // Spring traduce este nombre de método a: SELECT * FROM usuarios WHERE username = ?
    Optional<Usuario> findByUsername(String username);
}
