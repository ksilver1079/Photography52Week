package com.darren.photo_challeng.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.darren.photo_challeng.dto.DashboardDTO;
import com.darren.photo_challeng.dto.ProgressItemDTO;
import com.darren.photo_challeng.entity.Plan;
import com.darren.photo_challeng.entity.Progress;
import com.darren.photo_challeng.entity.User;
import com.darren.photo_challeng.entity.enums.ProgressStatus;
import com.darren.photo_challeng.repository.PlanRepository;
import com.darren.photo_challeng.repository.ProgressRepository;
import com.darren.photo_challeng.repository.UserRepository; // 👈 記得注入

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PlanRepository planRepository;
    private final ProgressRepository progressRepository;
    private final UserRepository userRepository; // 👈 必須加入，用來獲取 User 實體

    @Transactional
    public DashboardDTO getDashboardData(Long currentUserId, Long planId) {
        // 1. 抓取計畫資訊
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("找不到計畫 ID: " + planId));

        // 2. 根據當前登入的使用者 ID 抓取專屬進度
        List<Progress> progressList = progressRepository
                .findByUserIdAndPlanIdOrderBySubThemeTopicOrderAsc(currentUserId, planId);

        // 3. 如果該使用者還沒有這個計畫的進度，則進行初始化建立
        if (progressList.isEmpty()) {
            // 👈 修正：先找到 User 實體再傳入初始化方法
            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new RuntimeException("找不到使用者"));
            progressList = initializeNewUserProgress(currentUser, plan);
        }

        // 4. 初始化 DTO
        DashboardDTO dashboard = new DashboardDTO();
        dashboard.setPlanName(plan.getName());
        dashboard.setTotalWeeks(plan.getTotalTopics());

        // 5. 轉換進度清單
        List<ProgressItemDTO> weekDtos = progressList.stream()
                .filter(Objects::nonNull)
                .map(p -> {
                    ProgressItemDTO item = new ProgressItemDTO();
                    item.setSubThemeId(p.getSubTheme().getId());
                    item.setProgressId(p.getId());

                    if (p.getSubTheme() != null) {
                        item.setWeekNum(p.getSubTheme().getTopicOrder());
                        item.setTitle(p.getSubTheme().getTitle());
                        item.setDescription(p.getSubTheme().getDescription());
                        item.setCategory(p.getSubTheme().getCategory());
                    } else {
                        item.setWeekNum(0);
                        item.setTitle("主題未定義");
                        item.setCategory("未分類");
                    }

                    item.setStatus(p.getStatus() != null ? p.getStatus().name() : "ACTIVE");

                    // 👇 ========== ✨ 新增：計算倒數時間的邏輯 ========== 👇
                    if ("ACTIVE".equals(item.getStatus()) && p.getStartedAt() != null) {
                        // 💡 新需求：第一週 (weekNum == 1) 不計時，第二週開始才計時
                        if (item.getWeekNum() > 1) {
                            // 任務期限 = 開始時間 + 7 天
                            java.time.LocalDateTime deadline = p.getStartedAt().plusDays(7);
                            // 將「絕對時間」轉成標準字串 (ISO 8601) 傳給前端
                            item.setDeadlineIso(deadline.toString());
                        } else {
                            item.setDeadlineIso(null); // 第一週沒有期限
                        }
                    } else {
                        item.setDeadlineIso(null); // 非 ACTIVE 狀態不顯示期限
                    }
                    // 👆 ================================================= 👆

                    // ... (其他 EXIF 或 PhotoPath 的 mapping 保留你的原本寫法) ...
                    // 例如：item.setPhotoPath(p.getPhotoPath()); 等等

                    return item;
                })
                .collect(Collectors.toList());

        dashboard.setWeeks(weekDtos);

        // 6. 計算達成率
        if (!weekDtos.isEmpty()) {
            long completedCount = weekDtos.stream()
                    .filter(dto -> "COMPLETED".equals(dto.getStatus()))
                    .count();
            double rate = (completedCount * 100.0) / plan.getTotalTopics();
            dashboard.setCompletionRate(rate);
        } else {
            dashboard.setCompletionRate(0.0);
        }

        return dashboard;
    }

    private List<Progress> initializeNewUserProgress(User user, Plan plan) {
        List<Progress> newProgresses = plan.getSubThemes().stream().map(subTheme -> {
            Progress p = new Progress();
            p.setUser(user);
            p.setPlan(plan);
            p.setSubTheme(subTheme);

            // 🎯 絕對鎖死：只要是第 1 個主題，就是 ACTIVE (暖身)
            if (subTheme.getTopicOrder() == 1) {
                p.setStatus(ProgressStatus.ACTIVE);
                p.setStartedAt(LocalDateTime.now());

            } else {
                // 🎯 其餘 51 個絕對是 LOCKED (盲盒)
                p.setStatus(ProgressStatus.LOCKED);
                p.setStartedAt(null);
            }

            return p;
        }).collect(Collectors.toList());

        return progressRepository.saveAll(newProgresses);
    }

    @Transactional
    public void resetPlanProgress(Long userId, Long planId) {
        List<Progress> progressList = progressRepository
                .findByUserIdAndPlanIdOrderBySubThemeTopicOrderAsc(userId, planId);

        for (Progress p : progressList) {
            // 💡 盲盒核心邏輯：判斷是不是第 1 週
            if (p.getSubTheme().getTopicOrder() == 1) {
                p.setStatus(ProgressStatus.ACTIVE); // 第 1 週設為進行中
                p.setStartedAt(LocalDateTime.now()); // 重新開始計時
            } else {
                p.setStatus(ProgressStatus.LOCKED); // 第 2~52 週通通鎖起來！
                p.setStartedAt(null); // 清除開始時間
            }

            // 🧹 大掃除：清空所有照片與參數紀錄
            p.setCompletedAt(null);
            p.setPhotoUrl(null);
            p.setPhotoPath(null);
            p.setAperture(null);
            p.setShutterSpeed(null);
            p.setIso(null);
        }

        progressRepository.saveAll(progressList);
    }
}