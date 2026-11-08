package br.unip.aps.queimadas.ordenacao;

import br.unip.aps.queimadas.model.Criterio;
import br.unip.aps.queimadas.model.FocoQueimada;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class AlgoritmosOrdenacaoTest {

    @ParameterizedTest
    @EnumSource(value = Algoritmo.class, names = {"BUBBLE", "SELECTION", "INSERTION", "MERGE", "QUICK"})
    void ordenaPorData(Algoritmo algoritmo) {
        Random r = new Random(1);
        FocoQueimada[] v = new FocoQueimada[500];
        for (int i = 0; i < v.length; i++) {
            v[i] = new FocoQueimada(i, LocalDateTime.of(2023, 1, 1, 0, 0).plusHours(r.nextInt(5000)),
                    "M" + r.nextInt(50), "B" + r.nextInt(5), 0, 0);
        }

        Resultado resultado = algoritmo.criar().ordenar(v, Criterio.DATA.comparador(false));

        assertThat(resultado.comparacoes()).isPositive();
        for (int i = 1; i < v.length; i++) {
            assertThat(v[i - 1].dataHora()).isBeforeOrEqualTo(v[i].dataHora());
        }
    }
}
