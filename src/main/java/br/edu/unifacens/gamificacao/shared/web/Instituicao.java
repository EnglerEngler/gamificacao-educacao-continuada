package br.edu.unifacens.gamificacao.shared.web;
public final class Instituicao {
    private Instituicao() {}
    public static String validar(String value) {
        if (value == null || !value.matches("[a-z0-9][a-z0-9-]{0,63}"))
            throw new IllegalArgumentException("Instituição inválida");
        return value;
    }
}

