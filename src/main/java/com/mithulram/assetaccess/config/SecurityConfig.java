package com.mithulram.assetaccess.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/index.html", "/assets/**", "/actuator/health").permitAll()
                        .requestMatchers("/api/audit-events/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/assets/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/assets/**").hasAnyRole("ADMIN", "OPERATOR", "VIEWER")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService users(
            PasswordEncoder passwordEncoder,
            @Value("${asset-access.demo.admin-password}") String adminPassword,
            @Value("${asset-access.demo.operator-password}") String operatorPassword,
            @Value("${asset-access.demo.viewer-password}") String viewerPassword) {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin").password(passwordEncoder.encode(adminPassword)).roles("ADMIN").build(),
                User.withUsername("operator").password(passwordEncoder.encode(operatorPassword)).roles("OPERATOR").build(),
                User.withUsername("viewer").password(passwordEncoder.encode(viewerPassword)).roles("VIEWER").build());
    }
}
