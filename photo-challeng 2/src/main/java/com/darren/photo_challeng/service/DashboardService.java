package com.darren.photo_challeng.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.darren.photo_challeng.dto.DashboardDTO;
import com.darren.photo_challeng.dto.ProgressItemDTO;
import com.darren.photo_challeng.entity.Plan;
import com.darren.photo_challeng.entity.Progress;
import com.darren.photo_challeng.entity.enums.ProgressStatus;
import com.darren.photo_challeng.repository.PlanRepository;
import com.darren.photo_challeng.repository.ProgressRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

  private final PlanRepository planRepository;
  private final ProgressRepository progressRepository;

  /**
   * 獲取儀表板數據
   */
  @Transactional(readOnly = true)
  public DashboardDTO getDashboardData(Long userId, Long planId) {
    // 1. 獲取計畫與進度列表
    Plan plan = planRepository.findById(planId)
        .orElseThrow(() -> new RuntimeException("找不到計畫 ID: " + planId));

    List<Progress> progressList = progressRepository.findByParticipantIdAndPlanId(userId, planId);

    // 2. 初始化 DTO
    DashboardDTO dashboard = new DashboardDTO();
    dashboard.setPlanName(plan.getName());
    dashboard.setTotalWeeks(plan.getTotalTopics());

    // 3. 轉換進度清單並處理空值防護
    List<ProgressItemDTO> weekDtos = progressList.stream()
        .filter(Objects::nonNull)
        .map(p -> {
          ProgressItemDTO item = new ProgressItemDTO();
          item.setProgressId(p.getId());

          if (p.getSubTheme() != null) {
            item.setWeekNum(p.getSubTheme().getTopicOrder());
            item.setTitle(p.getSubTheme().getTitle());
            item.setDescription(p.getSubTheme().getDescription());
          } else {
            item.setWeekNum(0);
            item.setTitle("主題未定義");
          }

          // 這裡直接存字串，方便 Thymeleaf 比對
          item.setStatus(p.getStatus() != null ? p.getStatus().name() : "ACTIVE");
          return item;
        })
        .collect(Collectors.toList());

    dashboard.setWeeks(weekDtos);

    // 4. 計算達成率 (確保使用最新轉換後的列表數量)
    if (!weekDtos.isEmpty()) {
      long completedCount = weekDtos.stream()
          .filter(dto -> "COMPLETED".equals(dto.getStatus()))
          .count();
      // 計算百分比，使用 100.0 強制轉為浮點數運算
      double rate = (completedCount * 100.0) / plan.getTotalTopics();
      dashboard.setCompletionRate(rate);
    } else {
      dashboard.setCompletionRate(0.0);
    }

    return dashboard;
  }

  /**
   * 重新挑戰：重置所有進度狀態
   */
  @Transactional
  public void resetPlanProgress(Long userId, Long planId) {
    List<Progress> progressList = progressRepository.findByParticipantIdAndPlanId(userId, planId);

    for (Progress p : progressList) {
      p.setStatus(ProgressStatus.ACTIVE);
      // 如果你有存照片欄位，記得在這裡清空
      // p.setPhotoPath(null);
      // p.setCompletedAt(null);
    }

    progressRepository.saveAll(progressList);
    System.out.println("✅ 使用者 " + userId + " 的計畫 " + planId + " 已重置成功。");
  }
}