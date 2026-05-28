# Photo Challenge 52 - 專案導引

## 1. 專案脈絡 (Context)
- **目標**：開發一個 52 週攝影挑戰系統，讓使用者每週根據主題上傳照片。
- **技術棧**：Java 21, Spring Boot 3.x, MySQL, Spring Security (JWT).
- **當前階段**：已完成資料庫設計與初步專案架構，準備實作照片上傳與挑戰邏輯。

## 2. 關鍵指令 (Commands)
- **建置專案**：`./mvnw clean install`
- **啟動開發伺服器**：`./mvnw spring-boot:run`
- **執行測試**：`./mvnw test`
- **資料庫遷移**：(如果你有使用 Liquibase 或 Flyway，請寫在此處)

## 3. 開發規範 (Style Guide)
- **命名規範**：
  - 資料庫欄位使用 `snake_case` (例如：`user_id`)。
  - Java 變數與方法使用 `camelCase` (例如：`userId`)。
- **架構模式**：嚴格遵守 Controller -> Service -> Repository 模式。
- **錯誤處理**：使用全域異常處理，API 回傳格式一律為 `Result<T>`。
- **圖片存儲**：實體檔案存放於專案根目錄的 `uploads/` 資料夾，資料庫僅記錄路徑。

## 4. 資料庫架構摘要 (Database)
- **Users**: id, email, password (BCrypt), display_name,role.
- **Plans**: id, name, total_topics, min_completion_tagrget, cadence_days, mode, participant_type, is_enabled.
- **Progress**: id, plan_id, sub_themes, participantType, status, user_id, shutter_speed, aperture, iso, photo_path.
- **Sub_themes**: id, plan_id, topic_order, title, description, difficulty, category.

