package br.edu.unifacens.gamificacao.repository;

import br.edu.unifacens.gamificacao.entity.AlunoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<AlunoEntity, Long> { }
