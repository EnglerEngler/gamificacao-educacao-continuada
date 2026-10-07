package br.edu.unifacens.gamificacao.ia.application;
import java.util.Map;
public interface IaPort {
    Map<String,Object> recomendar(String interesse);
    Map<String,Object> perguntar(String pergunta);
}

