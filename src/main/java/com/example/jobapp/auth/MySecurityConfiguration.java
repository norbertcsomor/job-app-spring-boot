package com.example.jobapp.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * A Spring Security működését leíró és meghatározó konfigurációs osztály.
 * 
 * @author Norbert Csomor
 */
@Configuration
@EnableWebSecurity
public class MySecurityConfiguration {

    @Bean
    public UserDetailsService userDetailsService() {
        return new MyUserDetailsService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .authorizeRequests()
                // Publikus menüpontok útvonalai
                .antMatchers(
                        "/",
                        "/contact",
                        "/informations",
                        "/console/**")
                .permitAll()
                // Bejelentkezési útvonal
                /*
                 * .antMatchers("/login")
                 * .permitAll()
                 */
                // Ügyintéző útvonalak
                .antMatchers(HttpMethod.GET, "/employers")
                .hasAuthority("Admin")
                .antMatchers(HttpMethod.GET, "/jobseekers")
                .hasAuthority("Admin")
                .antMatchers(HttpMethod.GET, "/jobadvertisements")
                .hasAuthority("Admin")
                // Munkaadók útvonalak
                .antMatchers("/employers/create")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/employers")
                .permitAll()
                .antMatchers("/employers/edit/**")
                .hasAuthority("Employer")
                // Álláskeresők útvonalak
                .antMatchers("/jobseekers/create")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/jobseekers")
                .permitAll()
                .antMatchers("/jobseekers/edit/**")
                .hasAuthority("Jobseeker")
                // Önéletrajzok útvonalai
                .antMatchers(HttpMethod.POST, "/cvs")
                .hasAuthority("Jobseeker")
                .antMatchers(HttpMethod.GET, "/cvs/**")
                .hasAnyAuthority("Admin", "Employer", "Jobseeker")
                // Álláshirdetések útvonalai
                .antMatchers(HttpMethod.GET, "/jobadvertisements/show/**")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/jobadvertisements/delete/**")
                .hasAnyAuthority("Admin", "Employer")
                .antMatchers(HttpMethod.GET, "/jobadvertisements/edit/**")
                .hasAnyAuthority("Admin", "Employer")
                .antMatchers(HttpMethod.POST, "/jobadvertisements/update/**")
                .hasAnyAuthority("Admin", "Employer")
                // Egyéb útvonalak
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .loginProcessingUrl("/auth")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error=true")
                .permitAll()
                // .and()
                // .rememberMe()
                // .key("AbcdEfghIjklmNopQrsTuvXyz_0123456789")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout=true")
                // .invalidateHttpSession(true)
                // .deleteCookies("JSESSIONID")
                .permitAll()
                .and()
                .csrf();
        // .disable();

        httpSecurity.headers().frameOptions().sameOrigin();

        return httpSecurity.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                // "/cvs/**",
                "/css/**",
                "/js/**",
                "/webjars/**");
    }

}
