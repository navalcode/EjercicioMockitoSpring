# Sales service testing with Mockito

[English](README.md) · [Más proyectos](https://github.com/navalcode)

## Resumen

Ejemplo formativo de un servicio de ventas con **18 comprobaciones locales**: ocho casos originales, cuatro regresiones de propiedad/entrada y seis recorridos HTTP/JPA. La revisión corrige productos ausentes, compara identificadores por valor y elimina todas las líneas del producto, retirando ambos extremos de la relación.

La aplicación arranca como demo autónoma con H2. El JSON se enlaza con los builders de los DTO; el conversor está registrado en Spring y las líneas son entidades JPA. Las entradas inválidas o ausentes no guardan una venta parcial.

## Ejecutar

Requiere JDK 17. Desde la raíz, en una shell Unix:

```sh
sh ./mvnw verify
java -jar target/EjercicioMockitoSpring-0.0.1-SNAPSHOT.jar --server.port=0
```

El servidor escucha en loopback e imprime el puerto asignado y un cuerpo JSON `DEMO_REQUEST=`. Envía ese cuerpo mediante POST a `/ventas` en el puerto indicado. El alias original `POST /` acepta el mismo contrato. Una demo recién iniciada produce:

```json
{
  "clienteId": 1,
  "productos": [
    {"idProducto": 2, "cantidad": 2}
  ]
}
```

La respuesta es 201 y el total 5.0 corresponde al cuaderno ficticio. Se conservan los nombres españoles de propiedades y la fecha `dd/MM/yyyy` original.

Los errores tienen un campo JSON estable `code`.

| Caso | Resultado |
|---|---|
| Cliente/producto válidos y cantidad positiva |201 y venta persistida con subtotales calculados |
| Cliente o producto desconocido |404 · `not_found`, sin venta parcial |
| IDs de producto duplicados o cantidad no positiva |400 · `invalid_request`, sin venta |
| Añadir/retirar desde el servicio cuando falta venta/línea |`null`, sin escritura |

## Leer el comportamiento

Empieza por `VentaServicioTest`, después `VentaServicioRegressionTest` y `VentaHttpJourneyTest` en `src/test/`. Las comprobaciones antiguas de éxito podían pasar con colecciones vacías; ahora exigen cantidad de líneas, unidades, precio, propiedad y escrituras. Se conserva el contrato nullable de añadir/retirar. `@Transactional` pertenece a cada operación; la eliminación compara valores `Long` fuera de la caché de enteros pequeños.

El 29 de octubre de 2026 pasan 18 tests y Maven `verify` empaqueta el JAR con JDK 17/macOS. Un arranque separado del JAR en loopback confirma 201/404/400 y se cierra correctamente. La versión inicial tenía una prueba fallida y dos errores entre ocho casos; el recorrido HTTP nuevo tampoco podía arrancar porque faltaba el driver H2. [Resultados](docs/verification.json); esta revisión no añade CI alojada.

Se mantienen Maven 3.8.3/Spring Boot 2.6.2. H2 es la única dependencia runtime añadida. Sigue siendo un modelo formativo con precios `double`, datos en memoria y sin sistemas de stock, pagos o cuentas. [Decisiones de diseño y contrato](DESIGN.md).
