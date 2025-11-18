package com._OK.service;

import com._OK.domain.PDFDocument;
import com._OK.domain.Problem;
import com._OK.dto.request.ProblemRequestDto;
import com._OK.dto.response.ProblemResponseDto;
import com._OK.repository.PDFDocumentRepository;
import com._OK.repository.ProblemRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final PDFDocumentRepository pdfDocumentRepository;

    // FastAPI 서버 주소
    private final WebClient webClient = WebClient.create("https://fastapi-server-url");

    /**
     * FastAPI로부터 문제를 생성하고 DB에 저장
     */
    public List<ProblemResponseDto> importProblemsFromFastAPI(ProblemRequestDto requestDto) {
        try {
            // 1️⃣ FastAPI 호출
            var response = webClient.post()
                    .uri("/api/pdf/generate-questions")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(FastApiResponse.class)
                    .block();

            // 2️⃣ PDF Document 정보 저장
            PDFDocument document = PDFDocument.builder()
                    .fileName("uploaded.pdf")
                    .documentSummary("AI 자동 문제 생성 결과")
                    .modelUsed(requestDto.getModel())
                    .processingTime("2.8s")
                    .pdfPagesAnalyzed(10)
                    .questionCount(response.getQuestion_count())
                    .createdAt(LocalDateTime.now())
                    .build();

            pdfDocumentRepository.save(document);

            // 3️⃣ 문제 리스트 DB 저장
            List<Problem> problems = response.getGenerated_questions().stream()
                    .map(q -> Problem.builder()
                            .pdfDocument(document)
                            .question(q.getQuestion())
                            .answer(q.getAnswer())
                            .type(q.getType())
                            .difficulty(requestDto.getDifficulty())
                            .language(requestDto.getLanguage())
                            .createdAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            problemRepository.saveAll(problems);

            // 4️⃣ 응답 변환
            return problems.stream()
                    .map(p -> ProblemResponseDto.builder()
                            .problemId(p.getProblemId())
                            .type(p.getType())
                            .question(p.getQuestion())
                            .answer(p.getAnswer())
                            .difficulty(p.getDifficulty())
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("FastAPI 서버와의 통신에 실패했습니다.");
        }
    }

    // 개별 문제 조회
    public ProblemResponseDto getProblemById(Long problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new IllegalArgumentException("problemId 존재하지 않습니다."));

        return ProblemResponseDto.builder()
                .problemId(problem.getProblemId())
                .type(problem.getType())
                .question(problem.getQuestion())
                .answer(problem.getAnswer())
                .difficulty(problem.getDifficulty())
                .build();
    }

    // 내부 FastAPI 응답 DTO
    @Getter
    public static class FastApiResponse {
        private int question_count;
        private List<GeneratedQuestion> generated_questions;

        @Getter
        public static class GeneratedQuestion {
            private Long problemId;
            private String type;
            private String question;
            private String answer;
        }
    }
}
