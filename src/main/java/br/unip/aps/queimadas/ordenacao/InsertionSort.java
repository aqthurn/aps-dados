package br.unip.aps.queimadas.ordenacao;

import br.unip.aps.queimadas.model.FocoQueimada;

public class InsertionSort extends Ordenador {

    @Override
    public String nome() {
        return "Insertion Sort";
    }

    @Override
    protected void ordenar() {
        for (int i = 1; i < v.length; i++) {
            FocoQueimada atual = v[i];
            int j = i - 1;
            while (j >= 0 && comparar(v[j], atual) > 0) {
                v[j + 1] = v[j];
                mover();
                j--;
            }
            v[j + 1] = atual;
        }
    }
}
