package com.darren.photo_challeng.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.darren.photo_challeng.entity.SubTheme;

@Repository
public interface SubThemeRepository extends JpaRepository<SubTheme, Long> {

    // Spring Data JPA 會根據方法名稱自動生成 SQL:
    // SELECT * FROM sub_themes WHERE plan_id = ? ORDER BY topic_order ASC
    List<SubTheme> findByPlanIdOrderByTopicOrderAsc(Long planId);

    @Modifying // 告訴 JPA 這是會變動資料的操作
    @Query("DELETE FROM SubTheme s WHERE s.plan.id = :planId") // 確保刪除邏輯正確
    void deleteByPlanId(@Param("planId") Long planId);
}
