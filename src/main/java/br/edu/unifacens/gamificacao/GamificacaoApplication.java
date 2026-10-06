package br.edu.unifacens.gamificacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableScheduling
public class GamificacaoApplication {
    public static void main(String[] args) { SpringApplication.run(GamificacaoApplication.class, args); }
}
