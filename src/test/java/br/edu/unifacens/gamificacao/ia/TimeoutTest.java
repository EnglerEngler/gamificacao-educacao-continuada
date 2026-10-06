package br.edu.unifacens.gamificacao.ia;
import br.edu.unifacens.gamificacao.ia.application.IaService;
import br.edu.unifacens.gamificacao.ia.internal.http.FastApiAdapter;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.*;

class TimeoutTest {
    @Test void servidorConectadoQueNaoRespondeTambemDegrada() throws Exception {
        var server=HttpServer.create(new InetSocketAddress("127.0.0.1",0),0);
        var executor=Executors.newSingleThreadExecutor();
        server.setExecutor(executor);
        server.createContext("/recomendacoes",exchange-> {
            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            exchange.close();
        });
        server.start();
        try {
            var service=new IaService(new FastApiAdapter("http://127.0.0.1:"+server.getAddress().getPort()),new SimpleMeterRegistry());
            long start=System.nanoTime();
            assertEquals(true,service.recomendar("java").get("degradado"));
            assertTrue((System.nanoTime()-start)/1_000_000 < 1800,"Read timeout deve impedir espera indefinida");
        } finally { server.stop(0); executor.shutdownNow(); }
    }
}

