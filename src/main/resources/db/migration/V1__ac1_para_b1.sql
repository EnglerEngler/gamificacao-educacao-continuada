-- Migração explícita para uma base PostgreSQL da AC1 (faça backup antes).
-- A microsolução em banco vazio usa Hibernate update; operação final deve usar migrations.
ALTER TABLE alunos ADD COLUMN IF NOT EXISTS instituicao VARCHAR(64) NOT NULL DEFAULT 'ac1';
ALTER TABLE alunos ADD COLUMN IF NOT EXISTS cursos_concluidos INTEGER NOT NULL DEFAULT 0;
ALTER TABLE alunos ADD COLUMN IF NOT EXISTS moedas INTEGER NOT NULL DEFAULT 0;
ALTER TABLE alunos ADD COLUMN IF NOT EXISTS versao BIGINT NOT NULL DEFAULT 0;
CREATE INDEX IF NOT EXISTS idx_aluno_instituicao ON alunos(instituicao);
-- Índice de expressão no PostgreSQL, específico para o read model do ranking.
CREATE INDEX IF NOT EXISTS idx_aluno_ranking ON alunos(instituicao, (cursos_concluidos*100+moedas) DESC, id);

