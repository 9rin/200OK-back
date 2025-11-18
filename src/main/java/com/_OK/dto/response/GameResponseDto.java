package com._OK.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameResponseDto {
    private Long game_id;
    private String title;
    private List<Long> problem_ids;
}

