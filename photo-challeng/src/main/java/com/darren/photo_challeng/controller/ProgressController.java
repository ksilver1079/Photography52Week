package com.darren.photo_challeng.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.darren.photo_challeng.entity.Progress;
import com.darren.photo_challeng.entity.enums.ProgressStatus;
import com.darren.photo_challeng.repository.ProgressRepository;
import com.darren.photo_challeng.service.CustomUserDetails;

import com.darren.photo_challeng.service.PhotoExifService;
import com.darren.photo_challeng.service.ProgressService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/progress")
@RequiredArgsConstructor
public class ProgressController {

  private final PhotoExifService photoExifService; // ✨ 注入 EXIF 處理器
  private final ProgressRepository progressRepository; // ✨ 注入 Repo 才能存檔
  private final ProgressService progressService;

  

  // 2. 顯示上傳頁面 (對齊 upload.html)
  // 2. 顯示上傳頁面 (完美對接 upload.html)
  @GetMapping("/upload/{subThemeId}")
  public String showUploadPage(
      @PathVariable Long subThemeId,
      @AuthenticationPrincipal CustomUserDetails userDetails,
      Model model) {

    if (userDetails == null)
      return "redirect:/login";

    // ✨ 根據 subThemeId 找出目前的進度，藉此獲取 planId 和 progressId
    Progress progress = progressRepository.findByUserIdAndSubThemeId(userDetails.getId(), subThemeId)
        .orElseThrow(() -> new RuntimeException("找不到對應的進度紀錄"));

    // ✨ 把 HTML 需要的三個變數都準備好
    model.addAttribute("subThemeId", subThemeId);
    model.addAttribute("planId", progress.getPlan().getId());
    model.addAttribute("progressId", progress.getId());

    return "upload";
  }

  // 3. 接收照片並存檔 (實作 EXIF 功能)
  @PostMapping("/upload/{subThemeId}")
  public String handleFileUpload(
      @PathVariable Long subThemeId,
      @RequestParam("file") MultipartFile file,
      @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {

    if (userDetails == null)
      return "redirect:/login";

    // A. 找到該名攝影師對應的這週進度紀錄
    // 這裡建議在 Service 寫一個查詢邏輯，這裡先示範概念
    Progress progress = progressRepository.findByUserIdAndSubThemeId(userDetails.getId(), subThemeId)
        .orElseThrow(() -> new RuntimeException("找不到對應的進度紀錄"));

    if (!file.isEmpty()) {
      // B. 呼叫 PhotoExifService 存檔 (UUID 檔名)
      String fileName = photoExifService.saveImage(file);
      progress.setPhotoPath(fileName);

      // C. 自動抓取光圈、快門、ISO
      photoExifService.fillPhotoMetadata(file, progress);

      // D. 更新狀態
      progress.setStatus(ProgressStatus.COMPLETED);
      progress.setCompletedAt(java.time.LocalDateTime.now());

      progressRepository.save(progress);
      progressService.unlockNextWeek(userDetails.getId(), progress.getPlan().getId());
    }

    System.out.println("📸 成功存檔，準備跳轉！");
    return "redirect:/dashboard/" + progress.getPlan().getId();
  }

  @GetMapping("/detail/{id}")
  public String showPhotoView(@PathVariable("id") Long id, Model model) {
    Progress progress = progressRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("找不到該筆作品紀錄"));

    model.addAttribute("progress", progress);
    return "photo_view"; // ✨ 已經改為新的檔名！
  }

}