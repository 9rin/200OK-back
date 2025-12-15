package com._OK.controller;

import com._OK.service.LearningService;
import com._OK.dto.response.ProblemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/learning")
public class LearningController {

    private final LearningService learningService;

    /**
     * 오답 기반 집중학습 문제 생성 API
     * POST /api/learning/generate-focus-questions
     */
    @PostMapping("/generate-focus-questions")
    public ResponseEntity<?> generateFocusQuestions(@RequestBody FocusRequest request) {
        try {
            List<ProblemResponseDto> generated = learningService.generateFocusQuestions(request.getError_stats());
            return ResponseEntity.ok(generated);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorResponse(500, "FastAPI 서버와의 통신에 실패했습니다: " + e.getMessage(), "/api/learning/generate-focus-questions")
            );
        }
    }

    @Getter
    public static class FocusRequest {
        private List<ErrorStat> error_stats;
        private String model = "gpt-4o-mini";
    }

    @Getter
    public static class ErrorStat {
        private Long problemId;
        private Double wrong_rate;
    }
}
