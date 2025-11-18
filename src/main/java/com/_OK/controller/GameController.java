package com._OK.controller;

import com._OK.dto.request.GameRequestDto;
import com._OK.dto.response.GameResponseDto;
import com._OK.dto.response.GameResultResponseDto;
import com._OK.service.GameService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    // 🎮 미니게임 생성
    @PostMapping
    public ResponseEntity<?> createGame(@RequestBody GameRequestDto requestDto) {
        if (requestDto.getTitle() == null || requestDto.getProblem_ids() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("R101", "Required parameter title or problem_ids is missing or invalid format")
            );
        }

        GameResponseDto response = gameService.createGame(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 📘 미니게임 문제 조회
     */
    @GetMapping("/{gameId}/problems")
    public ResponseEntity<?> getGameProblems(@PathVariable Long gameId) {
        try {
            return ResponseEntity.ok(gameService.getGameProblems(gameId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorResponse("404", "gameId 존재하지 않습니다.")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponse("400", "Illegal Argument")
            );
        }
    }

    /**
     * 📊 미니게임 결과 조회
     */
    @GetMapping("/results")
    public ResponseEntity<GameResultResponseDto> getGameResult() {
        return ResponseEntity.ok(gameService.getGameResult());
    }

    @Getter
    @AllArgsConstructor
    static class ErrorResponse {
        private String code;
        private String message;
    }
}
