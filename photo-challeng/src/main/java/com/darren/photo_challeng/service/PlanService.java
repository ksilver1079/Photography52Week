package com.darren.photo_challeng.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.darren.photo_challeng.entity.Plan;
import com.darren.photo_challeng.entity.Progress;
import com.darren.photo_challeng.entity.SubTheme;
import com.darren.photo_challeng.entity.User;
import com.darren.photo_challeng.entity.enums.ProgressStatus;
import com.darren.photo_challeng.repository.PlanRepository;
import com.darren.photo_challeng.repository.ProgressRepository;
import com.darren.photo_challeng.repository.SubThemeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // 自動注入 Repository
public class PlanService {

  private final PlanRepository planRepository;
  private final SubThemeRepository subThemeRepository;
  private final ProgressRepository progressRepository;

  // 獲取所有 18 個計畫
  public List<Plan> getAllPlans() {
    return planRepository.findAll();
  }

  // 根據 ID 獲取單一計畫詳情
  public Plan getPlanById(@org.springframework.lang.NonNull Long id) {
    return planRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("找不到該計畫！"));
  }

  @Transactional
  public List<SubTheme> assignPlanToUser(User user, Long planId) {
    Plan plan = planRepository.findById(planId).orElseThrow();
    List<SubTheme> subThemes = subThemeRepository.findByPlanIdOrderByTopicOrderAsc(planId);

    for (SubTheme theme : subThemes) {
      Progress progress = new Progress();
      progress.setPlan(plan);
      progress.setSubTheme(theme);
      // ✨ 修改這裡：直接傳入 User 物件，而不是只傳 ID
      progress.setUser(user);

      progress.setStatus(ProgressStatus.ACTIVE);
      progress.setStartedAt(LocalDateTime.now());
      progressRepository.save(progress);
    }
    return subThemes;
  }

  public List<Plan> getEnabledPlans() {
    // 呼叫我們在 Repository 定義的方法
    return planRepository.findByIsEnabledTrue();
  }

}
