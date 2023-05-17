package com.shop.config;

import com.shop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { // http 요청에 대한 보안을 설정한다. 페이지 권한 설정, 로그인 페이지 설정, 로그아웃 메서드 등에 대한 설정을 작성한다.
        http.formLogin()
                .loginPage("/members/login") // 로그인시 페이지 URL을 설정한다.
                .defaultSuccessUrl("/") // 로그인 성공 시 이동할 URL을 설정한다.
                .usernameParameter("email") // 로그인 시 사용할 파라미터 이름으로 email을 지정한다.
                .failureUrl("/members/login/error") // 로그인 실패 시 이동할 URL을 설정한다.
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) // 로그아웃 URL을 설정한다.
                .logoutSuccessUrl("/") // 로그아웃 성공 시 이동할 URL을 설정한다.
        ;

        http.authorizeRequests()
                .mvcMatchers("/css/**", "/js/**", "/img/**").permitAll()
                .mvcMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()
                .mvcMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { // 해킹을 통한 비빌번호 노출을 막기 위해서 BCryptPasswordEncoder의 해시함수를 이용하여 비밀번호를 암호화하여 저장한다.
        return new BCryptPasswordEncoder();
    }
}