package com.darren.photo_challeng.service;

import com.darren.photo_challeng.repository.UserRepository;
import com.darren.photo_challeng.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  // 註冊新使用者
  public User registerUser(User user) {
    // 檢查 Email 是否重複
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
      throw new RuntimeException("該 Email 已被註冊！");
    }
    // 這裡暫時明文儲存，之後我們會加上 BCrypt 加密
    return userRepository.save(user);
  }

  // 簡單的登入驗證邏輯
  public User login(String email, String password) {
    return userRepository.findByEmail(email)
        .filter(u -> u.getPassword().equals(password))
        .orElseThrow(() -> new RuntimeException("帳號或密碼錯誤"));
  }

  
}