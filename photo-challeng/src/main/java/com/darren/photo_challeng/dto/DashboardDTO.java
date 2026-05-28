package com.darren.photo_challeng.dto;

import java.util.List;

import lombok.Data;

@Data
public class DashboardDTO {
    private String planName;
    private Integer totalWeeks;
    private Double completionRate;
    private List<ProgressItemDTO> weeks;
}
