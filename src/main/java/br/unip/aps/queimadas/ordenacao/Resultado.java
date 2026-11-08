package br.unip.aps.queimadas.ordenacao;

public record Resultado(String algoritmo, int tamanho, long comparacoes, long trocas, double tempoMs) {
}
