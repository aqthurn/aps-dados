package br.unip.aps.queimadas.model;

import java.time.LocalDateTime;

public record FocoQueimada(long id, LocalDateTime dataHora, String municipio, String bioma,
                           double latitude, double longitude) {
}
