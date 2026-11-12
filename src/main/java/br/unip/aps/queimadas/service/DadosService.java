package br.unip.aps.queimadas.service;

import br.unip.aps.queimadas.model.FocoQueimada;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class DadosService {

    private static final String URL_INPE =
            "https://dataserver-coids.inpe.br/queimadas/queimadas/focos/csv/anual/EstadosBr_sat_ref/";
    private static final Path PASTA = Path.of("dados");
    private static final List<String> UFS = List.of(
            "AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO", "MA", "MG", "MS", "MT", "PA",
            "PB", "PE", "PI", "PR", "RJ", "RN", "RO", "RR", "RS", "SC", "SE", "SP", "TO");

    public record Arquivo(String nome, int registros, int invalidas) {}

    private final Map<String, List<FocoQueimada>> focosPorArquivo = new LinkedHashMap<>();
    private final Map<String, Arquivo> arquivos = new LinkedHashMap<>();
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @PostConstruct
    void carregarPastaLocal() {
        if (!Files.isDirectory(PASTA)) {
            return;
        }
        try (Stream<Path> lista = Files.list(PASTA)) {
            for (Path p : lista.sorted().toList()) {
                String nome = p.getFileName().toString();
                if (ehCsvOuZip(nome)) {
                    carregar(nome, Files.readAllBytes(p));
                }
            }
        } catch (IOException | RuntimeException e) {
            System.err.println("Não foi possível carregar a pasta dados: " + e.getMessage());
        }
    }

    public List<Arquivo> baixarDoInpe(String uf, List<Integer> anos) throws IOException, InterruptedException {
        String sigla = uf.trim().toUpperCase(Locale.ROOT);
        if (!UFS.contains(sigla)) {
            throw new IllegalArgumentException("UF inválida: " + uf);
        }
        List<Arquivo> baixados = new ArrayList<>();
        for (int ano : anos) {
            String nome = "focos_br_" + sigla.toLowerCase(Locale.ROOT) + "_ref_" + ano + ".zip";
            Path local = PASTA.resolve(nome);
            if (!Files.exists(local)) {
                HttpRequest requisicao = HttpRequest.newBuilder(URI.create(URL_INPE + sigla + "/" + nome))
                        .timeout(Duration.ofMinutes(3))
                        .build();
                HttpResponse<byte[]> resposta = http.send(requisicao, HttpResponse.BodyHandlers.ofByteArray());
                if (resposta.statusCode() != 200) {
                    throw new IllegalArgumentException("Arquivo " + nome + " não encontrado no INPE (HTTP "
                            + resposta.statusCode() + ").");
                }
                Files.createDirectories(PASTA);
                Files.write(local, resposta.body());
            }
            baixados.add(carregar(nome, Files.readAllBytes(local)));
        }
        return baixados;
    }

    public Arquivo importar(String nomeOriginal, byte[] conteudo) throws IOException {
        if (nomeOriginal == null || !ehCsvOuZip(nomeOriginal)) {
            throw new IllegalArgumentException("Envie um arquivo .csv ou .zip.");
        }
        String nome = Path.of(nomeOriginal).getFileName().toString();
        Arquivo arquivo = carregar(nome, conteudo);
        Files.createDirectories(PASTA);
        Files.write(PASTA.resolve(nome), conteudo);
        return arquivo;
    }

    private synchronized Arquivo carregar(String nome, byte[] conteudo) throws IOException {
        LeitorCsv.Leitura leitura = LeitorCsv.ler(nome, conteudo);
        if (leitura.focos().isEmpty()) {
            throw new IllegalArgumentException("Nenhum registro válido em " + nome + ".");
        }
        Arquivo arquivo = new Arquivo(nome, leitura.focos().size(), leitura.invalidas());
        focosPorArquivo.put(nome, leitura.focos());
        arquivos.put(nome, arquivo);
        return arquivo;
    }

    public synchronized void limpar() throws IOException {
        for (String nome : arquivos.keySet()) {
            Files.deleteIfExists(PASTA.resolve(nome));
        }
        focosPorArquivo.clear();
        arquivos.clear();
    }

    public synchronized List<FocoQueimada> todos() {
        List<FocoQueimada> todos = new ArrayList<>();
        for (List<FocoQueimada> lista : focosPorArquivo.values()) {
            todos.addAll(lista);
        }
        return todos;
    }

    public synchronized List<Arquivo> arquivos() {
        return List.copyOf(arquivos.values());
    }

    private static boolean ehCsvOuZip(String nome) {
        String minusculo = nome.toLowerCase(Locale.ROOT);
        return minusculo.endsWith(".csv") || minusculo.endsWith(".zip");
    }
}
