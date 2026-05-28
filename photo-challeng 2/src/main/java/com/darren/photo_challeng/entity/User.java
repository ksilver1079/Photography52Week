package com.darren.photo_challeng.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(name = "display_name", length = 100)
  private String displayName;

  @Enumerated(EnumType.STRING)
  private Role role = Role.USER;

  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  public enum Role {
    USER, ADMIN
  }
}
