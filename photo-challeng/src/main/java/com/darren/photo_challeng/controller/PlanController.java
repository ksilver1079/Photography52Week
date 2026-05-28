package com.darren.photo_challeng.controller;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.darren.photo_challeng.entity.Plan;
import com.darren.photo_challeng.service.PlanService;
import com.darren.photo_challeng.service.CustomUserDetails; // ✨ 引用你寫好的類別

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping({ "/", "/plan" })
    public String index(Model model, 
                        @AuthenticationPrincipal CustomUserDetails userDetails) { // ✨ 直接注入

        // 1. 處理使用者名稱：直接從 session 拿，不用再查資料庫
        if (userDetails != null) {
            model.addAttribute("userDisplayName", userDetails.getDisplayName());
        } else {
            model.addAttribute("userDisplayName", "攝影師");
        }

        // 2. 抓取「上架中」的計畫
        List<Plan> activePlans = planService.getEnabledPlans();
        model.addAttribute("plans", activePlans);

        return "index"; // 指向首頁
    }
}