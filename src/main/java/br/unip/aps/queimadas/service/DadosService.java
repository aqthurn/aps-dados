package br.unip.aps.queimadas.service;

import br.unip.aps.queimadas.model.Criterio;
import br.unip.aps.queimadas.model.FocoQueimada;
import br.unip.aps.queimadas.ordenacao.Algoritmo;
import br.unip.aps.queimadas.ordenacao.Resultado;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DadosService {

    private final List<FocoQueimada> focos = new ArrayList<>();

    public int importar(String conteudo) {
        List<FocoQueimada> novos = LeitorCsv.ler(conteudo);
        focos.addAll(novos);
        return novos.size();
    }

    public void baixarDoInpe(String uf, int ano) {
        throw new UnsupportedOperationException("Download do INPE ainda não implementado.");
    }

    public void limpar() {
        focos.clear();
    }

    public int total() {
        return focos.size();
    }

    public record Ordenacao(Resultado resultado, List<FocoQueimada> itens) {}

    public Ordenacao ordenar(Algoritmo algoritmo, Criterio criterio, boolean decrescente, int limite) {
        if (focos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum dado carregado.");
        }
        FocoQueimada[] vetor = focos.toArray(new FocoQueimada[0]);
        Resultado resultado = algoritmo.criar().ordenar(vetor, criterio.comparador(decrescente));
        List<FocoQueimada> itens = List.of(vetor).subList(0, Math.min(limite, vetor.length));
        return new Ordenacao(resultado, itens);
    }

    public List<Resultado> compararTodos(Criterio criterio) {
        throw new UnsupportedOperationException("Comparação entre algoritmos ainda não implementada.");
    }
}
