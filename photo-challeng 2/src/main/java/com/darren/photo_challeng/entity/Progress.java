package com.darren.photo_challeng.entity;

import java.time.LocalDateTime;
import com.darren.photo_challeng.entity.enums.ParticipantType;
import com.darren.photo_challeng.entity.enums.ProgressStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "progress")
@Data
public class Progress {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) // 實務建議用 LAZY 提升效能
  @JoinColumn(name = "plan_id", nullable = false)
  private Plan plan;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sub_theme_id")
  private SubTheme subTheme;

  @Enumerated(EnumType.STRING)
  private ParticipantType participantType;

  private Long participantId;

  @Enumerated(EnumType.STRING)
  private ProgressStatus status = ProgressStatus.ACTIVE;

  private LocalDateTime startedAt = LocalDateTime.now();

  private LocalDateTime completedAt;

  public void setUser(User user) {
   
    throw new UnsupportedOperationException("Unimplemented method 'setUser'");
  }

  @Column(name = "photo_url")
  private String photoUrl; // 👈 必須有這個欄位

}