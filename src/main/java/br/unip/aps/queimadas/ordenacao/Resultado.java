package br.unip.aps.queimadas.ordenacao;

public record Resultado(String algoritmo, int tamanho, long comparacoes, long trocas, long movimentacoes,
                        long totalOperacoes, double tempoMs) {
}
