package com.microsystem.portal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa un colaborador en la base de datos.
 * La contraseña nunca se guarda en texto plano — solo su hash SHA-256
 * generado como SHA-256("username:password").
 */
@Data               // Lombok genera getters, setters, equals y toString automáticamente
@NoArgsConstructor  // Lombok genera el constructor vacío que JPA necesita
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "primer_apellido")
    private String primerApellido;

    @Column(name = "segundo_apellido")
    private String segundoApellido;

    @Column(name = "fecha_nacimiento")
    private String fechaNacimiento;
}
