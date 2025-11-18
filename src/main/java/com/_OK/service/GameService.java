package com._OK.service;


import com._OK.domain.Game;
import com._OK.domain.Problem;
import com._OK.domain.ProblemGame;
import com._OK.dto.request.GameRequestDto;
import com._OK.dto.response.GameProblemResponseDto;
import com._OK.dto.response.GameResponseDto;
import com._OK.dto.response.GameResultResponseDto;
import com._OK.repository.GameRepository;
import com._OK.repository.GameResultRepository;
import com._OK.repository.ProblemGameRepository;
import com._OK.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final ProblemRepository problemRepository;
    private final ProblemGameRepository problemGameRepository;
    private final GameResultRepository gameResultRepository;

    // 미니게임 생성
    public GameResponseDto createGame(GameRequestDto requestDto) {
        // 1️) 게임 생성
        Game game = Game.builder()
                .title(requestDto.getTitle())
                .createdAt(LocalDateTime.now())
                .build();
        gameRepository.save(game);

        // 2️) 문제 연결 (problem-game)
        List<Problem> problems = problemRepository.findAllById(requestDto.getProblem_ids());
        for (Problem problem : problems) {
            ProblemGame pg = ProblemGame.builder()
                    .game(game)
                    .problem(problem)
                    .build();
            problemGameRepository.save(pg);
        }

        // 3️) 응답 DTO
        return GameResponseDto.builder()
                .game_id(game.getGameId())
                .title(game.getTitle())
                .problem_ids(requestDto.getProblem_ids())
                .build();
    }

    // 게임에 포함된 문제 조회
    public GameProblemResponseDto getGameProblems(Long gameId) {
        List<ProblemGame> problemGames = problemGameRepository.findByGame_GameId(gameId);
        if (problemGames.isEmpty()) {
            throw new IllegalArgumentException("gameId 존재하지 않습니다.");
        }

        List<GameProblemResponseDto.ProblemDto> problems = problemGames.stream()
                .map(pg -> pg.getProblem())
                .map(p -> GameProblemResponseDto.ProblemDto.builder()
                        .id(p.getProblemId())
                        .type(p.getType())
                        .question(p.getQuestion())
                        .answer(p.getAnswer())
                        .difficulty(p.getDifficulty())
                        .build())
                .collect(Collectors.toList());

        return GameProblemResponseDto.builder()
                .gameId(gameId)
                .problems(problems)
                .build();
    }

    // (임시용) 게임 결과 조회 — 실제로는 GameResult 기반
    public GameResultResponseDto getGameResult() {
        // 실제로는 DB 계산 또는 AI 기반 점수 로직이 들어갈 수 있음.
        return GameResultResponseDto.builder()
                .score(78)
                .totalAnswerNumber(10)
                .wrongAnswerNumber(4)
                .build();
    }
}

