package br.unip.aps.queimadas.ordenacao;

public class SelectionSort extends Ordenador {

    @Override
    public String nome() {
        return "Selection Sort";
    }

    @Override
    protected void ordenar() {
        for (int i = 0; i < v.length - 1; i++) {
            int menor = i;
            for (int j = i + 1; j < v.length; j++) {
                if (comparar(v[j], v[menor]) < 0) {
                    menor = j;
                }
            }
            trocar(i, menor);
        }
    }
}
