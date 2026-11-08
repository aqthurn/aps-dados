# APS — Algoritmos de Ordenação (Focos de Queimadas / INPE)

Java 17 + Spring Boot 3. Carrega um CSV de focos de queimadas do INPE e ordena por data, bioma ou município,
mostrando comparações, trocas e tempo.

## Executar

```bash
mvnw.cmd spring-boot:run
```

Abra <http://localhost:8080>, envie o `.csv` do INPE e escolha o critério e o algoritmo.

## Estrutura

```
model/      FocoQueimada, Criterio
ordenacao/  Ordenador, Algoritmo, Resultado e os algoritmos
service/    LeitorCsv, DadosService
web/        ApiController
```

## Pendente

- Shell Sort
- Heap Sort
- Download direto do INPE (`DadosService.baixarDoInpe`)
- Comparação entre todos os algoritmos (`DadosService.compararTodos`, `/api/comparar`)
