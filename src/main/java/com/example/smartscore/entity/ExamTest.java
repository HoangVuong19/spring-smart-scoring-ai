package com.example.smartscore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exam_test")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String barem;
}
