package com.darren.photo_challeng.dto;

import lombok.Data;

@Data
public class ProgressItemDTO {
  private Integer weekNum;
  private String title;
  private String description;
  private String status;
  private Long progressId;
  private Long subThemeId; // 新增：供上傳連結使用

  private String category;

  // ✨ 這裡一定要補，前端才抓得到
  private String photoPath;
  private String aperture;
  private String shutterSpeed;
  private String iso;
  private String deadlineIso;
}