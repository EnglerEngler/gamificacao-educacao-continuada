package br.edu.fatec.gamificacao.repository;

import br.edu.fatec.gamificacao.entity.AlunoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<AlunoEntity, Long> { }
