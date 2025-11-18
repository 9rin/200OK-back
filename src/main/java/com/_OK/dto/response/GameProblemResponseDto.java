package com._OK.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameProblemResponseDto {

    private Long gameId;
    private List<ProblemDto> problems;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProblemDto {
        private Long id;
        private String type;
        private String question;
        private String answer;
        private String difficulty;
    }
}

