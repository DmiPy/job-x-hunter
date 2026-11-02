package dev.dmitriy.job_x_hunter.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/job-vacancies/**", "/skills/**", "/job-skills/job-vacancy/**").permitAll()
                        .requestMatchers("/users/me").authenticated()
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/job-vacancies/**", "/job-skills/**").hasRole("ADMIN")
                        .requestMatchers("/profiles/**", "/resumes/**", "/user-profile-skill/**").authenticated()
                        .anyRequest().denyAll())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(roleConverter())))
                .build();
    }

    @Bean
    Converter<Jwt, ? extends AbstractAuthenticationToken> roleConverter() {
        return jwt -> {
            Object claim = jwt.getClaim("realm_access");
            List<SimpleGrantedAuthority> authorities = List.of();
            if (claim instanceof java.util.Map<?, ?> realm && realm.get("roles") instanceof List<?> roles) {
                authorities = roles.stream().filter(String.class::isInstance).map(String.class::cast)
                        .filter(role -> role.equals("USER") || role.equals("ADMIN"))
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
            }
            return new JwtAuthenticationToken(jwt, authorities);
        };
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origins:http://localhost:4200}") String origins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(origins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
