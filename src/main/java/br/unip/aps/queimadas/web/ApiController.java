package br.unip.aps.queimadas.web;

import br.unip.aps.queimadas.model.Criterio;
import br.unip.aps.queimadas.model.FocoQueimada;
import br.unip.aps.queimadas.ordenacao.Algoritmo;
import br.unip.aps.queimadas.service.DadosService;
import br.unip.aps.queimadas.service.EstatisticasService;
import br.unip.aps.queimadas.service.Filtro;
import br.unip.aps.queimadas.service.OrdenacaoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DadosService dados;
    private final OrdenacaoService ordenacao;
    private final EstatisticasService estatisticas;

    public ApiController(DadosService dados, OrdenacaoService ordenacao, EstatisticasService estatisticas) {
        this.dados = dados;
        this.ordenacao = ordenacao;
        this.estatisticas = estatisticas;
    }

    @GetMapping("/dados")
    public Map<String, Object> status() {
        return Map.of(
                "total", dados.todos().size(),
                "arquivos", dados.arquivos(),
                "anos", estatisticas.anos(),
                "biomas", estatisticas.biomas(),
                "limiteQuadratico", OrdenacaoService.LIMITE_QUADRATICO);
    }

    @PostMapping("/dados/inpe")
    public List<DadosService.Arquivo> baixarDoInpe(@RequestParam String uf, @RequestParam List<Integer> anos)
            throws IOException, InterruptedException {
        return dados.baixarDoInpe(uf, anos);
    }

    @PostMapping("/dados/upload")
    public DadosService.Arquivo upload(@RequestParam("arquivo") MultipartFile arquivo) throws IOException {
        return dados.importar(arquivo.getOriginalFilename(), arquivo.getBytes());
    }

    @DeleteMapping("/dados")
    public void limpar() throws IOException {
        dados.limpar();
    }

    @GetMapping("/ordenar")
    public OrdenacaoService.Pagina ordenar(@RequestParam Criterio criterio,
                                           @RequestParam Algoritmo algoritmo,
                                           @RequestParam(defaultValue = "false") boolean decrescente,
                                           @RequestParam(required = false) Integer ano,
                                           @RequestParam(required = false) String bioma,
                                           @RequestParam(required = false) Integer amostra,
                                           @RequestParam(defaultValue = "0") int pagina,
                                           @RequestParam(defaultValue = "50") int tamanho) {
        return ordenacao.ordenarPagina(algoritmo, criterio, decrescente, new Filtro(ano, bioma, amostra),
                pagina, tamanho);
    }

    @GetMapping("/comparar")
    public List<OrdenacaoService.Comparacao> comparar(@RequestParam Criterio criterio,
                                                      @RequestParam(defaultValue = "false") boolean decrescente,
                                                      @RequestParam(required = false) Integer ano,
                                                      @RequestParam(required = false) String bioma,
                                                      @RequestParam(required = false) Integer amostra) {
        return ordenacao.compararTodos(criterio, decrescente, new Filtro(ano, bioma, amostra));
    }

    @GetMapping("/estatisticas")
    public EstatisticasService.Resumo estatisticas(@RequestParam(required = false) Integer ano,
                                                   @RequestParam(required = false) String bioma) {
        return estatisticas.resumo(new Filtro(ano, bioma, null));
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@RequestParam Criterio criterio,
                                           @RequestParam Algoritmo algoritmo,
                                           @RequestParam(defaultValue = "false") boolean decrescente,
                                           @RequestParam(required = false) Integer ano,
                                           @RequestParam(required = false) String bioma,
                                           @RequestParam(required = false) Integer amostra) {
        FocoQueimada[] focos = ordenacao.ordenar(algoritmo, criterio, decrescente,
                new Filtro(ano, bioma, amostra)).focos();
        StringBuilder csv = new StringBuilder("﻿id_bdq,data_pas,municipio,bioma,lat,lon\n");
        for (FocoQueimada f : focos) {
            csv.append(f.id()).append(',')
                    .append(FORMATO.format(f.dataHora())).append(',')
                    .append(f.municipio()).append(',')
                    .append(f.bioma()).append(',')
                    .append(f.latitude()).append(',')
                    .append(f.longitude()).append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"focos_ordenados.csv\"")
                .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }
}
