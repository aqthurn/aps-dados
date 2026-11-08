package br.unip.aps.queimadas.service;

import br.unip.aps.queimadas.model.FocoQueimada;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LeitorCsvTest {

    @Test
    void leCsvIgnorandoLinhasInvalidas() {
        String csv = """
                id_bdq,foco_id,lat,lon,data_pas,pais,estado,municipio,bioma
                1,a,-20.9,-49.1,2023-01-02 17:09:00,Brasil,SÃO PAULO,UCHOA,Mata Atlântica
                linha quebrada
                2,b,-21.9,-47.1,2024-01-02 17:09:00,Brasil,SÃO PAULO,CASA BRANCA,Cerrado
                """;

        List<FocoQueimada> focos = LeitorCsv.ler(csv);

        assertThat(focos).hasSize(2);
        assertThat(focos.get(0).municipio()).isEqualTo("UCHOA");
    }
}
