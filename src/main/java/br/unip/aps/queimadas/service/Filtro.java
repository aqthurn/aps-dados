package br.unip.aps.queimadas.service;

import br.unip.aps.queimadas.model.FocoQueimada;

import java.util.ArrayList;
import java.util.List;

public record Filtro(Integer ano, String bioma, Integer amostra) {

    public Filtro {
        if (amostra != null && amostra < 1) {
            throw new IllegalArgumentException("A amostra deve ter pelo menos 1 registro.");
        }
        if (bioma != null && bioma.isBlank()) {
            bioma = null;
        }
    }

    public List<FocoQueimada> aplicar(List<FocoQueimada> focos) {
        List<FocoQueimada> resultado = new ArrayList<>();
        for (FocoQueimada f : focos) {
            if (amostra != null && resultado.size() >= amostra) {
                break;
            }
            if (ano != null && f.ano() != ano) {
                continue;
            }
            if (bioma != null && !f.bioma().equals(bioma)) {
                continue;
            }
            resultado.add(f);
        }
        return resultado;
    }
}
