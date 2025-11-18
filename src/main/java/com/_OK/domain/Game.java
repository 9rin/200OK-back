package com._OK.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    @Column(nullable = false, length = 50)
    private String title;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<ProblemGame> problemGames;
}
