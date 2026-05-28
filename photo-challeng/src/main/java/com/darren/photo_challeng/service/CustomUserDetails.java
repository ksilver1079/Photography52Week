package com.darren.photo_challeng.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * CustomUserDetails 負責將資料庫的 User 轉化為 Spring Security 認得的 Principal。
 * 使用 @Getter 讓 Controller 可以直接呼叫 .getId() 或 .getDisplayName()。
 */
@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

  private final Long id;
  private final String email;
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
  private final String displayName;

  // --- UserDetails 介面要求實作的方法 ---

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email; // 登入帳號使用 Email
  }

  // --- 帳號狀態檢查（開發階段通常回傳 true） ---

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}