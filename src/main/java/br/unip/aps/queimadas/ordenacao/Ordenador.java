package br.unip.aps.queimadas.ordenacao;

import br.unip.aps.queimadas.model.FocoQueimada;

import java.util.Comparator;

public abstract class Ordenador {

    protected FocoQueimada[] v;
    private Comparator<FocoQueimada> comparador;
    private long comparacoes;
    private long trocas;

    public abstract String nome();

    protected abstract void ordenar();

    public Resultado ordenar(FocoQueimada[] vetor, Comparator<FocoQueimada> comparador) {
        this.v = vetor;
        this.comparador = comparador;
        this.comparacoes = 0;
        this.trocas = 0;
        long inicio = System.nanoTime();
        ordenar();
        double tempoMs = (System.nanoTime() - inicio) / 1_000_000.0;
        return new Resultado(nome(), vetor.length, comparacoes, trocas, tempoMs);
    }

    protected int comparar(FocoQueimada a, FocoQueimada b) {
        comparacoes++;
        return comparador.compare(a, b);
    }

    protected void trocar(int i, int j) {
        trocas++;
        FocoQueimada tmp = v[i];
        v[i] = v[j];
        v[j] = tmp;
    }

    protected void mover() {
        trocas++;
    }
}
