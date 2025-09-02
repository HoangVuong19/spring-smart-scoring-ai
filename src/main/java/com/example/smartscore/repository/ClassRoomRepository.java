package com.example.smartscore.repository;

import com.example.smartscore.dto.ClassRoomDto;
import com.example.smartscore.entity.ClassRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoom, Long> {

    @Query("""
                SELECT new com.example.smartscore.dto.ClassRoomDto(
                    cr.name, s.name, s.gender, s.literature
                )
                FROM ClassRoom cr
                JOIN cr.studentList s
            """)
    List<ClassRoomDto> findAllWithClass();
}
