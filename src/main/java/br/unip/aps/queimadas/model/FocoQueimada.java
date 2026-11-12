package br.unip.aps.queimadas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Locale;

public record FocoQueimada(long id, LocalDateTime dataHora, String municipio, String bioma,
                           double latitude, double longitude,
                           @JsonIgnore String chaveMunicipio, @JsonIgnore String chaveBioma) {

    public FocoQueimada(long id, LocalDateTime dataHora, String municipio, String bioma,
                        double latitude, double longitude) {
        this(id, dataHora, municipio, bioma, latitude, longitude, normalizar(municipio), normalizar(bioma));
    }

    public int ano() {
        return dataHora.getYear();
    }

    public static String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT);
    }
}
