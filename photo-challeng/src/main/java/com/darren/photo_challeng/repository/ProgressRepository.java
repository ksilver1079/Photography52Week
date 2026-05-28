package com.darren.photo_challeng.repository;

import com.darren.photo_challeng.entity.Progress;
import com.darren.photo_challeng.entity.enums.ProgressStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {

  // --- 核心查詢：依使用者與計畫隔離 ---

  // 1. 獲取特定使用者在特定計畫中的 52 週進度 (OrderBy 確保格子順序正確)
  // 使用 @EntityGraph 可以一次抓完 SubTheme，避免 N+1 效能問題，讓你 M4 的速度優勢發揮出來
  @EntityGraph(attributePaths = { "subTheme" })
  List<Progress> findByUserIdAndPlanIdOrderBySubThemeTopicOrderAsc(Long userId, Long planId);

  // 2. 獲取特定使用者在特定計畫中的所有進度 (不限排序，通常給重置邏輯使用)
  List<Progress> findByUserIdAndPlanId(Long userId, Long planId);

  // --- 其他輔助查詢 ---

  // 3. 獲取使用者所有的進度 (跨計畫)
  List<Progress> findByUserIdOrderBySubThemeTopicOrderAsc(Long userId);

  // 4. 根據狀態篩選 (例如：獲取所有已完成的作品)
  List<Progress> findByUserIdAndStatus(Long userId, ProgressStatus status);

  // 5. 根據計畫 ID 查詢 (管理端使用)
  List<Progress> findByPlanIdOrderBySubThemeTopicOrderAsc(Long planId);

  Optional<Progress> findByUserIdAndSubThemeId(Long userId, Long subThemeId);

  Optional<Progress> findFirstByUserIdAndPlanIdAndStatusOrderByIdAsc(
      Long userId, Long planId, ProgressStatus status);

  List<Progress> findByUserIdAndPlanIdAndStatus(Long userId, Long planId, ProgressStatus status);
}