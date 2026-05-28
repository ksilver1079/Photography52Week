package com.darren.photo_challeng.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darren.photo_challeng.entity.SubTheme;

@Repository
public interface SubThemeRepository extends JpaRepository<SubTheme, Long> {
    
    // Spring Data JPA 會根據方法名稱自動生成 SQL: 
    // SELECT * FROM sub_themes WHERE plan_id = ? ORDER BY topic_order ASC
    List<SubTheme> findByPlanIdOrderByTopicOrderAsc(Long planId);
}