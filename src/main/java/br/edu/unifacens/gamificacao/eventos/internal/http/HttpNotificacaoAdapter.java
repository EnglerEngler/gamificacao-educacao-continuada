package br.edu.unifacens.gamificacao.eventos.internal.http;
import br.edu.unifacens.gamificacao.eventos.application.NotificacaoPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Component
public class HttpNotificacaoAdapter implements NotificacaoPort {
    private final RestClient client;
    public HttpNotificacaoAdapter(@Value("${b1.notificacao.url}") String url,
                                  @Value("${b1.integration.token}") String token) {
        var factory=new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(250); factory.setReadTimeout(700);
        client=RestClient.builder().baseUrl(url).requestFactory(factory)
            .defaultHeader("X-Integration-Token", token).build();
    }
    public void enviar(String payload) {
        client.post().uri("/eventos").contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            .body(payload).retrieve().toBodilessEntity();
    }
}

