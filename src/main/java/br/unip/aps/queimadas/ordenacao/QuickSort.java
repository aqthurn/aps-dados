package br.unip.aps.queimadas.ordenacao;

import br.unip.aps.queimadas.model.FocoQueimada;

public class QuickSort extends Ordenador {

    @Override
    public String nome() {
        return "Quick Sort";
    }

    @Override
    protected void ordenar() {
        quickSort(0, v.length - 1);
    }

    private void quickSort(int ini, int fim) {
        if (ini >= fim) {
            return;
        }
        FocoQueimada pivo = v[(ini + fim) / 2];
        int i = ini;
        int j = fim;
        while (i <= j) {
            while (comparar(v[i], pivo) < 0) {
                i++;
            }
            while (comparar(v[j], pivo) > 0) {
                j--;
            }
            if (i <= j) {
                trocar(i, j);
                i++;
                j--;
            }
        }
        quickSort(ini, j);
        quickSort(i, fim);
    }
}
