package br.unip.aps.queimadas.ordenacao;

import br.unip.aps.queimadas.model.FocoQueimada;

public class ShellSort extends Ordenador {

    @Override
    public String nome() {
        return "Shell Sort";
    }

    @Override
    protected void ordenar() {
        int tamanho = v.length;
        for (int gap = tamanho / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < tamanho; i++) {
                FocoQueimada temp = v[i];
                int j = i;
                while (j >= gap && comparar(v[j - gap], temp) > 0) {
                    v[j] = v[j - gap];
                    mover();
                    j -= gap;
                }
                v[j] = temp;
                mover();
            }
        }
    }
}
