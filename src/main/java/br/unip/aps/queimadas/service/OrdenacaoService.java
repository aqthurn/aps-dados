package br.unip.aps.queimadas.service;

import br.unip.aps.queimadas.model.Criterio;
import br.unip.aps.queimadas.model.FocoQueimada;
import br.unip.aps.queimadas.ordenacao.Algoritmo;
import br.unip.aps.queimadas.ordenacao.Resultado;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class OrdenacaoService {

    public static final int LIMITE_QUADRATICO = 20_000;

    public record Ordenado(FocoQueimada[] focos, Resultado resultado) {}

    public record Pagina(Resultado resultado, int total, int pagina, int totalPaginas, List<FocoQueimada> itens) {}

    public record Comparacao(String algoritmo, String complexidade, Resultado resultado, Boolean correto,
                             String observacao) {}

    private final DadosService dados;

    public OrdenacaoService(DadosService dados) {
        this.dados = dados;
    }

    public Ordenado ordenar(Algoritmo algoritmo, Criterio criterio, boolean decrescente, Filtro filtro) {
        FocoQueimada[] focos = selecionar(filtro);
        if (algoritmo.quadratico() && focos.length > LIMITE_QUADRATICO) {
            throw new IllegalArgumentException(algoritmo.criar().nome() + " é O(n²) e a seleção tem "
                    + focos.length + " registros. Use uma amostra de até " + LIMITE_QUADRATICO
                    + " registros ou outro algoritmo.");
        }
        Resultado resultado = algoritmo.criar().ordenar(focos, criterio.comparador(decrescente));
        return new Ordenado(focos, resultado);
    }

    public Pagina ordenarPagina(Algoritmo algoritmo, Criterio criterio, boolean decrescente, Filtro filtro,
                                int pagina, int tamanho) {
        if (pagina < 0 || tamanho < 1 || tamanho > 500) {
            throw new IllegalArgumentException("Página ou tamanho de página inválido.");
        }
        Ordenado ordenado = ordenar(algoritmo, criterio, decrescente, filtro);
        int total = ordenado.focos().length;
        int totalPaginas = Math.max(1, (total + tamanho - 1) / tamanho);
        int inicio = Math.min(pagina * tamanho, total);
        int fim = Math.min(inicio + tamanho, total);
        List<FocoQueimada> itens = Arrays.asList(Arrays.copyOfRange(ordenado.focos(), inicio, fim));
        return new Pagina(ordenado.resultado(), total, pagina, totalPaginas, itens);
    }

    public List<Comparacao> compararTodos(Criterio criterio, boolean decrescente, Filtro filtro) {
        FocoQueimada[] original = selecionar(filtro);
        Comparator<FocoQueimada> comparador = criterio.comparador(decrescente);

        FocoQueimada[] referencia = original.clone();
        Algoritmo.MERGE.criar().ordenar(referencia, comparador);

        List<Comparacao> comparacoes = new ArrayList<>();
        for (Algoritmo algoritmo : Algoritmo.values()) {
            String nome = algoritmo.criar().nome();
            if (algoritmo.quadratico() && original.length > LIMITE_QUADRATICO) {
                comparacoes.add(new Comparacao(nome, algoritmo.complexidade(), null, null,
                        "Ignorado: mais de " + LIMITE_QUADRATICO + " registros."));
                continue;
            }
            FocoQueimada[] copia = original.clone();
            Resultado resultado = algoritmo.criar().ordenar(copia, comparador);
            comparacoes.add(new Comparacao(nome, algoritmo.complexidade(), resultado,
                    Arrays.equals(copia, referencia), null));
        }
        return comparacoes;
    }

    private FocoQueimada[] selecionar(Filtro filtro) {
        List<FocoQueimada> todos = dados.todos();
        if (todos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum dado carregado.");
        }
        List<FocoQueimada> filtrados = filtro.aplicar(todos);
        if (filtrados.isEmpty()) {
            throw new IllegalArgumentException("Nenhum foco corresponde aos filtros escolhidos.");
        }
        return filtrados.toArray(new FocoQueimada[0]);
    }
}
