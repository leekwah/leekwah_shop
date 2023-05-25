package com.shop.config;

import com.shop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    MemberService memberService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
                .usernameParameter("email")
                .failureUrl("/members/login/error")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/")
        ;

        http.authorizeRequests() // 시큐리티 처리에 HttpServletRequest를 이용한다는 것을 의미한다.
                .mvcMatchers("/css/**", "/static/js/**", "/img/**").permitAll()
                .mvcMatchers("/", "/members/**", "/item/**", "/images/**").permitAll() // permitAll() 메서드를 이용해 모든 사용자가 인증(로그인)없이 해당 경로에 접근할 수 있도록 설정한다.
                .mvcMatchers("/admin/**").hasRole("ADMIN") // '/admin'으로 시작하는 경로는 해당 계정이 ADMIN ROLE일 경우에만 접근 가능하도록 설정한다.
                .anyRequest().authenticated() // 2, 3에서 제외한 나머지 경로들은 모두 인증을 요구하도록 설정한다.
        ;

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        ;

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() { // 해킹을 통한 비빌번호 노출을 막기 위해서 BCryptPasswordEncoder의 해시함수를 이용하여 비밀번호를 암호화하여 저장한다.
        return new BCryptPasswordEncoder();
    }
}