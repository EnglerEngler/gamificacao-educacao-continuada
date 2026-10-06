package br.edu.unifacens.gamificacao.iot.api;
import br.edu.unifacens.gamificacao.aluno.api.*;
import br.edu.unifacens.gamificacao.iot.internal.persistence.*;
import br.edu.unifacens.gamificacao.shared.web.Instituicao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@RestController @RequestMapping("/api/iot/eventos")
public class IotController {
    public enum Tipo { PRESENCA, INICIO, CONCLUSAO }
    public record Evento(@NotNull UUID eventoId, @NotBlank String instituicao,
                         @NotBlank @Size(max=120) String dispositivoId, @NotNull Tipo tipo,
                         @NotNull @Positive Long alunoId, @NotBlank @Size(max=120) String cursoId,
                         @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal media) {}
    private final ConcluirCurso alunos;
    private final IotRepository repository;
    private final MeterRegistry metrics;
    private final String token;
    public IotController(ConcluirCurso alunos, IotRepository repository, MeterRegistry metrics,
                         @Value("${b1.integration.token}") String token) {
        this.alunos=alunos; this.repository=repository; this.metrics=metrics; this.token=token;
    }
    @PostMapping @Transactional
    public Map<String,Object> receber(
        @RequestHeader(value="X-Integration-Token",defaultValue="") String recebido,
        @RequestHeader("X-Instituicao") String tenant, @Valid @RequestBody Evento evento) {
        if (!MessageDigest.isEqual(token.getBytes(StandardCharsets.UTF_8), recebido.getBytes(StandardCharsets.UTF_8)))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Instituicao.validar(tenant);
        if (!tenant.equals(evento.instituicao())) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        if (repository.existsByInstituicaoAndEventoId(tenant,evento.eventoId())) {
            metrics.counter("b1.iot.events","resultado","duplicado").increment();
            return Map.of("status","duplicado","eventoId",evento.eventoId());
        }
        if (evento.tipo()==Tipo.CONCLUSAO) {
            if (evento.media()==null) throw new IllegalArgumentException("Conclusão exige média");
            alunos.concluirCurso(tenant,evento.alunoId(),
                new ConcluirCursoRequest(evento.media(),true,evento.cursoId(),evento.eventoId()));
        }
        repository.saveAndFlush(new IotReceipt(tenant,evento.eventoId(),evento.dispositivoId(),evento.tipo().name()));
        metrics.counter("b1.iot.events","resultado","aceito").increment();
        return Map.of("status","aceito","eventoId",evento.eventoId());
    }
}

