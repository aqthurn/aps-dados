package br.unip.aps.queimadas.service;

import br.unip.aps.queimadas.model.FocoQueimada;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LeitorCsv {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public record Leitura(List<FocoQueimada> focos, int invalidas) {}

    public static Leitura ler(String nomeArquivo, byte[] conteudo) throws IOException {
        if (nomeArquivo.toLowerCase(Locale.ROOT).endsWith(".zip")) {
            return lerZip(conteudo);
        }
        return lerCsv(new String(conteudo, StandardCharsets.UTF_8));
    }

    private static Leitura lerZip(byte[] conteudo) throws IOException {
        List<FocoQueimada> focos = new ArrayList<>();
        int invalidas = 0;
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(conteudo))) {
            ZipEntry entrada;
            while ((entrada = zip.getNextEntry()) != null) {
                if (entrada.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
                    Leitura leitura = lerCsv(new String(zip.readAllBytes(), StandardCharsets.UTF_8));
                    focos.addAll(leitura.focos());
                    invalidas += leitura.invalidas();
                }
            }
        }
        return new Leitura(focos, invalidas);
    }

    public static Leitura lerCsv(String texto) {
        String[] linhas = texto.replace("﻿", "").split("\r?\n");
        List<String> cabecalho = Arrays.asList(linhas[0].trim().toLowerCase(Locale.ROOT).split(","));
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
        int invalidas = 0;
        for (int i = 1; i < linhas.length; i++) {
            if (linhas[i].isBlank()) {
                continue;
            }
            String[] c = linhas[i].split(",");
            try {
                String municipio = c[cMunicipio].trim();
                String bioma = c[cBioma].trim();
                if (municipio.isEmpty()) {
                    invalidas++;
                    continue;
                }
                focos.add(new FocoQueimada(
                        cId >= 0 ? Long.parseLong(c[cId].trim()) : i,
                        LocalDateTime.parse(c[cData].trim(), FORMATO),
                        municipio,
                        bioma.isEmpty() ? "Não informado" : bioma,
                        cLat >= 0 ? Double.parseDouble(c[cLat].trim()) : 0,
                        cLon >= 0 ? Double.parseDouble(c[cLon].trim()) : 0));
            } catch (RuntimeException e) {
                invalidas++;
            }
        }
        return new Leitura(focos, invalidas);
    }
}
