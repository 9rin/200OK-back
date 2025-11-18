package com._OK.repository;

import com._OK.domain.ProblemGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemGameRepository extends JpaRepository<ProblemGame, Long> {
    List<ProblemGame> findByGame_GameId(Long gameId);
}

