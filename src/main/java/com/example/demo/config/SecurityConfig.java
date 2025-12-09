package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // ====================================================
                        // 1. [누구나 접속 가능] - 인증/로그인, H2 콘솔, 에러 페이지
                        // ====================================================
                        .requestMatchers("/h2-console/**", "/error").permitAll()
                        // 명세서: 회원가입, 회원 로그인, 관리자 로그인 -> 모두 Open
                        .requestMatchers(HttpMethod.POST, "/auth/signup", "/auth/login", "/admin/login").permitAll()

                        // 도서 목록/검색/상세 조회는 보통 비로그인 상태에서도 보여주므로 permitAll 처리
                        .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()


                        // ====================================================
                        // 2. [관리자 전용] - ROLE_ADMIN만 가능
                        // ====================================================
                        // 명세서: AI 이미지 생성
                        .requestMatchers(HttpMethod.POST, "/api/images/generate").hasRole("ADMIN")

                        // 명세서: 도서 등록, 대여 가능 여부 확인 (/admin/books/...)
                        // 팁: /admin/** 로 시작하는 주소는 한방에 관리자 전용으로 묶음
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 명세서: 도서 삭제 (DELETE /api/books/delete/{bookId})
                        .requestMatchers(HttpMethod.DELETE, "/api/books/delete/**").hasRole("ADMIN")

                        // 명세서: 도서 수정 (UPDATE는 HTTP 메서드가 아님 -> PUT/PATCH로 매핑)
                        .requestMatchers(HttpMethod.PUT, "/api/books/update/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/books/update/**").hasRole("ADMIN")


                        // ====================================================
                        // 3. [일반 멤버 전용] - ROLE_USER만 가능
                        // ====================================================
                        // 명세서: 대여 하기 (POST /api/loans)
                        .requestMatchers(HttpMethod.POST, "/api/loans").hasRole("USER")

                        // 명세서: 내 대여 목록 (GET /loans/my)
                        .requestMatchers(HttpMethod.GET, "/loans/my").hasRole("USER")

                        // 명세서: 반납 하기 (PATCH /loans/{loanId}/return)
                        .requestMatchers(HttpMethod.PATCH, "/loans/**/return").hasRole("USER")


                        // ====================================================
                        // 4. [나머지] - 설정 안 된 모든 요청은 로그인 필요
                        // ====================================================
                        .anyRequest().authenticated()
                )

                // 필터 설정 (JWT 필터를 먼저 실행)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}