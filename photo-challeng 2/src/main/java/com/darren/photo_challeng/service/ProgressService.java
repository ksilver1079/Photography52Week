package com.darren.photo_challeng.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.darren.photo_challeng.entity.Plan;
import com.darren.photo_challeng.entity.Progress;
import com.darren.photo_challeng.entity.SubTheme;
import com.darren.photo_challeng.entity.enums.ParticipantType;
import com.darren.photo_challeng.entity.enums.ProgressStatus;
import com.darren.photo_challeng.repository.ProgressRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final PlanService planService;

    @Transactional
    public void startPlan(Long planId, Long userId) {
        // 1. 取得計畫（包含裡面的 52 個 SubTheme）
        Plan plan = planService.getPlanById(planId);
        List<SubTheme> themes = plan.getSubThemes();

        if (themes == null || themes.isEmpty()) {
            throw new RuntimeException("該計畫尚未設定主題內容");
        }

        // 2. ✨ 核心修改：用迴圈一次產生 52 週的進度
        for (SubTheme theme : themes) {
            Progress progress = new Progress();
            progress.setPlan(plan);
            progress.setSubTheme(theme); // 綁定每一週的具體主題
            progress.setParticipantType(ParticipantType.INDIVIDUAL);
            progress.setParticipantId(userId);

            // 第一週設為 ACTIVE (進行中)，其餘可設為 PENDING (待處理) 或全部 ACTIVE
            progress.setStatus(ProgressStatus.ACTIVE);

            progress.setStartedAt(LocalDateTime.now());
            progressRepository.save(progress);
        }

        System.out.println("✅ 已成功為使用者 " + userId + " 建立 " + themes.size() + " 週的挑戰進度");
    }

    public List<Progress> getActiveProgressByUser(Long userId) {
        // 記得在 Repository 裡定義這個方法，並確保有 Order By subTheme.topicOrder
        return progressRepository.findByParticipantIdOrderBySubThemeTopicOrderAsc(userId);
    }

    @Transactional
    public void startPlan1(Long planId, Long userId) {
        // ✨ 檢查是否已經存在進度，避免重複建立
        List<Progress> existing = progressRepository.findByParticipantIdAndPlanIdOrderBySubThemeTopicOrderAsc(userId,
                planId);
        if (!existing.isEmpty()) {
            System.out.println("⚠️ 該使用者已經建立過此計畫進度，跳過建立步驟。");
            return;
        }
        Plan plan = planService.getPlanById(planId);
        List<SubTheme> themes = plan.getSubThemes();

        // 確保只循環一次 52 個主題，並且每個主題都建立一筆進度紀錄
        for (SubTheme theme : themes) {
            Progress progress = new Progress();
            progress.setPlan(plan);
            progress.setSubTheme(theme);
            progress.setParticipantId(userId);
            progress.setStatus(ProgressStatus.ACTIVE);
            progress.setStartedAt(LocalDateTime.now());
            progressRepository.save(progress);
        }
    }
}