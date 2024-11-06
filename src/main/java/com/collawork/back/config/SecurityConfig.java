package com.collawork.back.config;

import com.collawork.back.security.JwtAuthenticationFilter;
import com.collawork.back.security.JwtTokenProvider;
import com.collawork.back.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/", "/static/**", "/favicon.ico").permitAll()
                        .requestMatchers("/api/auth/check-duplicates").permitAll()
                        .requestMatchers("/api/auth/social/**").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/user/info").permitAll()
                        .requestMatchers("/login/oauth2/code/**").permitAll()
                        .requestMatchers("/api/user/projects/newproject").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/friends/status").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/friends/accept").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/friends/reject").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/friends/remove").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/chatrooms").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/projects").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/notifications").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/refresh").permitAll()
                        .requestMatchers("/api/notifications/unread").authenticated()
                        .requestMatchers("/chattingServer/**").permitAll()


                        .anyRequest().authenticated())  // 나머지 경로는 인증 필요
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // JwtAuthenticationFilter 추가
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
