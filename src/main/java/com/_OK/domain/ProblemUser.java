package com._OK.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "problem_user")
public class ProblemUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long problemTF; // ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pg_id")
    private ProblemGame problemGame;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    private Boolean isRight;
}
