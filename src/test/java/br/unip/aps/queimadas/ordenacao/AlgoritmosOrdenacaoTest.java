package br.unip.aps.queimadas.ordenacao;

import br.unip.aps.queimadas.model.Criterio;
import br.unip.aps.queimadas.model.FocoQueimada;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class AlgoritmosOrdenacaoTest {

    private static final String[] MUNICIPIOS = {"AVARÉ", "ÁGUAS DE LINDÓIA", "BAURU", "UCHOA", "CASA BRANCA"};
    private static final String[] BIOMAS = {"Cerrado", "Mata Atlântica"};

    private static FocoQueimada[] gerar(int n) {
        Random r = new Random(1);
        FocoQueimada[] v = new FocoQueimada[n];
        for (int i = 0; i < n; i++) {
            v[i] = new FocoQueimada(i, LocalDateTime.of(2023, 1, 1, 0, 0).plusHours(r.nextInt(5000)),
                    MUNICIPIOS[r.nextInt(MUNICIPIOS.length)], BIOMAS[r.nextInt(BIOMAS.length)], 0, 0);
        }
        return v;
    }

    @ParameterizedTest
    @EnumSource(Algoritmo.class)
    void ordenaPorTodosOsCriterios(Algoritmo algoritmo) {
        for (Criterio criterio : Criterio.values()) {
            for (boolean decrescente : new boolean[]{false, true}) {
                Comparator<FocoQueimada> comparador = criterio.comparador(decrescente);
                FocoQueimada[] v = gerar(500);
                FocoQueimada[] esperado = v.clone();
                Arrays.sort(esperado, comparador);

                Resultado resultado = algoritmo.criar().ordenar(v, comparador);

                assertThat(v).containsExactly(esperado);
                assertThat(resultado.comparacoes()).isPositive();
                assertThat(resultado.totalOperacoes())
                        .isEqualTo(resultado.comparacoes() + resultado.trocas() + resultado.movimentacoes());
            }
        }
    }

    @ParameterizedTest
    @EnumSource(Algoritmo.class)
    void aceitaVetorVazioEUnitario(Algoritmo algoritmo) {
        algoritmo.criar().ordenar(new FocoQueimada[0], Criterio.DATA.comparador(false));
        FocoQueimada[] um = gerar(1);
        algoritmo.criar().ordenar(um, Criterio.DATA.comparador(false));
        assertThat(um).hasSize(1);
    }

    @Test
    void ordenaNomesComAcentoCorretamente() {
        FocoQueimada[] v = gerar(50);
        Algoritmo.QUICK.criar().ordenar(v, Criterio.MUNICIPIO.comparador(false));
        assertThat(v[0].municipio()).isEqualTo("ÁGUAS DE LINDÓIA");
    }
}
