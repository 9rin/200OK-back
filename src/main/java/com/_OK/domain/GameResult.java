package com._OK.domain;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "gameResult")
public class GameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameResultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gameId")
    private Game game;

    private int score;

    @Column(columnDefinition = "jsonb")
    private String accuracyByType;   // {"objective":0.8, "subjective":0.6}

    @Column(columnDefinition = "jsonb")
    private String conceptScores;    // [{"concept":"분수의 덧셈", "accuracy":0.7}]

    @Column(columnDefinition = "jsonb")
    private String wrongConcepts;    // ["분수의 나눗셈"]

    @Column(columnDefinition = "jsonb")
    private String strongConcepts;   // ["입체도형의 겉넓이"]

    @Column(columnDefinition = "jsonb")
    private String wrongProblemList; // [1,3,4]

    private LocalDateTime createdAt;
}
