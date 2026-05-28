package com.darren.photo_challeng.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.darren.photo_challeng.service.CustomUserDetails;
import com.darren.photo_challeng.dto.DashboardDTO;
import com.darren.photo_challeng.dto.ProgressItemDTO;
import com.darren.photo_challeng.entity.Progress;
import com.darren.photo_challeng.entity.enums.ProgressStatus;
import com.darren.photo_challeng.repository.ProgressRepository;
import com.darren.photo_challeng.service.DashboardService;
import com.darren.photo_challeng.service.PhotoExifService;

// import com.darren.photo_challeng.service.ProgressService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final ProgressRepository progressRepository;
    private final PhotoExifService photoExifService;
    // private final ProgressService progressService;

    /**
     * 1. 顯示儀表板
     */
    @GetMapping("/dashboard/{id}")
    public String getDashboard(@PathVariable("id") Long planId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model) {
        if (userDetails == null)
            return "redirect:/login";

        // 1. 原本的程式碼：取得包含 52 週進度的 DTO
        DashboardDTO dashboard = dashboardService.getDashboardData(userDetails.getId(), planId);

        // 2. ✨ 新增邏輯：從 dashboard 的週次清單中，濾出狀態為 ACTIVE 的那一項
        // 假設你的 DashboardDTO 裡面存放清單的欄位叫 weeks
        ProgressItemDTO currentTask = dashboard.getWeeks().stream()
                .filter(task -> "ACTIVE".equals(task.getStatus()))
                .findFirst()
                .orElse(null);

        // 3. 把資料傳給前端
        model.addAttribute("data", dashboard); // 這是原本的 52 週完整資料
        model.addAttribute("currentTask", currentTask); // 這是專門給置頂 Hero 區塊用的單筆資料
        model.addAttribute("planId", planId);
        model.addAttribute("userDisplayName", userDetails.getDisplayName());

        return "dashboard";
    }

    /**
     * 2. 計畫總覽 (原 detail.html)
     * 注意：確保你有一個對應的方法回傳 "plan_overview"
     */
    @GetMapping("/plan/overview/{id}")
    public String showPlanOverview(@PathVariable("id") Long planId, Model model) {
        // 這裡需要根據 planId 抓取任務列表，目前先對應你改名的檔名
        // 假設你原本是用這個頁面顯示 52 個格子
        return "plan_overview";
    }

    /**
     * 4. 顯示上傳頁面 (GET)
     */
    @GetMapping("/upload")
    public String showUploadPage(@RequestParam("progressId") Long progressId,
            @RequestParam(value = "planId", required = false) Long planId,
            Model model) {
        model.addAttribute("progressId", progressId);
        model.addAttribute("planId", planId);
        return "upload";
    }

    /**
     * 5. 處理檔案上傳 (POST) - 整合 EXIF 功能
     */
    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file,
            @RequestParam("progressId") Long progressId,
            @RequestParam("planId") Long planId) throws Exception {

        Progress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new RuntimeException("找不到紀錄"));

        if (!file.isEmpty()) {
            // A. 使用你寫好的 Service 存檔並回傳 UUID 檔名
            String fileName = photoExifService.saveImage(file);
            progress.setPhotoPath(fileName);

            // B. ✨ 自動讀取 Sony A7R3 的 EXIF 並填入
            photoExifService.fillPhotoMetadata(file, progress);

            // C. 更新狀態
            progress.setStatus(ProgressStatus.COMPLETED);
            progress.setCompletedAt(java.time.LocalDateTime.now());

            progressRepository.save(progress);
        }

        return "redirect:/dashboard/" + planId;
    }

    /**
     * 6. 重新挑戰
     */
    @PostMapping("/dashboard/reset/{planId}")
    public String resetChallenge(@PathVariable Long planId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 1. 呼叫原本的方法，把資料庫裡舊的、狀態錯誤的進度徹底刪除
        dashboardService.resetPlanProgress(userDetails.getId(), planId);

        // 2. ✨ 關鍵新增：呼叫盲盒引擎，重新洗牌並發放 1 個 ACTIVE 與 51 個 LOCKED！
        // progressService.startPlan(planId, userDetails.getId());
        return "redirect:/dashboard/" + planId;
    }

    @GetMapping("/progress/start")
    public String startChallenge(@RequestParam("planId") Long planId) {
        // 不需要呼叫任何 Service 進行洗牌！
        // 我們直接導向儀表板，DashboardService 的 getDashboardData 發現資料庫是空的，
        // 就會自動依照我們寫好的順序，幫你建好 1 號 ACTIVE 其他 LOCKED 的進度。
        return "redirect:/dashboard/" + planId;
    }

}