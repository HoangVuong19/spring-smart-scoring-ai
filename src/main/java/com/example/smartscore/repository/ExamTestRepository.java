package com.example.smartscore.repository;

import com.example.smartscore.dto.ExamDto;
import com.example.smartscore.entity.ExamTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface ExamTestRepository extends JpaRepository<ExamTest, Long> {

    @Query("""
                SELECT new com.example.smartscore.dto.ExamDto(
                    ex.question, ex.barem
                )
                FROM ExamTest ex
                WHERE ex.id = :examId
            """)
    ExamDto findAllWithExamId(Long examId);
}
