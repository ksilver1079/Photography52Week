package com.darren.photo_challeng.repository;

import java.util.List;
import java.util.Optional; // ✨ 記得要 Import 這個

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darren.photo_challeng.entity.Photo;
import com.darren.photo_challeng.entity.User; // ✨ 記得 Import User
import com.darren.photo_challeng.entity.Progress; // ✨ 記得 Import Progress
import com.darren.photo_challeng.entity.enums.Visibility;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

  // 用來判斷「這個人」在「這週進度」是否拍過照片
  Optional<Photo> findByOwnerAndProgress(User owner, Progress progress);

  // 實作「看別人的觀點」：根據主題 ID 找出所有公開的照片
  List<Photo> findByProgress_SubTheme_IdAndVisibility(Long subThemeId, Visibility visibility);

  // 實作「團隊觀摩」：找出同一個進度下的所有照片
  List<Photo> findByProgressId(Long progressId);

  // 找出某個使用者上傳的所有照片
  List<Photo> findByOwnerId(Long ownerId);
}