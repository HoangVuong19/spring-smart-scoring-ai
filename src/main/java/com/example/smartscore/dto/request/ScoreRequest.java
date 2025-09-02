package com.example.smartscore.dto.request;

public record ScoreRequest(Long studentId, Long questionId, String answer) {
}