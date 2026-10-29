# Sales service testing with Mockito

[Español](README.es.md) · [More work](https://github.com/navalcode)

## Overview

A coursework sales-service example with **18 local checks**: the original eight service cases, four ownership/input regressions and six HTTP/JPA journeys. The review fixes missing-product handling, compares boxed identifiers by value and removes every matching sale line while detaching both sides.

The application now starts as a self-contained H2 demo. The JSON request is bound through the DTO builders; the converter is registered with Spring and sale lines are JPA entities. Invalid or missing inputs do not persist a partial sale.

## Run

Requires JDK 17. From this repository root on a Unix shell:

```sh
sh ./mvnw verify
java -jar target/EjercicioMockitoSpring-0.0.1-SNAPSHOT.jar --server.port=0
```

The server binds to loopback and prints its assigned port plus a `DEMO_REQUEST=` JSON body. Post that body to `/ventas` at the printed port. The original `POST /` alias accepts the same payload. A fresh demo produces this request:

```json
{
  "clienteId": 1,
  "productos": [
    {"idProducto": 2, "cantidad": 2}
  ]
}
```

The response is 201 with a 5.0 total for the fictional notebook. Spanish property names and the original `dd/MM/yyyy` date representation are retained.

Error responses carry a stable JSON `code` field.

| Case | Result |
|---|---|
| Valid customer/product and positive quantity |201 and a persisted sale with calculated line totals |
| Unknown customer or product |404 · `not_found`, no partial sale |
| Duplicate product IDs or non-positive quantity |400 · `invalid_request`, no sale |
| Service-only add/remove with an absent sale/product line |`null`, no persistence write |

## Read the behavior

Start with `VentaServicioTest`, then `VentaServicioRegressionTest` and `VentaHttpJourneyTest` under `src/test/`. The old success assertions could pass with empty collections; they now assert line count, quantity, price, ownership and writes explicitly. The service keeps its existing nullable add/remove contract. `@Transactional` owns each operation; removal compares `Long` values outside the small-integer cache.

On 29 October 2026, JDK 17/macOS passed all 18 tests and Maven `verify` packaged the executable JAR. A separate loopback run of that JAR confirmed 201/404/400 responses, then shut down cleanly. Initial evidence was eight tests with one failure/two errors; the new HTTP journey initially could not start because the H2 driver was absent. Results are [recorded here](docs/verification.json); hosted CI was not added in this review.

The original Maven 3.8.3/Spring Boot 2.6.2 pins remain. H2 is the only added runtime dependency. This remains a coursework model with `double` prices, memory-only data, and no stock, payment or account system. [Design and contract decisions](DESIGN.md).

## Resumen

La versión española completa explica los mismos contratos, comandos y límites: [README.es.md](README.es.md).
