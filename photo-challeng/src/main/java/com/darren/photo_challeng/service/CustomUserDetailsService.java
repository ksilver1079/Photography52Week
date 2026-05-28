package com.darren.photo_challeng.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.darren.photo_challeng.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. 從資料庫尋找使用者 Entity
        com.darren.photo_challeng.entity.User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("找不到使用者: " + email));

        // 2. 建立角色權限 (手動加上 ROLE_ 前綴最保險)
        String roleWithPrefix = "ROLE_" + u.getRole().name(); // 假設 u.getRole().name() 是 "USER"
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleWithPrefix);

        // 3. 回傳 Spring Security 的 User 物件
        return new CustomUserDetails(
                u.getId(),
                u.getEmail(),
                u.getPassword(),
                Collections.singleton(authority),
                u.getDisplayName());
    }
}