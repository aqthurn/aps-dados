package br.unip.aps.queimadas.model;

import java.util.Comparator;

public enum Criterio {

    DATA(Comparator.comparing(FocoQueimada::dataHora)
            .thenComparing(FocoQueimada::chaveMunicipio)
            .thenComparingLong(FocoQueimada::id)),

    BIOMA(Comparator.comparing(FocoQueimada::chaveBioma)
            .thenComparing(FocoQueimada::chaveMunicipio)
            .thenComparing(FocoQueimada::dataHora)
            .thenComparingLong(FocoQueimada::id)),

    MUNICIPIO(Comparator.comparing(FocoQueimada::chaveMunicipio)
            .thenComparing(FocoQueimada::dataHora)
            .thenComparingLong(FocoQueimada::id));

    private final Comparator<FocoQueimada> comparador;

    Criterio(Comparator<FocoQueimada> comparador) {
        this.comparador = comparador;
    }

    public Comparator<FocoQueimada> comparador(boolean decrescente) {
        return decrescente ? comparador.reversed() : comparador;
    }
}
