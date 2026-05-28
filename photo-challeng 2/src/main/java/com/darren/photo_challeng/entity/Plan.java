package com.darren.photo_challeng.entity;

import java.util.List;

import com.darren.photo_challeng.entity.enums.ParticipantType;
import com.darren.photo_challeng.entity.enums.Mode;
import jakarta.persistence.CascadeType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "plans")
@Data // Lombok 自動生成 Getter/Setter
public class Plan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private Integer totalTopics = 52;
  private Integer minCompletionTarget;
  private Integer cadenceDays;

  @Enumerated(EnumType.STRING)
  private Mode mode; // SEQUENTIAL, RANDOM

  @Enumerated(EnumType.STRING)
  private ParticipantType participantType; // USER, TEAM

  // 一個計畫擁有多個小主題
  @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
  private List<SubTheme> subThemes;

  public void addSubTheme(SubTheme subTheme) {
    subThemes.add(subTheme);
    subTheme.setPlan(this); // 這是關鍵：必須手動維護雙向關聯
  }

}
