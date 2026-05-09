package com.microsystem.portal.service;

import com.microsystem.portal.model.ReciboPago;
import com.microsystem.portal.model.Usuario;
import com.microsystem.portal.repository.ReciboPagoRepository;
import com.microsystem.portal.repository.UsuarioRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Carga los datos de los CSV a SQLite al iniciar la aplicación.
 * Solo inserta si la tabla está vacía, para no duplicar datos en reinicios.
 */
@Service
public class DataLoaderService {

    private final UsuarioRepository usuarioRepository;
    private final ReciboPagoRepository reciboPagoRepository;

    public DataLoaderService(UsuarioRepository usuarioRepository,
                             ReciboPagoRepository reciboPagoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.reciboPagoRepository = reciboPagoRepository;
    }

    @PostConstruct
    public void cargarDatos() {
        cargarUsuarios();
        cargarRecibos();
    }

    private void cargarUsuarios() {
        // Si ya hay datos, no volvemos a cargar
        if (usuarioRepository.count() > 0) return;

        try (CSVReader reader = new CSVReader(new FileReader("./data/Usuarios.csv"))) {
            List<String[]> filas = reader.readAll();

            // Saltamos la cabecera (primera fila)
            for (int i = 1; i < filas.size(); i++) {
                String[] fila = filas.get(i);
                if (fila.length < 6) continue;

                Usuario u = new Usuario();
                u.setUsername(fila[0].trim());
                u.setPasswordHash(fila[1].trim());
                u.setNombres(fila[2].trim());
                u.setPrimerApellido(fila[3].trim());
                u.setSegundoApellido(fila[4].trim());
                u.setFechaNacimiento(fila[5].trim());

                usuarioRepository.save(u);
            }

            System.out.println("✓ Usuarios cargados desde CSV.");

        } catch (IOException | CsvException e) {
            System.err.println("Error al cargar Usuarios.csv: " + e.getMessage());
        }
    }

    private void cargarRecibos() {
        if (reciboPagoRepository.count() > 0) return;

        try (CSVReader reader = new CSVReader(new FileReader("./data/Recibos de Pago.csv"))) {
            List<String[]> filas = reader.readAll();

            for (int i = 1; i < filas.size(); i++) {
                String[] fila = filas.get(i);
                if (fila.length < 11) continue;

                ReciboPago r = new ReciboPago();
                r.setUsername(fila[0].trim());
                r.setNroRecibo(Integer.parseInt(fila[1].trim()));
                r.setFechaPago(fila[2].trim());
                r.setPeriodo(fila[3].trim());
                r.setSueldoBase(Long.parseLong(fila[4].trim()));
                r.setBonoProduccion(Long.parseLong(fila[5].trim()));
                r.setDescuentoSalud(Long.parseLong(fila[6].trim()));
                r.setDescuentoAfp(Long.parseLong(fila[7].trim()));
                r.setOtrosDescuentos(Long.parseLong(fila[8].trim()));
                r.setSueldoLiquido(Long.parseLong(fila[9].trim()));
                r.setDetalle(fila[10].trim());

                reciboPagoRepository.save(r);
            }

            System.out.println("✓ Recibos de pago cargados desde CSV.");

        } catch (IOException | CsvException e) {
            System.err.println("Error al cargar Recibos de Pago.csv: " + e.getMessage());
        }
    }
}
