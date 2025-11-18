package com._OK.controller;

import com._OK.dto.request.ProblemRequestDto;
import com._OK.dto.response.ProblemResponseDto;
import com._OK.service.ProblemService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/problems")
public class ProblemController {

    private final ProblemService problemService;

    // PDF 기반 문제 생성 (FastAPI 연동)
    @PostMapping
    public ResponseEntity<?> createProblems(@RequestBody ProblemRequestDto requestDto) {
        try {
            List<ProblemResponseDto> createdProblems = problemService.importProblemsFromFastAPI(requestDto);
            return ResponseEntity.ok(createdProblems);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    new ErrorResponse(500, "FastAPI 서버와의 통신에 실패했습니다.", "/api/problems")
            );
        }
    }

    // 단일 문제 조회
    @GetMapping("/{problemId}")
    public ResponseEntity<?> getProblemById(@PathVariable Long problemId) {
        try {
            ProblemResponseDto problem = problemService.getProblemById(problemId);
            return ResponseEntity.ok(problem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(
                    new ErrorResponse(404, "problemId 존재하지 않습니다.", "/api/problems/" + problemId)
            );
        } catch (Exception e) {
            return ResponseEntity.status(400).body(
                    new ErrorResponse(400, "Illegal Argument", "/api/problems/" + problemId)
            );
        }
    }

    // 에러 응답 DTO
    @Getter
    @AllArgsConstructor
    static class ErrorResponse {
        private int status;
        private String message;
        private String path;
    }
}
