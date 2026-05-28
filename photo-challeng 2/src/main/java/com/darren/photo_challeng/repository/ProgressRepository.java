package com.darren.photo_challeng.repository;

import com.darren.photo_challeng.entity.Progress;
import com.darren.photo_challeng.entity.enums.ProgressStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {

  // 1. 根據使用者 ID 尋找所有進度，並按照小主題的順序排列 (給 52 格網格使用)
  List<Progress> findByParticipantIdOrderBySubThemeTopicOrderAsc(Long userId);

  // 2. 原有的方法
  List<Progress> findByParticipantIdAndStatus(Long participantId, ProgressStatus status);

  List<Progress> findByParticipantId(Long userId);

  // 加上 AndPlanId，確保不會撈到該使用者參加過的其他計畫進度
  List<Progress> findByParticipantIdAndPlanIdOrderBySubThemeTopicOrderAsc(Long userId, Long planId);

  @EntityGraph(attributePaths = { "subTheme" })
  List<Progress> findByParticipantIdAndPlanId(Long userId, Long planId);

  
  // 3. 根據計畫 ID 尋找所有進度，並按照小主題的順序排列 (給後台管理使用)
  List<Progress> findByPlanIdOrderBySubThemeTopicOrderAsc(Long planId);

}
