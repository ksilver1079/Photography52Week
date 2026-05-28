package com.darren.photo_challeng.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.darren.photo_challeng.entity.Plan;
import com.darren.photo_challeng.entity.Progress;
import com.darren.photo_challeng.entity.SubTheme;
import com.darren.photo_challeng.entity.User;
import com.darren.photo_challeng.entity.enums.ProgressStatus;
import com.darren.photo_challeng.repository.ProgressRepository;
import com.darren.photo_challeng.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final PlanService planService;
    private final UserRepository userRepository;

    /**
     * 為使用者開啟新的挑戰計畫 (盲盒洗牌版)
     */
    @Transactional
    public void startPlan(Long planId, Long userId) {
        List<Progress> existing = progressRepository.findByUserIdAndPlanIdOrderBySubThemeTopicOrderAsc(userId, planId);

        if (!existing.isEmpty()) {
            System.out.println("ℹ️ 使用者 " + userId + " 已經擁有計畫 " + planId + " 的進度，直接跳轉。");
            return;
        }

        Plan plan = planService.getPlanById(planId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        if (plan.getSubThemes() == null || plan.getSubThemes().isEmpty()) {
            throw new RuntimeException("該計畫尚未設定主題內容");
        }

        List<SubTheme> shuffledThemes = new ArrayList<>(plan.getSubThemes());
        Collections.shuffle(shuffledThemes);

        int weekCounter = 1;
        for (SubTheme theme : shuffledThemes) {
            Progress progress = new Progress();
            progress.setPlan(plan);
            progress.setSubTheme(theme);
            progress.setUser(user);

            if (weekCounter == 1) {
                progress.setStatus(ProgressStatus.ACTIVE);
            } else {
                progress.setStatus(ProgressStatus.LOCKED);
            }

            progress.setStartedAt(LocalDateTime.now());
            progressRepository.save(progress);

            weekCounter++;
        }

        System.out.println("✅ 已成功為攝影師 [" + user.getDisplayName() + "] 建立計畫：「" + plan.getName() + "」的 52 週盲盒挑戰進度");
    }

    @Transactional
    public void unlockNextWeek(Long userId, Long planId) {
        // 1. 強制防呆：先找出目前所有 ACTIVE 的任務並設為 COMPLETED
        // 這能解決你之前提到的「顯示兩個任務」的狀態殘留問題
        List<Progress> activeTasks = progressRepository.findByUserIdAndPlanIdAndStatus(userId, planId,
                ProgressStatus.ACTIVE);
        for (Progress p : activeTasks) {
            p.setStatus(ProgressStatus.COMPLETED);
            p.setCompletedAt(LocalDateTime.now());
            progressRepository.save(p);
        }

        // 2. 撈出所有目前處於 LOCKED 狀態的盲盒任務
        List<Progress> lockedTasks = progressRepository.findByUserIdAndPlanIdAndStatus(userId, planId,
                ProgressStatus.LOCKED);

        // 3. 判斷是否有盲盒可以開
        if (!lockedTasks.isEmpty()) {
            // 🎲 核心盲盒邏輯：將剩下的任務大洗牌 (Shuffle)
            Collections.shuffle(lockedTasks);

            // 🎁 抽出第一個作為下週任務
            Progress nextTask = lockedTasks.get(0);

            // 🔓 解鎖並按下倒數碼表
            nextTask.setStatus(ProgressStatus.ACTIVE);
            nextTask.setStartedAt(LocalDateTime.now()); // 非常重要：這是倒數計時的起點

            progressRepository.save(nextTask);
            System.out.println("🎁 成功解鎖隨機盲盒任務: [" + nextTask.getSubTheme().getTitle() + "] (Week "
                    + nextTask.getSubTheme().getTopicOrder() + ")");
        } else {
            System.out.println("🎉 恭喜！攝影師已完成所有 52 週挑戰！");
        }
    }

    /**
     * 獲取使用者目前的進度列表 (把你原本寫好的加回來)
     */
    public List<Progress> getActiveProgressByUser(Long userId) {
        return progressRepository.findByUserIdOrderBySubThemeTopicOrderAsc(userId);
    }

    public long getHoursRemaining(LocalDateTime startedAt) {
        LocalDateTime deadline = startedAt.plusDays(7);
        return java.time.Duration.between(LocalDateTime.now(), deadline).toHours();
    }

}