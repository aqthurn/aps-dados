package br.unip.aps.queimadas.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LeitorCsvTest {

    @Test
    void leCsvContandoLinhasInvalidas() {
        String csv = """
                id_bdq,foco_id,lat,lon,data_pas,pais,estado,municipio,bioma
                1,a,-20.9,-49.1,2023-01-02 17:09:00,Brasil,SÃO PAULO,UCHOA,Mata Atlântica
                linha quebrada
                2,b,-21.9,-47.1,2024-01-02 17:09:00,Brasil,SÃO PAULO,CASA BRANCA,Cerrado
                3,c,-21.9,-47.1,data errada,Brasil,SÃO PAULO,CASA BRANCA,Cerrado
                """;

        LeitorCsv.Leitura leitura = LeitorCsv.lerCsv(csv);

        assertThat(leitura.focos()).hasSize(2);
        assertThat(leitura.invalidas()).isEqualTo(2);
        assertThat(leitura.focos().get(0).municipio()).isEqualTo("UCHOA");
        assertThat(leitura.focos().get(0).chaveBioma()).isEqualTo("MATA ATLANTICA");
    }

    @Test
    void recusaCsvSemColunasObrigatorias() {
        assertThatThrownBy(() -> LeitorCsv.lerCsv("a,b,c\n1,2,3"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
