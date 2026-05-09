package com.microsystem.portal.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Este filtro intercepta cada petición HTTP y valida el JWT antes de que llegue al controlador.
 *
 * El flujo que implementé es:
 *   1. Leo el header "Authorization: Bearer <token>"
 *   2. Valido el token con JwtUtil
 *   3. Si es válido, registro al usuario en el contexto de seguridad de Spring
 *   4. Paso la petición al siguiente filtro o controlador sin importar el resultado
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Quito el prefijo "Bearer " para quedarme solo con el token
            String token = authHeader.substring(7);

            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.extractUsername(token);

                // Registro la identidad del usuario en el contexto de seguridad
                // para que los controladores puedan leerla con el parámetro Authentication
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Siempre continúo con la cadena de filtros, aunque el token sea inválido o no exista
        filterChain.doFilter(request, response);
    }
}
