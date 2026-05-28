package com.darren.photo_challeng.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http

        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll() // 公開頁面
            .requestMatchers("/dashboard/**", "/upload/**", "/progress/**", "/plan/**").hasAnyRole("USER", "ADMIN") // 保護頁面
            .requestMatchers("/admin/**").hasRole("ADMIN") // 管理員頁面
            .anyRequest().authenticated())

        // 登入設定：與你的 User Entity 對接
        .formLogin(form -> form
            .loginPage("/login") // 定義登入頁面路徑
            .loginProcessingUrl("/login") // 表單 POST 的路徑
            .usernameParameter("email") // 重要：告訴 Spring 用 Email 當帳號
            .passwordParameter("password") // 對應 SQL 的 password
            .defaultSuccessUrl("/", true) // 成功後跳轉到 52 週主題 index.html
            .permitAll())

        // 4. 登出設定
        .logout(logout -> logout
            .logoutUrl("/logout") // 登出的觸發路徑
            .logoutSuccessUrl("/login?logout") // 登出成功後跳轉的頁面
            .invalidateHttpSession(true) // 銷毀 Session
            .deleteCookies("JSESSIONID") // 刪除 Cookie
            .permitAll());

    return http.build();
  }

  // 密碼加密器：存入 SQL 的密碼必須經過此加密，登入才能比對成功
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}