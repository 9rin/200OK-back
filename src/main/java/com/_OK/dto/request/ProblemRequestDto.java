package com._OK.dto.request;

import lombok.Getter;
import java.util.List;

@Getter
public class ProblemRequestDto {
    private String model;
    private List<String> question_types;
    private Integer num_questions;
    private String difficulty;
    private String language;
}

