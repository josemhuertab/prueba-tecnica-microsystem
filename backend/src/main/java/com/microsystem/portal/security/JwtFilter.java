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
 * Intercepta cada petición HTTP y valida el JWT antes de que llegue al controlador.
 *
 * Flujo:
 *   1. Lee el header "Authorization: Bearer <token>"
 *   2. Valida el token con JwtUtil
 *   3. Si es válido, registra al usuario en el contexto de seguridad
 *   4. Pasa la petición al siguiente filtro o controlador
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
            // Extraemos el token quitando el prefijo "Bearer "
            String token = authHeader.substring(7);

            if (jwtUtil.isTokenValid(token)) {
                String username = jwtUtil.extractUsername(token);

                // Registramos la identidad del usuario para que los controladores
                // puedan acceder a ella con el parámetro Authentication
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Continuamos con la cadena de filtros independientemente del resultado
        filterChain.doFilter(request, response);
    }
}
