package com.darren.photo_challeng.config;

import com.darren.photo_challeng.entity.User;
import com.darren.photo_challeng.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    // 檢查並建立管理者 (ADMIN)
    if (userRepository.findByEmail("admin@test.com").isEmpty()) {
      User admin = new User();
      admin.setEmail("admin@test.com");
      admin.setPassword(passwordEncoder.encode("admin123"));
      admin.setDisplayName("系統管理員");
      admin.setRole(User.Role.ADMIN);
      userRepository.save(admin);
      System.out.println("✅ 管理者帳號已建立: admin@test.com / admin123");
    } else {
      System.out.println("ℹ️ 初始帳號已存在，跳過建立步驟。");
    }

    // 檢查並建立一般使用者 A
    if (userRepository.findByEmail("user1@test.com").isEmpty()) {
      User user1 = new User();
      user1.setEmail("user1@test.com");
      user1.setPassword(passwordEncoder.encode("user123"));
      user1.setDisplayName("攝影愛好者-小明");
      user1.setRole(User.Role.USER);
      userRepository.save(user1);
      System.out.println("✅ 使用者1已建立: user1@test.com / user123");
    } else {
      System.out.println("ℹ️ 初始帳號已存在，跳過建立步驟。");
    }

    // 檢查並建立一般使用者 B
    if (userRepository.findByEmail("user2@test.com").isEmpty()) {
      User user2 = new User();
      user2.setEmail("user2@test.com");
      user2.setPassword(passwordEncoder.encode("user234"));
      user2.setDisplayName("攝影大師-Darren");
      user2.setRole(User.Role.USER);
      userRepository.save(user2);
      System.out.println("✅ 使用者2已建立: user2@test.com / user234");
    } else {
      System.out.println("ℹ️ 初始帳號已存在，跳過建立步驟。");
    }
  }
}