package com.darren.photo_challeng.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.darren.photo_challeng.entity.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    // 預設已包含 findAll(), findById(), save(), delete()
}
