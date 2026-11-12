# APS — Algoritmos de Ordenação (Focos de Queimadas / INPE)

Java 17 + Spring Boot 3. Baixa os focos de queimadas do INPE (2023 e 2024) de um estado e ordena por
data, bioma ou município com 7 algoritmos implementados do zero, mostrando comparações, trocas,
movimentações e tempo a cada exibição.

## Executar

Requisito: JDK 17 ou superior.

```bash
mvnw.cmd spring-boot:run
```

Abra <http://localhost:8080>. Na aba **Dados**, escolha o estado e os anos e clique em *Baixar e carregar*
(ou envie o `.csv`/`.zip` manualmente). Os arquivos ficam na pasta `dados/` e são recarregados ao reiniciar.

Testes: `mvnw.cmd test`

## Abas

| Aba | O que faz |
|---|---|
| Dados | Baixa do INPE ou importa arquivo; mostra registros e linhas inválidas |
| Ordenação | Critério, algoritmo, ordem, filtros por ano/bioma, amostra, paginação, exportação CSV e métricas |
| Comparar algoritmos | Executa os 7 algoritmos nos mesmos dados, compara operações e tempo e confere se o resultado é igual |
| Painel | Total de focos, focos por ano, por mês, por bioma e os 10 municípios com mais focos |

## Algoritmos

| Algoritmo | Complexidade média | Estável |
|---|---|---|
| Bubble Sort | O(n²) | Sim |
| Selection Sort | O(n²) | Não |
| Insertion Sort | O(n²) | Sim |
| Shell Sort | ~O(n^1,5) | Não |
| Merge Sort | O(n log n) | Sim |
| Quick Sort | O(n log n) | Não |
| Heap Sort | O(n log n) | Não |

- **Comparações**: chamadas ao comparador.
- **Trocas**: troca de duas posições do vetor.
- **Movimentações**: escrita de um elemento (deslocamentos do Insertion/Shell e intercalação do Merge).

Os algoritmos O(n²) ficam limitados a 20.000 registros; acima disso use a opção de amostra.

Cada critério tem desempate (ex.: bioma → município → data → id), então todos os algoritmos chegam ao
mesmo resultado. Nomes são comparados sem acento (`ÁGUAS` vem antes de `AVARÉ`).

## Estrutura

```
model/      FocoQueimada, Criterio
ordenacao/  Ordenador, Algoritmo, Resultado e os algoritmos
service/    LeitorCsv, Filtro, DadosService, OrdenacaoService, EstatisticasService
web/        ApiController, TratadorErros
```

## API

| Método | Endpoint |
|---|---|
| GET | `/api/dados` |
| POST | `/api/dados/inpe?uf=SP&anos=2023&anos=2024` |
| POST | `/api/dados/upload` (multipart `arquivo`) |
| DELETE | `/api/dados` |
| GET | `/api/ordenar?criterio=DATA&algoritmo=QUICK&decrescente=false&ano=&bioma=&amostra=&pagina=0&tamanho=50` |
| GET | `/api/comparar?criterio=BIOMA&amostra=5000` |
| GET | `/api/estatisticas?ano=&bioma=` |
| GET | `/api/exportar?criterio=MUNICIPIO&algoritmo=MERGE` |
