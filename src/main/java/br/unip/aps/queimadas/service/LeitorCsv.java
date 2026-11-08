package br.unip.aps.queimadas.service;

import br.unip.aps.queimadas.model.FocoQueimada;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeitorCsv {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static List<FocoQueimada> ler(String texto) {
        String[] linhas = texto.replace("\uFEFF", "").split("\r?\n");
        List<String> cabecalho = Arrays.asList(linhas[0].trim().split(","));
        int cId = cabecalho.indexOf("id_bdq");
        int cData = cabecalho.indexOf("data_pas");
        int cMunicipio = cabecalho.indexOf("municipio");
        int cBioma = cabecalho.indexOf("bioma");
        int cLat = cabecalho.indexOf("lat");
        int cLon = cabecalho.indexOf("lon");

        if (cData < 0 || cMunicipio < 0 || cBioma < 0) {
            throw new IllegalArgumentException("CSV sem as colunas data_pas, municipio e bioma.");
        }

        List<FocoQueimada> focos = new ArrayList<>();
        for (int i = 1; i < linhas.length; i++) {
            String[] c = linhas[i].split(",");
            try {
                focos.add(new FocoQueimada(
                        cId >= 0 ? Long.parseLong(c[cId].trim()) : i,
                        LocalDateTime.parse(c[cData].trim(), FORMATO),
                        c[cMunicipio].trim(),
                        c[cBioma].trim(),
                        cLat >= 0 ? Double.parseDouble(c[cLat].trim()) : 0,
                        cLon >= 0 ? Double.parseDouble(c[cLon].trim()) : 0));
            } catch (RuntimeException e) {
            }
        }
        return focos;
    }
}
