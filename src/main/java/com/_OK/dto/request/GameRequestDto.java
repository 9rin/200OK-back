package com._OK.dto.request;

import lombok.Getter;
import java.util.List;

@Getter
public class GameRequestDto {
    private String title;
    private List<Long> problem_ids;
}
