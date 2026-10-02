package com.umg.citasmedicas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Patrón con comodín también para la IP de AWS: cubre cualquier
        // puerto que publiques ahí (8081 ahora, el que sea después),
        // sin tener que acordarnos de agregar cada uno a mano.
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://13.220.83.251:*"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    // Reemplaza el comportamiento por defecto de httpBasic (que manda el
    // header WWW-Authenticate: Basic y por eso dispara el cuadro gris
    // nativo del navegador) por una simple respuesta 401 en JSON. La
    // autenticación con Basic Auth sigue funcionando igual para quien
    // mande el header Authorization a mano (tu app.js) — lo único que
    // cambia es qué pasa cuando NO lo manda.
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"No autenticado\"}");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Archivos estáticos reales de tu proyecto: la raíz,
                        // cualquier .html/.js/.css suelto, y el favicon.
                        // Son solo la "carcasa" visual, sin datos sensibles
                        // adentro — el navegador tiene que poder navegar a
                        // ellos SIEMPRE sin pedir credenciales, o vas a
                        // seguir viendo el cuadro gris en cada redirección.
                        .requestMatchers(HttpMethod.GET, "/", "/*.html", "/*.js", "/*.css", "/favicon.ico").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/pacientes").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/medicos", "/api/administradores", "/api/especialidades")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMINISTRADOR")
                        // /api/auth/me y todo lo demás SÍ necesitan
                        // autenticación real — es justo lo que le dice a
                        // tu frontend quién inició sesión.
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> basic.authenticationEntryPoint(authenticationEntryPoint()));

        return http.build();
    }
}
