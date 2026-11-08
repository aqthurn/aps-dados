package br.unip.aps.queimadas.model;

import java.util.Comparator;

public enum Criterio {

    DATA(Comparator.comparing(FocoQueimada::dataHora)),
    BIOMA(Comparator.comparing(FocoQueimada::bioma)),
    MUNICIPIO(Comparator.comparing(FocoQueimada::municipio));

    private final Comparator<FocoQueimada> comparador;

    Criterio(Comparator<FocoQueimada> comparador) {
        this.comparador = comparador;
    }

    public Comparator<FocoQueimada> comparador(boolean decrescente) {
        return decrescente ? comparador.reversed() : comparador;
    }
}
