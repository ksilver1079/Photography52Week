package com.darren.photo_challeng.entity;

import java.time.LocalDateTime;

import com.darren.photo_challeng.entity.enums.Visibility;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "photos")
@Data
public class Photo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 1. 關聯到進度追蹤 (保留)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "progress_id", nullable = false)
  private Progress progress;

  // 2. 關聯到上傳者 (這才是對的方式)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  @Column(name = "exif_data", columnDefinition = "TEXT")
  private String exifData;

  @Column(name = "file_path", nullable = false)
  private String filePath;

  @Enumerated(EnumType.STRING)
  private Visibility visibility = Visibility.PRIVATE;

  @Column(name = "uploaded_at")
  private LocalDateTime uploadedAt = LocalDateTime.now();

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

}