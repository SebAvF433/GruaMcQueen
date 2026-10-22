# Grúa McQueen (Java consola)

Sistema de gestión de asistencia vial desarrollado en Java, con POO básica y almacenamiento en memoria (no requiere base de datos).

## Requisitos
- JDK 17 o superior.

## Ejecutar desde la carpeta del proyecto

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java" ! -path "*/test/*")
java -cp out Main
```

En Windows PowerShell, puede compilar con:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$archivos = Get-ChildItem -Recurse src -Filter *.java | Where-Object { $_.FullName -notmatch '\\test\\' }
javac -encoding UTF-8 -d out $archivos.FullName
java -cp out Main
```

## Usuarios iniciales
- Administrador: usuario `admin`, clave `admin123`
- Cliente de prueba: usuario `cliente`, clave `cliente123`

## Funcionalidades
- Registro e inicio/cierre de sesión de clientes.
- Gestión de grúas: registrar, listar y cambiar estado.
- Gestión de vehículos de cada cliente.
- Crear, consultar y cancelar solicitudes.
- Asignar grúa compatible, iniciar y finalizar servicios.
- Generar factura al finalizar el servicio, consultar facturas y ver desglose de tarifa.
- Reportes de ingresos y de servicios por estado/grúa.

## Importante
Los datos se guardan en memoria y se reinician al cerrar el programa. Para asignar una solicitud, primero registra una grúa compatible desde el menú administrador. Las grúas pesadas atienden camiones; las de plataforma y arrastre atienden motos, automóviles y camionetas.
