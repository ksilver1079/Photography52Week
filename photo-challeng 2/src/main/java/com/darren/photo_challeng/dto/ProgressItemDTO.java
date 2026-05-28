package com.darren.photo_challeng.dto;

import lombok.Data;

@Data
public class ProgressItemDTO {
  private Long progressId;
  private Integer weekNum; // 對應 topicOrder
  private String title; // 主題名稱
  private String description; // 任務描述 (你剛才要求的邏輯)
  private String status; // "COMPLETED" 或 "IN_PROGRESS"
}