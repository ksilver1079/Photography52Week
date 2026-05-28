package com.darren.photo_challeng.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter; // 👈 確保有這個
import lombok.NoArgsConstructor;
import com.darren.photo_challeng.entity.enums.ProgressStatus;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter // 👈 讓 Lombok 自動幫你生成 setUser, setPlan 等方法
@NoArgsConstructor
@Table(name = "progress")

public class Progress {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false) // 👈 加上 nullable = false，防止產生幽靈進度
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "plan_id", nullable = false)
  private Plan plan;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sub_theme_id", nullable = false)
  private SubTheme subTheme;

  @Enumerated(EnumType.STRING)
  private ProgressStatus status;

  @Column(name = "photo_url")
  private String photoUrl;

  private LocalDateTime completedAt;
  private LocalDateTime startedAt;

  private String photoPath; // 存放 saveImage 回傳的檔名

  // ✨ 新增 EXIF 欄位
  private String aperture;
  private String shutterSpeed;
  private String iso;
}