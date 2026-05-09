package com.microsystem.portal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un recibo de pago mensual de un colaborador.
 * Los montos están en pesos sin decimales.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "recibos_pago")
public class ReciboPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario al que pertenece este recibo
    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "nro_recibo")
    private Integer nroRecibo;

    @Column(name = "fecha_pago")
    private String fechaPago;

    // Periodo en formato MM-YYYY
    @Column(name = "periodo")
    private String periodo;

    @Column(name = "sueldo_base")
    private Long sueldoBase;

    @Column(name = "bono_produccion")
    private Long bonoProduccion;

    @Column(name = "descuento_salud")
    private Long descuentoSalud;

    @Column(name = "descuento_afp")
    private Long descuentoAfp;

    @Column(name = "otros_descuentos")
    private Long otrosDescuentos;

    @Column(name = "sueldo_liquido")
    private Long sueldoLiquido;

    @Column(name = "detalle")
    private String detalle;
}
