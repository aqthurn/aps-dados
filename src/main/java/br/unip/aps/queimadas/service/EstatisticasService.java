package br.unip.aps.queimadas.service;

import br.unip.aps.queimadas.model.FocoQueimada;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class EstatisticasService {

    public record Contagem(String nome, long focos) {}

    public record Resumo(int total, int municipios, Map<Integer, Long> porAno, Map<Integer, long[]> porMes,
                         List<Contagem> porBioma, List<Contagem> topMunicipios) {}

    private final DadosService dados;

    public EstatisticasService(DadosService dados) {
        this.dados = dados;
    }

    public Resumo resumo(Filtro filtro) {
        List<FocoQueimada> focos = filtro.aplicar(dados.todos());
        if (focos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum dado para os filtros escolhidos.");
        }

        Map<Integer, Long> porAno = new TreeMap<>();
        Map<Integer, long[]> porMes = new TreeMap<>();
        Map<String, Long> porBioma = new HashMap<>();
        Map<String, Long> porMunicipio = new HashMap<>();

        for (FocoQueimada f : focos) {
            porAno.merge(f.ano(), 1L, Long::sum);
            porMes.computeIfAbsent(f.ano(), a -> new long[12])[f.dataHora().getMonthValue() - 1]++;
            porBioma.merge(f.bioma(), 1L, Long::sum);
            porMunicipio.merge(f.municipio(), 1L, Long::sum);
        }

        return new Resumo(focos.size(), porMunicipio.size(), porAno, porMes,
                ranking(porBioma, Integer.MAX_VALUE), ranking(porMunicipio, 10));
    }

    public List<Integer> anos() {
        return dados.todos().stream().map(FocoQueimada::ano).distinct().sorted().toList();
    }

    public List<String> biomas() {
        return dados.todos().stream().map(FocoQueimada::bioma).distinct().sorted().toList();
    }

    private static List<Contagem> ranking(Map<String, Long> contagens, int limite) {
        return contagens.entrySet().stream()
                .map(e -> new Contagem(e.getKey(), e.getValue()))
                .sorted(Comparator.comparingLong(Contagem::focos).reversed())
                .limit(limite)
                .toList();
    }
}
