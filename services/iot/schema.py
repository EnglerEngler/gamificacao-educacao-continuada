from decimal import Decimal
from enum import Enum
from uuid import UUID
from pydantic import BaseModel, Field, model_validator


class Tipo(str, Enum):
    PRESENCA = "PRESENCA"
    INICIO = "INICIO"
    CONCLUSAO = "CONCLUSAO"


class Evento(BaseModel):
    eventoId: UUID
    instituicao: str = Field(pattern=r"^[a-z0-9][a-z0-9-]{0,63}$")
    dispositivoId: str = Field(min_length=1, max_length=120)
    tipo: Tipo
    alunoId: int = Field(gt=0)
    cursoId: str = Field(min_length=1, max_length=120)
    media: Decimal | None = Field(default=None, ge=0, le=10)

    @model_validator(mode="after")
    def validar_conclusao(self):
        if self.tipo == Tipo.CONCLUSAO and self.media is None:
            raise ValueError("Conclusão exige média")
        return self


def validar_topico(topico: str, evento: Evento, instituicoes: set[str]):
    partes = topico.split("/")
    if len(partes) != 5 or partes[0] != "instituicoes" or partes[2] != "dispositivos" or partes[4] != "eventos":
        raise ValueError("Tópico inválido")
    if partes[1] != evento.instituicao or partes[3] != evento.dispositivoId:
        raise ValueError("Tópico e payload divergentes")
    if evento.instituicao not in instituicoes:
        raise ValueError("Instituição não autorizada")

