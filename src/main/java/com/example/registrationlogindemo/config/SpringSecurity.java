package com.example.registrationlogindemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/register/**").permitAll()
                                .requestMatchers("/login", "/logout", "/", "/index").permitAll()
                                .requestMatchers("/detalle/**").permitAll()
                                .requestMatchers("/files/**").permitAll()
                                .requestMatchers("/detalle/submit").permitAll()
                                .requestMatchers("/crud/articulos").hasRole("ADMIN")
                                .requestMatchers("/crud/articulos/altas").hasRole("ADMIN")
                                .requestMatchers("/crud/articulos/altas/submit").hasRole("ADMIN")
                                .requestMatchers("/crud/articulos/modificar/**").hasRole("ADMIN")
                                .requestMatchers("/crud/articulos/modificar/submit").hasRole("ADMIN")
                                .requestMatchers("/crud/articulos/eliminar/**").hasRole("ADMIN")
                                .requestMatchers("/crud/comentarios").hasRole("ADMIN")
                                .requestMatchers("/crud/comentarios/delete/**").hasRole("ADMIN")
                                .requestMatchers("/users").hasRole("ADMIN")
                                .anyRequest().authenticated()
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/index")
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/index")
                                .permitAll()
                );
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}
