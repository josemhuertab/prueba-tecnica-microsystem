package com.microsystem.portal.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represento un recibo de pago mensual en la base de datos.
 * Guardé todos los montos como enteros en pesos chilenos (sin decimales).
 *
 * Si necesito agregar un campo nuevo, lo declaro aquí con su @Column
 * y el frontend lo recibirá automáticamente en el JSON de respuesta.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "recibos_pago")
public class ReciboPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Vinculo el recibo con su dueño — lo uso para verificar el acceso en cada consulta
    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "nro_recibo")
    private Integer nroRecibo;

    @Column(name = "fecha_pago")
    private String fechaPago;

    // Guardo el período en formato MM-YYYY, por ejemplo "04-2025"
    @Column(name = "periodo")
    private String periodo;

    // --- Haberes ---
    @Column(name = "sueldo_base")
    private Long sueldoBase;

    @Column(name = "bono_produccion")
    private Long bonoProduccion;

    // --- Descuentos ---
    @Column(name = "descuento_salud")
    private Long descuentoSalud;

    @Column(name = "descuento_afp")
    private Long descuentoAfp;

    @Column(name = "otros_descuentos")
    private Long otrosDescuentos;

    // El resultado final: sueldoBase + bonos - descuentos
    @Column(name = "sueldo_liquido")
    private Long sueldoLiquido;

    @Column(name = "detalle")
    private String detalle;
}
