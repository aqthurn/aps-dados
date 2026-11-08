package br.unip.aps.queimadas.ordenacao;

import java.util.function.Supplier;

public enum Algoritmo {

    BUBBLE(BubbleSort::new),
    SELECTION(SelectionSort::new),
    INSERTION(InsertionSort::new),
    SHELL(ShellSort::new),
    MERGE(MergeSort::new),
    QUICK(QuickSort::new),
    HEAP(HeapSort::new);

    private final Supplier<Ordenador> fabrica;

    Algoritmo(Supplier<Ordenador> fabrica) {
        this.fabrica = fabrica;
    }

    public Ordenador criar() {
        return fabrica.get();
    }
}
