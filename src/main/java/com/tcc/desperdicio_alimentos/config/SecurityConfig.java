package com.tcc.desperdicio_alimentos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/login.html",
                                "/register.html",
                                "/register-ong.html",
                                "/dashboard-funcionario.html",
                                "/dashboard-ong.html",
                                "/produtos.html",
                                "/doacoes.html",
                                "/retiradas-funcionario.html",
                                "/retiradas-ong.html",
                                "/ongs.html",
                                "/cadastrar-produto.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/static/**",
                                "/api/auth/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .formLogin(login -> login.disable())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
