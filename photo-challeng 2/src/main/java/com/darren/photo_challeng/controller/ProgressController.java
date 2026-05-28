package com.darren.photo_challeng.controller;

import com.darren.photo_challeng.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/progress")
@RequiredArgsConstructor
public class ProgressController {

  private final ProgressService progressService;

  @GetMapping("/start")
  public String startChallenge(@RequestParam("planId") Long planId) {
    // 執行你 Service 寫好的 52 筆初始化邏輯
    progressService.startPlan(planId, 1L);

    // 重定向到 DashboardController 的路徑
    return "redirect:/dashboard/" + planId;
  }
}