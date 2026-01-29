package com.binbang.backend.global.security;

import com.binbang.backend.global.jwt.JwtAuthenticationFilter;
import com.binbang.backend.global.security.oauth2.CustomOAuth2UserService;
import com.binbang.backend.global.security.oauth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * - URL별 접근 권한 설정
 * - JWT 기반 인증 설정 (세션 사용 안 함)
 * - 비밀번호 암호화 설정
 * - CORS 설정
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // spring security 필터 체인 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                // csfr 보호 비활성화 (jwt 사용 시 필요없음)
                .csrf(csrf->csrf.disable())
                // jwt는 세션 사용 안함
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // URL별 접근 권한 설정
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/test/**",
                                "/login/oauth2/**"  //소셜로그인 엔드포인트 허용
                        ).permitAll()
                        // 나머지는 다 허용
                        .anyRequest().authenticated()
                )
                // jwt쓰기 때문에 form 로그인 비활성화
                .formLogin(form->form.disable())
                // HTTP Basic 인증 비활성화
                .httpBasic(basic->basic.disable())
                // OAuth2 로그인 설정 추가
                .oauth2Login(o->o
                        .userInfoEndpoint(end->end
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                )
                // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
