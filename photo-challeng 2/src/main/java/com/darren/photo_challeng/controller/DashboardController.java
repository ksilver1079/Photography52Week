package com.darren.photo_challeng.controller;

import com.darren.photo_challeng.dto.DashboardDTO;
import com.darren.photo_challeng.entity.Progress;
import com.darren.photo_challeng.entity.enums.ProgressStatus;
import com.darren.photo_challeng.repository.ProgressRepository;
import com.darren.photo_challeng.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor // 這會自動處理 DashboardService 的注入，不需要再寫 @Autowired
public class DashboardController {

    private final DashboardService dashboardService;
    private final ProgressRepository progressRepository;

    /**
     * 顯示儀表板
     */
    @GetMapping("/dashboard/{id}")
    public String getDashboard(@PathVariable("id") Long id, Model model) {
        System.out.println("🔍 正在讀取計畫 ID: " + id);

        // 目前 userId 先寫死為 1L
        DashboardDTO dashboard = dashboardService.getDashboardData(1L, id);

        model.addAttribute("data", dashboard);
        model.addAttribute("planId", id); // 傳給前端，方便「重新挑戰」按鈕使用
        return "dashboard";
    }

    /**
     * 顯示上傳頁面
     */
    @GetMapping("/upload")
    public String showUploadPage(@RequestParam("progressId") Long progressId,
            @RequestParam(value = "planId", required = false) Long planId,
            Model model) {
        model.addAttribute("progressId", progressId);
        model.addAttribute("planId", planId); // 為了上傳後能跳回正確的計畫
        return "upload";
    }

    /**
     * 處理檔案上傳
     * 這裡模擬上傳成功後更新狀態並跳轉
     */
    @PostMapping("/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file,
            @RequestParam("progressId") Long progressId,
            @RequestParam("planId") Long planId) {

        // 1. 找到這筆進度
        Progress progress = progressRepository.findById(progressId)
                .orElseThrow(() -> new RuntimeException("找不到紀錄"));

        if (!file.isEmpty()) {
            // 這就是你的 fileName，我們把它用起來！
            // 使用時間戳記避免檔名重複
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // 這裡可以加入儲存檔案到硬碟的邏輯...

            // 2. 將檔名存入資料庫欄位 (假設你的實體類有 photoUrl 欄位)
            progress.setPhotoUrl("/uploads/" + fileName);

            // 3. 更新狀態
            progress.setStatus(ProgressStatus.COMPLETED);
            progress.setCompletedAt(java.time.LocalDateTime.now());

            // 4. 關鍵：存回資料庫！(有了這一行，MySQL Workbench 才會有資料)
            progressRepository.save(progress);

            System.out.println("✅ 已儲存檔案: " + fileName);
        }

        return "redirect:/dashboard/" + planId;
    }

    /**
     * 重新挑戰功能
     */
    @PostMapping("/dashboard/reset/{planId}")
    public String resetChallenge(@PathVariable Long planId) {
        System.out.println("🔄 觸發重新挑戰，計畫 ID: " + planId);

        // 呼叫 Service 執行重置 (UserId 目前寫死 1L)
        dashboardService.resetPlanProgress(1L, planId);

        // 重置完後跳回儀表板，達成率會變回 0%
        return "redirect:/dashboard/" + planId;
    }

    @GetMapping("/progress/detail/{id}")
    public String showProgressDetail(@PathVariable("id") Long id, Model model) {
        // 1. 從資料庫抓取該筆進度（包含圖片路徑、上傳時間等）
        Progress progress = progressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到該筆作品紀錄"));

        // 2. 將資料傳給前端
        model.addAttribute("progress", progress);

        // 3. 回傳對應的 HTML 檔名
        return "progress_detail";
    }
}