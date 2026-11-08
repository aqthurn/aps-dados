package br.unip.aps.queimadas.ordenacao;

public class BubbleSort extends Ordenador {

    @Override
    public String nome() {
        return "Bubble Sort";
    }

    @Override
    protected void ordenar() {
        for (int fim = v.length - 1; fim > 0; fim--) {
            for (int i = 0; i < fim; i++) {
                if (comparar(v[i], v[i + 1]) > 0) {
                    trocar(i, i + 1);
                }
            }
        }
    }
}
