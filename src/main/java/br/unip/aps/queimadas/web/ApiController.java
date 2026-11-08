package br.unip.aps.queimadas.web;

import br.unip.aps.queimadas.model.Criterio;
import br.unip.aps.queimadas.ordenacao.Algoritmo;
import br.unip.aps.queimadas.ordenacao.Resultado;
import br.unip.aps.queimadas.service.DadosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final DadosService dados;

    public ApiController(DadosService dados) {
        this.dados = dados;
    }

    @GetMapping("/dados")
    public Map<String, Integer> status() {
        return Map.of("total", dados.total());
    }

    @PostMapping("/dados/upload")
    public Map<String, Integer> upload(@RequestParam("arquivo") MultipartFile arquivo) throws IOException {
        int importados = dados.importar(new String(arquivo.getBytes(), StandardCharsets.UTF_8));
        return Map.of("importados", importados, "total", dados.total());
    }

    @DeleteMapping("/dados")
    public void limpar() {
        dados.limpar();
    }

    @GetMapping("/ordenar")
    public DadosService.Ordenacao ordenar(@RequestParam Criterio criterio,
                                          @RequestParam Algoritmo algoritmo,
                                          @RequestParam(defaultValue = "false") boolean decrescente,
                                          @RequestParam(defaultValue = "100") int limite) {
        return dados.ordenar(algoritmo, criterio, decrescente, limite);
    }

    @GetMapping("/comparar")
    public List<Resultado> comparar(@RequestParam Criterio criterio) {
        return dados.compararTodos(criterio);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> erro(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("erro", String.valueOf(e.getMessage())));
    }
}
