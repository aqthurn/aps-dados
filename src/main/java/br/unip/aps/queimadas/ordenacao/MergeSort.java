package br.unip.aps.queimadas.ordenacao;

import br.unip.aps.queimadas.model.FocoQueimada;

public class MergeSort extends Ordenador {

    @Override
    public String nome() {
        return "Merge Sort";
    }

    @Override
    protected void ordenar() {
        mergeSort(0, v.length - 1);
    }

    private void mergeSort(int ini, int fim) {
        if (ini >= fim) {
            return;
        }
        int meio = (ini + fim) / 2;
        mergeSort(ini, meio);
        mergeSort(meio + 1, fim);
        intercalar(ini, meio, fim);
    }

    private void intercalar(int ini, int meio, int fim) {
        FocoQueimada[] aux = new FocoQueimada[fim - ini + 1];
        int i = ini;
        int j = meio + 1;
        int k = 0;
        while (i <= meio && j <= fim) {
            if (comparar(v[i], v[j]) <= 0) {
                aux[k++] = v[i++];
            } else {
                aux[k++] = v[j++];
            }
        }
        while (i <= meio) {
            aux[k++] = v[i++];
        }
        while (j <= fim) {
            aux[k++] = v[j++];
        }
        for (k = 0; k < aux.length; k++) {
            v[ini + k] = aux[k];
            mover();
        }
    }
}
