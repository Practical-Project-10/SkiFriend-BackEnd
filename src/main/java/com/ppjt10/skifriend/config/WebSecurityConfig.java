package com.ppjt10.skifriend.config;

import com.ppjt10.skifriend.security.FilterSkipMatcher;
import com.ppjt10.skifriend.security.FormLoginSuccessHandler;
import com.ppjt10.skifriend.security.filter.FormLoginFilter;
import com.ppjt10.skifriend.security.filter.JwtAuthFilter;
import com.ppjt10.skifriend.security.jwt.HeaderTokenExtractor;
import com.ppjt10.skifriend.security.provider.FormLoginAuthProvider;
import com.ppjt10.skifriend.security.provider.JWTAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true) // @Secured 어노테이션 활성화
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTAuthProvider jwtAuthProvider;
    private final HeaderTokenExtractor headerTokenExtractor;

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        // CustomAuthenticationProvider()를 호출하기 위해서 Overriding
        auth
                .authenticationProvider(formLoginAuthProvider())
                .authenticationProvider(jwtAuthProvider);
    }

    @Override
    public void configure(WebSecurity web) {
        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        web
                .ignoring()
                .antMatchers("/h2-console/**")
                .antMatchers("/profile")
                .antMatchers("/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .headers()
                .frameOptions().sameOrigin(); // SockJS는 기본적으로 HTML iframe 요소를 통한 전송을 허용하지 않도록 설정되는데 해당 내용을 해제한다.

        // cors설정 추가
        http
                .cors()
                .configurationSource(corsConfigurationSource());

        // 서버에서 인증은 JWT로 인증하기 때문에 Session의 생성을 막습니다.
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        /*
         * 1.
         * UsernamePasswordAuthenticationFilter 이전에 FormLoginFilter, JwtFilter 를 등록합니다.
         * FormLoginFilter : 로그인 인증을 실시합니다.
         * JwtFilter       : 서버에 접근시 JWT 확인 후 인증을 실시합니다.
         */
        http
                .addFilterBefore(formLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                .anyRequest()
                .permitAll()
                .and()
                // [로그아웃 기능]
                .logout()
                // 로그아웃 요청 처리 URL
                .logoutUrl("/user/logout")
                .permitAll()
                .and()
                .exceptionHandling();
    }

    @Bean
    public FormLoginFilter formLoginFilter() throws Exception {
        FormLoginFilter formLoginFilter = new FormLoginFilter(authenticationManager());
        formLoginFilter.setFilterProcessesUrl("/user/login");
        formLoginFilter.setAuthenticationSuccessHandler(formLoginSuccessHandler()); // 인증 성공시 호출할 핸들러 지정
        formLoginFilter.afterPropertiesSet();
        return formLoginFilter;
    }

    @Bean
    public FormLoginSuccessHandler formLoginSuccessHandler() {
        return new FormLoginSuccessHandler();
    }

    @Bean
    public FormLoginAuthProvider formLoginAuthProvider() {
        return new FormLoginAuthProvider(encodePassword());
    }

    private JwtAuthFilter jwtFilter() throws Exception {
        List<String> skipPathList = new ArrayList<>();

        // h2-console 허용
        skipPathList.add("GET,/h2-console/**");
        skipPathList.add("POST,/h2-console/**");

        // 홈
        skipPathList.add("GET,/");

        // 회원 관리 API 허용
        skipPathList.add("GET,/user/kakao/callback");
        skipPathList.add("GET,/user/kakao/callback/{userId}");
        skipPathList.add("GET,/user/naver/callback");

        // 상세 탭
        skipPathList.add("GET,/main");
        skipPathList.add("GET,/board/carpool/{skiResort}/banner");
        skipPathList.add("GET,/board/carpool/{skiResort}");
        skipPathList.add("POST,/board/carpool/{skiResort}/category");
        skipPathList.add("GET,/board/freeBoard/**");
        skipPathList.add("GET,/skiResort/{skiResort}");

        // 채팅
        skipPathList.add("GET,/webjars/**");
        skipPathList.add("GET,/ws-stomp/**");
        skipPathList.add("GET,/ws-alarm/**");
        skipPathList.add("GET,/chat/room");
        skipPathList.add("GET,/chat/user");

        // 쇼츠
        skipPathList.add("GET,/shorts");
        skipPathList.add("GET,/shorts/{shortsId}");

        FilterSkipMatcher matcher = new FilterSkipMatcher(
                skipPathList,
                "/**"
        );

        JwtAuthFilter filter = new JwtAuthFilter(
                matcher,
                headerTokenExtractor
        );
        filter.setAuthenticationManager(super.authenticationManagerBean());

        return filter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000"); // local 테스트 시
        configuration.addAllowedOrigin("https://skifriend.shop");
        configuration.setAllowCredentials(true);
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("Authorization");
//        configuration.addAllowedOriginPattern("*"); // 배포 전 모두 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
