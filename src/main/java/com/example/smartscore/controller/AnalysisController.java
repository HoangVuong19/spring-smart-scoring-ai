package com.example.smartscore.controller;

import com.example.smartscore.dto.request.ScoreRequest;
import com.example.smartscore.dto.response.ScoreResponse;
import com.example.smartscore.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisController {
    private final AnalysisService analysisService;

    @PostMapping("")
    public String analyzeData(@RequestBody String message) {
        return analysisService.analyze(message);
    }

    @PostMapping("/grade-answer")
    public ResponseEntity<ScoreResponse> gradeAnswer(@RequestBody ScoreRequest request) {
        ScoreResponse response = analysisService.essayGrading(request);
        return ResponseEntity.ok(response);
    }
}
