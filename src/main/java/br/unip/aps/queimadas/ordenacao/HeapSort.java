package br.unip.aps.queimadas.ordenacao;

public class HeapSort extends Ordenador {

    @Override
    public String nome() {
        return "Heap Sort";
    }

    @Override
    protected void ordenar() {

        int tamanho = v.length;

        // Construir o Max Heap
        for (int i = tamanho / 2 - 1; i >= 0; i--) {
            heapify(tamanho, i);
        }

        // Ordenar o vetor
        for (int i = tamanho - 1; i > 0; i--) {

            trocar(0, i);

            heapify(i, 0);
        }
    }

    private void heapify(int tamanho, int raiz) {

        int maior = raiz;

        int esquerda = 2 * raiz + 1;
        int direita = 2 * raiz + 2;

        if (esquerda < tamanho &&
                comparar(v[esquerda], v[maior]) > 0) {

            maior = esquerda;
        }

        if (direita < tamanho &&
                comparar(v[direita], v[maior]) > 0) {

            maior = direita;
        }

        if (maior != raiz) {

            trocar(raiz, maior);

            heapify(tamanho, maior);
        }
    }
}
