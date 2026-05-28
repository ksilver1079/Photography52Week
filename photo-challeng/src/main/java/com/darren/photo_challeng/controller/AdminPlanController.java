package com.darren.photo_challeng.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.darren.photo_challeng.entity.Plan;
import com.darren.photo_challeng.entity.SubTheme;
import com.darren.photo_challeng.entity.enums.Mode;
import com.darren.photo_challeng.entity.enums.ParticipantType;
import com.darren.photo_challeng.repository.PlanRepository;
import com.darren.photo_challeng.repository.SubThemeRepository;
import com.darren.photo_challeng.service.ExcelImportService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/plans")
@PreAuthorize("hasRole('ADMIN')") // 確保只有管理者能進來
@RequiredArgsConstructor
public class AdminPlanController {

    private final PlanRepository planRepository;
    private final SubThemeRepository subThemeRepository;
    private final ExcelImportService excelImportService;

    // 1. 列表頁面
    @GetMapping
    public String listPlans(Model model) {
        // 加上 OrderBy 確保顯示是按 ID 順序
        model.addAttribute("plans", planRepository.findAllByOrderByIdAsc());
        return "admin/plan_manage";
    }

    // 2. 切換上/下架
    @PostMapping("/toggle/{id}")
    public String togglePlan(@PathVariable Long id) {
        Plan plan = planRepository.findById(id).orElseThrow();
        plan.setEnabled(!plan.isEnabled());
        planRepository.save(plan);
        return "redirect:/admin/plans";
    }

    // 4. 儲存修改 (包含計畫名稱與 52 週內容)
    @PostMapping("/save")
    public String savePlan(@ModelAttribute Plan plan,
            @RequestParam("themeTitles") List<String> titles,
            @RequestParam("themeDescs") List<String> descs) {

        // 儲存計畫基本資訊
        Plan existingPlan = planRepository.findById(plan.getId()).orElseThrow();
        existingPlan.setName(plan.getName());
        planRepository.save(existingPlan);

        // 批次更新 52 週內容
        List<SubTheme> themes = subThemeRepository.findByPlanIdOrderByTopicOrderAsc(plan.getId());
        for (int i = 0; i < themes.size(); i++) {
            SubTheme t = themes.get(i);
            t.setTitle(titles.get(i));
            t.setDescription(descs.get(i));
            subThemeRepository.save(t);
        }

        return "redirect:/admin/plans?success=true";
    }

    // 1. 處理「確認建立」的請求
    @PostMapping("/add")
    @Transactional
    public String addNewPlan(@RequestParam String planName) {

        System.out.println("====== 收到新增請求，計畫名稱為: " + planName + " ======");
        Plan newPlan = new Plan();
        newPlan.setName(planName);
        newPlan.setTotalTopics(52);
        newPlan.setMinCompletionTarget(40); // 參考截圖給 40
        newPlan.setCadenceDays(7); // 參考截圖給 7
        newPlan.setMode(Mode.SEQUENTIAL); // 參考截圖給 SEQUENTIAL
        newPlan.setParticipantType(ParticipantType.INDIVIDUAL); // 參考截圖給 INDIVIDUAL
        newPlan.setEnabled(false); // 新增預設下架
        // 2. 先儲存 Plan 以取得 ID
        newPlan = planRepository.save(newPlan);

        // 3. 自動產生 52 週的主題殼子
        List<SubTheme> themes = new ArrayList<>();
        for (int i = 1; i <= 52; i++) {
            SubTheme st = new SubTheme();
            st.setPlan(newPlan);
            st.setTopicOrder(i);
            st.setTitle("W" + i + " 待設定主題");
            st.setDescription("等待填寫任務描述...");
            st.setDifficulty(1);
            themes.add(st);
        }
        subThemeRepository.saveAll(themes);

        // ✨ 關鍵：跳轉到編輯網頁，並帶著新計畫的 ID
        return "redirect:/admin/plans/edit/" + newPlan.getId();
    }

    // 2. 負責顯示「編輯網頁」
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Plan plan = planRepository.findById(id).orElseThrow();
        model.addAttribute("plan", plan);
        // 抓出這 52 週主題給網頁顯示
        model.addAttribute("subThemes", subThemeRepository.findByPlanIdOrderByTopicOrderAsc(id));

        return "admin/plan_form"; // 👈 這裡指向 admin/plan_form.html
    }

    @PostMapping("/import/{id}")
    @Transactional
    public String importExcel(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Plan plan = planRepository.findById(id).orElseThrow();

            // 1. 先刪除該計畫原本的 52 個舊主題 (避免重複)
            subThemeRepository.deleteByPlanId(id);
            subThemeRepository.flush(); // 強制讓刪除動作先生效

            /// 2. 解析 Excel
            List<SubTheme> themes = excelImportService.parseExcel(file.getInputStream(), plan);

            // 3. 存入新主題
            if (!themes.isEmpty()) {
                subThemeRepository.saveAll(themes);
            }

            return "redirect:/admin/plans?success=imported";
        } catch (Exception e) {
            e.printStackTrace(); // 在終端機印出詳細錯誤
            return "redirect:/admin/plans?error=import_failed";
        }
    }
}