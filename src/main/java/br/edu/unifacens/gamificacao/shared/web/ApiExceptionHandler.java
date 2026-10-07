package br.edu.unifacens.gamificacao.shared.web;

import br.edu.unifacens.gamificacao.aluno.application.AlunoService.AlunoNaoEncontrado;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(AlunoNaoEncontrado.class)
    ResponseEntity<?> naoEncontrado(AlunoNaoEncontrado e) {
        return ResponseEntity.status(404).body(Map.of("erro", e.getMessage()));
    }
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    ResponseEntity<?> invalido(Exception e) {
        return ResponseEntity.badRequest().body(Map.of("erro", "Dados inválidos"));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<?> conflito(Exception e) {
        return ResponseEntity.status(409).body(Map.of("erro", "Evento já utilizado ou dados em conflito"));
    }
}

