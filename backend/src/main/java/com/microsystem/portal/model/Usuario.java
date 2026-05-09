package com.microsystem.portal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represento a un colaborador en la base de datos.
 * Decidí nunca guardar la contraseña en texto plano — solo almaceno el hash SHA-256
 * generado como SHA-256("username:password").
 */
@Data               // Le pido a Lombok que genere getters, setters, equals y toString por mí
@NoArgsConstructor  // JPA necesita un constructor vacío — Lombok lo genera con esta anotación
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
