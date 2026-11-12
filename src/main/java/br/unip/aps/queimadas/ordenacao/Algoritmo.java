package br.unip.aps.queimadas.ordenacao;

import java.util.function.Supplier;

public enum Algoritmo {

    BUBBLE(BubbleSort::new, "O(n²)", true),
    SELECTION(SelectionSort::new, "O(n²)", true),
    INSERTION(InsertionSort::new, "O(n²)", true),
    SHELL(ShellSort::new, "O(n^1,5)", false),
    MERGE(MergeSort::new, "O(n log n)", false),
    QUICK(QuickSort::new, "O(n log n)", false),
    HEAP(HeapSort::new, "O(n log n)", false);

    private final Supplier<Ordenador> fabrica;
    private final String complexidade;
    private final boolean quadratico;

    Algoritmo(Supplier<Ordenador> fabrica, String complexidade, boolean quadratico) {
        this.fabrica = fabrica;
        this.complexidade = complexidade;
        this.quadratico = quadratico;
    }

    public Ordenador criar() {
        return fabrica.get();
    }

    public String complexidade() {
        return complexidade;
    }

    public boolean quadratico() {
        return quadratico;
    }
}
