package com.darren.photo_challeng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/photo")
public class PhotoController {

  /**
   * 顯示上傳頁面 (GET)
   */
  @GetMapping("/upload")
  public String showUploadPage(@RequestParam("progressId") Long progressId, Model model) {
    // 必須帶入 progressId，否則 upload.html 的隱藏欄位會報錯
    model.addAttribute("progressId", progressId);
    return "upload";
  }

  /**
   * 接收並處理檔案 (POST)
   * 對應 upload.html 的 form action
   */
  @PostMapping("/upload")
  public String handleUpload(@RequestParam("file") MultipartFile file,
      @RequestParam("progressId") Long progressId) {
    // 儲存邏輯
    return "redirect:/dashboard/1";
  }
}