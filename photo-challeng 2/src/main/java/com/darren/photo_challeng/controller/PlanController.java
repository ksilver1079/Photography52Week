package com.darren.photo_challeng.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.darren.photo_challeng.entity.Plan;
import com.darren.photo_challeng.repository.UserRepository;
import com.darren.photo_challeng.service.PlanService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PlanController {

  private final PlanService planService;
  private final UserRepository userRepository;


  @GetMapping({ "/", "/plan" })
  public String index(Model model, Principal principal) {

    // 處理使用者名稱
    if (principal != null) {
      String email = principal.getName();
      userRepository.findByEmail(email).ifPresent(user -> {
        model.addAttribute("userDisplayName", user.getDisplayName());
      });
    } else {
      model.addAttribute("userDisplayName", "攝影師");
    }


    List<Plan> allPlans = planService.getAllPlans();
    model.addAttribute("plans", allPlans);

    // 回傳 index.html
    return "index";
  }
}