package com._OK.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameResultResponseDto {
    private int score;
    private int totalAnswerNumber;
    private int wrongAnswerNumber;
}

