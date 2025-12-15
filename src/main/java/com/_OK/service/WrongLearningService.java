package com._OK.service;

import com._OK.domain.Problem;
import com._OK.dto.response.ProblemResponseDto;
import com._OK.repository.ProblemRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LearningService {

    private final ProblemRepository problemRepository;

    @Value("${fastapi.url}")
    private String fastApiUrl;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(fastApiUrl)
                .build();
    }

    /**
     * FastAPI에 틀린 문제를 보내서
     * 집중학습 문제를 새로 생성하는 함수
     */
    public List<ProblemResponseDto> generateFocusQuestions(List<ErrorStat> stats) {

        try {
            // 1️⃣ 기존 문제 DB에서 가져오기
            List<Problem> wrongProblems = problemRepository.findAllById(wrongProblemIds);

            // 2️⃣ FastAPI 요청 Body 구성
            FocusRequest request = new FocusRequest(
                    wrongProblems.stream()
                            .map(Problem::getQuestion)
                            .collect(Collectors.toList())
            );

            // 3️⃣ FastAPI 호출
            FocusApiResponse response = webClient.post()
                    .uri("/api/learning/generate-focus-questions/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(FocusApiResponse.class)
                    .timeout(Duration.ofMinutes(3))
                    .block();

            if (response == null || response.getGenerated_questions() == null) {
                throw new RuntimeException("FastAPI 응답 없음");
            }

            // 4️⃣ 새 문제 DB 저장
            List<Problem> newProblems = response.getGenerated_questions().stream()
                    .map(g -> Problem.builder()
                            .question(g.getQuestion())
                            .answer(g.getAnswer())
                            .type(g.getType())
                            .difficulty("focus")
                            .language("ko")
                            .createdAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            problemRepository.saveAll(newProblems);

            // 5️⃣ 원하는 DTO로 변환
            return newProblems.stream()
                    .map(p -> ProblemResponseDto.builder()
                            .problemId(p.getProblemId())
                            .question(p.getQuestion())
                            .answer(p.getAnswer())
                            .type(p.getType())
                            .difficulty(p.getDifficulty())
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("FastAPI 통신 실패: " + e.getMessage(), e);
        }
    }

    // 요청 DTO
    @Getter
    public static class FocusRequest {
        private final List<String> wrong_questions;

        public FocusRequest(List<String> wrong_questions) {
            this.wrong_questions = wrong_questions;
        }
    }

    // 응답 DTO
    @Getter
    public static class FocusApiResponse {
        private int question_count;
        private List<GeneratedQuestion> generated_questions;

        @Getter
        public static class GeneratedQuestion {
            private String type;
            private String question;
            private String answer;
        }
    }
}
