package com.darren.photo_challeng.controller;

import com.darren.photo_challeng.entity.Progress;
import com.darren.photo_challeng.entity.enums.ProgressStatus;
import com.darren.photo_challeng.repository.ProgressRepository;
import com.darren.photo_challeng.service.PhotoExifService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/progress") // ✨ 修改為 /progress 以對齊你之前的按鈕連結
@RequiredArgsConstructor
public class PhotoController {

  private final ProgressRepository progressRepository;
  private final PhotoExifService photoExifService; // ✨ 注入你的 EXIF 處理器

  /**
   * 顯示上傳頁面 (對應 /progress/upload?progressId=XXX)
   */
  @GetMapping("/upload")
  public String showUploadPage(@RequestParam("progressId") Long progressId,
      @RequestParam(value = "planId", required = false) Long planId,
      Model model) {
    model.addAttribute("progressId", progressId);
    model.addAttribute("planId", planId);
    return "upload"; // 指向 upload.html
  }

  /**
   * 接收檔案 + 解析 EXIF + 存檔
   */
  @PostMapping("/progress/upload") // 確保路徑一致
  public String handleUpload(
      @RequestParam("file") MultipartFile file,
      @RequestParam("progressId") Long progressId,
      @RequestParam("planId") Long planId) throws Exception {

    Progress progress = progressRepository.findById(progressId)
        .orElseThrow(() -> new RuntimeException("找不到進度紀錄"));

    if (!file.isEmpty()) {
      // 1. ✨ 使用 Service 儲存實體檔案到 Mac 磁碟 (回傳 UUID 檔名)
      String fileName = photoExifService.saveImage(file);
      progress.setPhotoPath(fileName);

      // 2. ✨ 自動讀取 EXIF 並填入 Aperture, ShutterSpeed, ISO
      photoExifService.fillPhotoMetadata(file, progress);

      // 3. 更新狀態
      progress.setStatus(ProgressStatus.COMPLETED);
      progress.setCompletedAt(LocalDateTime.now());

      progressRepository.save(progress);
      System.out.println("✅ 已存檔並解析 EXIF，攝影師任務完成！");
    }

    // 完成後跳轉回儀表板
    return "redirect:/dashboard/" + planId;
  }
}