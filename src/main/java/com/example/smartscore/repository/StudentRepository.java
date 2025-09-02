package com.example.smartscore.repository;

import com.example.smartscore.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s JOIN FETCH s.classRoom WHERE s.id = :id")
    Optional<Student> findStudentById(Long id);
}
