package com._OK.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemResponseDto {
    private Long problemId;
    private String type;
    private String question;
    private String answer;
    private String difficulty;
}

