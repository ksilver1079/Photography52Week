package com.darren.photo_challeng.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.darren.photo_challeng.entity.User;
import com.darren.photo_challeng.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor // 自動幫你生成建構子來注入元件
public class LoginController {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @GetMapping("/login")
  public String login() {
    // 這行會去 src/main/resources/templates/ 找名為 login.html 的檔案
    return "login";
  }

  @GetMapping("/register")
  public String showRegisterPage() {
    return "register"; // 指向 src/main/resources/templates/register.html
  }

  @PostMapping("/register")
  public String registerUser(@ModelAttribute User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword())); // 加密
    user.setRole(User.Role.USER); // 預設給一般使用者權限
    userRepository.save(user);
    return "redirect:/login?success"; // 註冊完跳回登入頁
  }

}