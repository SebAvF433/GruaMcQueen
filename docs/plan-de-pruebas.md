# Plan de pruebas — Responsable B (Módulo 3 Vehículos y Módulo 6 Factura)

Las pruebas están en `src/test/VehiculoTest.java` y `src/test/FacturaTest.java`.
Cada una imprime `OK` o `FALLO` y se ejecuta con `main`.

## Módulo 3 — Registro de Vehículos

| HU | Caso | Entrada | Resultado esperado |
|----|------|---------|--------------------|
| HU-07 | Registro normal | placa `abc123`, AUTOMOVIL, Mazda | Queda registrado en mayúsculas (`ABC123`) y asociado al cliente |
| HU-07 | Placa duplicada | `ABC123` por segunda vez | Se rechaza (ReglaNegocioException); la lista no cambia |
| HU-07 | Formato inválido | `12AB` | Se rechaza (DatoInvalidoException); la lista no cambia |
| HU-07 | Campos vacíos | placa vacía, marca con espacios, tipo nulo | Se rechaza cada caso |
| HU-07 | Última posición letra | `DEF45G` | Se acepta |
| HU-08 | Cliente con vehículos | Luis con 2 y Eva con 1 | Cada uno ve solo los suyos |
| HU-08 | Cliente sin vehículos | cliente nuevo | Lista vacía (el menú muestra mensaje) |
| HU-08 | Aislamiento | lista de Eva | Ningún vehículo de otro cliente |
| HU-09 | Placa existente | `aaa111` | Devuelve el vehículo y su propietario |
| HU-09 | Placa inexistente | `ZZZ999` | Devuelve vacío, sin error |
| HU-09 | Filtro por tipo | MOTO / CAMION | Solo ese tipo; vacío si no hay |
| HU-09 | Filtro por cliente | Luis | Solo los vehículos de Luis |

## Módulo 6 — Factura

Tarifa: subtotal = base ($50.000) + km × $5.000; recargo = subtotal × (30 % camión + 20 % nocturno);
IVA = 19 % de (subtotal + recargo); total = subtotal + recargo + IVA. Nocturno: 19:00 a 05:59.

| HU | Caso | Entrada | Resultado esperado |
|----|------|---------|--------------------|
| HU-19 | Automóvil diurno | 10 km | Total $119.000 (subtotal 100.000, IVA 19.000) |
| HU-19 | Camión diurno | 10 km | Total $154.700 (recargo 30.000) |
| HU-19 | Automóvil nocturno | 10 km | Total $142.800 (recargo 20.000) |
| HU-19 | Km inválidos | 0 y -5 | Se rechaza (DatoInvalidoException) |
| HU-19 | Factura al finalizar | solicitud FINALIZADA | Se crea `FAC-001` con los cuatro valores |
| HU-19 | Horario nocturno real | solicitud a las 22:00 y a las 03:00 | Se factura como nocturna |
| HU-19 | No finalizada | solicitud SOLICITADA | Se rechaza y no se guarda factura |
| HU-19 | Doble facturación | misma solicitud dos veces | Se rechaza; queda una sola factura |
| HU-20 | Facturas por cliente | Ana y Luis | Cada uno ve solo las suyas |
| HU-20 | Sin facturas | cliente nuevo | Lista vacía (el menú muestra mensaje) |
| HU-21 | Listado completo | 2 facturas | Devuelve las 2 |
| HU-21 | Búsqueda por id | `FAC-002` | Devuelve el detalle completo |
| HU-21 | Id inexistente | `FAC-999` | Devuelve vacío, sin error |
| HU-21 | Sin facturas | repositorio vacío | Lista vacía |

## Cómo ejecutar

```
javac -encoding UTF-8 -d out src/auth/Usuario.java src/comun/*.java src/vehiculo/*.java src/solicitud/EstadoSolicitudes.java src/solicitud/Solicitud.java src/factura/*.java src/test/*.java
java -cp out test.VehiculoTest
java -cp out test.FacturaTest
```
