package com.darren.photo_challeng.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import jakarta.persistence.*;
import lombok.ToString;

@Entity
@Table(name = "sub_themes")
@Data // 一般開發最常用，自動生成所有基礎方法
public class SubTheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 實務必做：ManyToOne 預設是 EAGER，
     * 為了效能，一般都會手動改為 LAZY。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    @ToString.Exclude // 實務必加：防止 Lombok 生成 toString 時造成無限遞迴 (StackOverflow)
    private Plan plan;

    private Integer topicOrder;

    @Column(nullable = false)
    private String title;

    private String description;

    private Integer difficulty;
}