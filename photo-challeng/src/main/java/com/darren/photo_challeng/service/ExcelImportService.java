package com.darren.photo_challeng.service;

import com.darren.photo_challeng.entity.Plan;
import com.darren.photo_challeng.entity.SubTheme;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelImportService {

  public List<SubTheme> parseExcel(InputStream is, Plan plan) throws Exception {
    List<SubTheme> themes = new ArrayList<>();
    try (Workbook workbook = new XSSFWorkbook(is)) {
      Sheet sheet = workbook.getSheetAt(0);

      // ✨ 修改點：從 i = 1 開始 (跳過第一列的中文標題)
      // 確保讀取到第 52 週，所以是 <= 52
      for (int i = 1; i <= 52; i++) {
        Row row = sheet.getRow(i);
        if (row == null)
          continue; // 防止空行

        SubTheme st = new SubTheme();
        st.setPlan(plan);

        // A 欄 (索引 0): 週次
        st.setTopicOrder((int) row.getCell(0).getNumericCellValue());

        // B 欄 (索引 1): 主題標題
        st.setTitle(row.getCell(1).getStringCellValue());

        // C 欄 (索引 2): 任務描述
        st.setDescription(row.getCell(2).getStringCellValue());

        // D 欄 (索引 3): 難度
        st.setDifficulty((int) row.getCell(3).getNumericCellValue());

        // E 欄 (索引 4): 類別
        st.setCategory(row.getCell(4).getStringCellValue());

        themes.add(st);
      }
    }
    return themes;
  }
}
