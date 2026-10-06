package br.edu.unifacens.gamificacao.ia.internal.http;
import br.edu.unifacens.gamificacao.ia.application.IaPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.core.ParameterizedTypeReference;
import java.util.Map;

@Component
public class FastApiAdapter implements IaPort {
    private final RestClient client;
    private static final ParameterizedTypeReference<Map<String,Object>> TYPE=new ParameterizedTypeReference<>() {};
    public FastApiAdapter(@Value("${b1.ia.url}") String url) {
        var factory=new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(250); factory.setReadTimeout(700);
        client=RestClient.builder().baseUrl(url).requestFactory(factory).build();
    }
    public Map<String,Object> recomendar(String interesse) {
        return client.get().uri(u -> u.path("/recomendacoes").queryParam("interesse",interesse).build())
            .retrieve().body(TYPE);
    }
    public Map<String,Object> perguntar(String pergunta) {
        return client.post().uri("/assistente").body(Map.of("pergunta",pergunta)).retrieve().body(TYPE);
    }
}

