# MÓDULO 1 — Autenticación de Usuario

---

# HU-01 — Registrarse como cliente

Como cliente nuevo,
quiero crear mi cuenta con nombre, usuario y clave,
para poder solicitar servicios de grúa.

## Criterios de aceptación
- [ ] Con nombre, usuario y clave válidos, la cuenta se crea con rol CLIENTE.
- [ ] Un usuario que ya existe no se vuelve a registrar y se informa el motivo.
- [ ] Un campo vacío se rechaza y se vuelve a pedir el dato.
- [ ] Un registro rechazado no agrega ninguna cuenta a la lista de usuarios.

## Tareas técnicas
- Implementar la entidad Usuario y el enum Rol (CLIENTE, ADMIN) en modelo.
- Implementar guardar() y buscarPorUsuario() en UsuarioRepositorio.
- Implementar registrarCliente() en AuthServicio, lanzando ReglaNegocioException si el usuario ya existe.
- Incorporar la opción "Registrarse" en MenuPrincipal.
- Comprobar los casos normal, usuario duplicado y campo vacío.

---

# HU-02 — Iniciar sesión

Como usuario (cliente o administrador),
quiero ingresar con mi usuario y clave,
para acceder al menú que corresponde a mi rol.

## Criterios de aceptación
- [ ] Con las credenciales correctas de un administrador, se muestra el menú de administrador.
- [ ] Con las credenciales correctas de un cliente, se muestra el menú de cliente.
- [ ] Con una clave incorrecta se informa el error y se permite reintentar sin cerrar el programa.
- [ ] Con un usuario inexistente se informa "credenciales incorrectas" sin indicar cuál dato falló.
- [ ] Un usuario o una clave vacíos se rechazan y se vuelve a pedir el dato.

## Tareas técnicas
- Implementar iniciarSesion(usuario, clave) en AuthServicio, que devuelva el Usuario o lance ReglaNegocioException.
- Cargar un administrador y un cliente de prueba desde DatosIniciales.
- Redirigir al MenuAdmin o al MenuCliente según el Rol desde MenuPrincipal.
- Comprobar los casos credenciales correctas por rol, clave incorrecta, usuario inexistente y campos vacíos.

---

# HU-03 — Cerrar sesión

Como usuario,
quiero cerrar mi sesión,
para que otra persona pueda ingresar con su propia cuenta.

## Criterios de aceptación
- [ ] Al cerrar sesión se vuelve al menú principal.
- [ ] Después de cerrar sesión, las opciones del menú anterior no están disponibles hasta iniciar sesión de nuevo.
- [ ] Al ingresar con otra cuenta solo se ven los datos de esa cuenta.

## Tareas técnicas
- Guardar el usuario activo en AuthServicio y limpiarlo en cerrarSesion().
- Incorporar la opción "Cerrar sesión" en MenuCliente y MenuAdmin.
- Comprobar el recorrido cliente → cerrar sesión → administrador.

---

# MÓDULO 2 — Menú Interactivo

---

# HU-04 — Navegar por el menú de cliente

Como cliente,
quiero ver un menú numerado con mis opciones,
para moverme por el sistema sin conocer comandos.

## Criterios de aceptación
- [ ] El menú muestra las opciones: mis vehículos, mis solicitudes, mis facturas y cerrar sesión.
- [ ] Al elegir una opción se ejecuta su función y, al terminar, se vuelve al menú.
- [ ] El encabezado muestra el nombre del cliente que inició sesión.
- [ ] "Cerrar sesión" termina el menú sin cerrar el programa.

## Tareas técnicas
- Implementar MenuCliente con un ciclo do-while y un switch sobre la opción elegida.
- Conectar cada opción con los servicios correspondientes (VehiculoServicio, SolicitudService, FacturaServicio).
- Leer toda entrada a través de Consola.
- Comprobar el recorrido de todas las opciones del menú.

---

# HU-05 — Navegar por el menú de administrador

Como administrador,
quiero ver un menú numerado con las opciones de gestión,
para operar el sistema desde un solo lugar.

## Criterios de aceptación
- [ ] El menú muestra las opciones: grúas, solicitudes, facturas, reportes y cerrar sesión.
- [ ] Cada opción abre su submenú y, al terminar, se vuelve al menú de administrador.
- [ ] Un cliente no puede acceder a este menú.
- [ ] "Cerrar sesión" termina el menú sin cerrar el programa.

## Tareas técnicas
- Implementar MenuAdmin con submenús por módulo (grúas, solicitudes, facturas, reportes).
- Verificar el rol ADMIN antes de mostrar el menú.
- Conectar cada opción con los servicios correspondientes.
- Comprobar el recorrido de todas las opciones y el intento de acceso con un cliente.

---

# HU-06 — Recibir avisos ante entradas incorrectas

Como usuario,
quiero recibir un mensaje claro y volver a intentar cuando escribo un dato inválido,
para que el programa no se cierre inesperadamente.

## Criterios de aceptación
- [ ] Si se escribe texto donde se espera un número (por ejemplo "abc"), se informa y se repite la pregunta.
- [ ] Si se elige una opción fuera de rango (por ejemplo 99), se informa y se repite el menú.
- [ ] Si se deja un campo vacío, se informa y se repite la pregunta.
- [ ] Ningún dato incorrecto finaliza el programa.

## Tareas técnicas
- Implementar en Consola los métodos leerTexto(), leerEntero(min, max) y leerDecimal(), leyendo siempre con nextLine() y try/catch.
- Mantener un único Scanner en toda la aplicación (dentro de Consola).
- Capturar ReglaNegocioException y DatoInvalidoException en los menús y mostrar su mensaje.
- Comprobar los casos "abc", "99", texto vacío y "-5".

---

# MÓDULO 3 — Registro de Vehículos

---

# HU-07 — Registrar un vehículo

Como cliente,
quiero registrar mi vehículo con su placa, tipo y marca,
para poder solicitar una grúa para él.

## Criterios de aceptación
- [ ] Con una placa válida (por ejemplo ABC123), un tipo y una marca, el vehículo queda registrado y asociado al cliente.
- [ ] Una placa ya registrada se rechaza y se informa.
- [ ] Una placa con formato inválido (por ejemplo 12AB) se rechaza y se vuelve a pedir.
- [ ] Un registro rechazado no modifica la lista de vehículos.

## Tareas técnicas
- Implementar la entidad Vehiculo y el enum TipoVehiculo (MOTO, AUTOMOVIL, CAMIONETA, CAMION).
- Implementar Validador.placaValida() para el formato de tres letras, dos dígitos y un dígito o letra.
- Implementar guardar() y buscarPorPlaca() en VehiculoRepositorio.
- Implementar registrar() en VehiculoServicio, asociando el vehículo al cliente activo.
- Incorporar la opción "Registrar vehículo" en MenuCliente.
- Comprobar los casos normal, placa duplicada y placa con formato inválido.

---

# HU-08 — Consultar mis vehículos

Como cliente,
quiero ver la lista de mis vehículos registrados,
para elegir cuál necesita el servicio.

## Criterios de aceptación
- [ ] Un cliente con vehículos ve únicamente los suyos, con placa, tipo y marca.
- [ ] Un cliente sin vehículos ve un mensaje que lo informa.
- [ ] Los vehículos de otros clientes no aparecen en la lista.

## Tareas técnicas
- Implementar listarPorCliente(Usuario) en VehiculoRepositorio y en VehiculoServicio.
- Mostrar el listado en MenuCliente con formato de tabla (printf).
- Comprobar los casos con vehículos, sin vehículos y con dos clientes distintos.

---

# HU-09 — Buscar vehículos

Como administrador,
quiero buscar vehículos por placa y filtrarlos por tipo o por cliente,
para encontrar rápido la información de un servicio.

## Criterios de aceptación
- [ ] Al buscar una placa existente se muestran los datos del vehículo y de su propietario.
- [ ] Al buscar una placa inexistente se informa "no encontrado" sin error.
- [ ] Al filtrar por tipo se muestran solo los vehículos de ese tipo.
- [ ] Al filtrar por cliente se muestran solo los vehículos de ese cliente.

## Tareas técnicas
- Implementar buscarPorPlaca() devolviendo Optional<Vehiculo>.
- Implementar filtrarPorTipo() y filtrarPorCliente() en VehiculoServicio.
- Incorporar las opciones de búsqueda y filtro en MenuAdmin.
- Comprobar los casos placa existente, placa inexistente y filtros con y sin resultados.

---

# MÓDULO 4 — Inventario (grúas)

---

# HU-10 — Registrar una grúa

Como administrador,
quiero registrar una grúa con su placa y tipo,
para tenerla disponible en el inventario.

## Criterios de aceptación
- [ ] Con una placa válida y un tipo, la grúa queda registrada en estado DISPONIBLE.
- [ ] Una placa ya registrada se rechaza y se informa.
- [ ] Una placa con formato inválido se rechaza y se vuelve a pedir.
- [ ] El tipo se elige entre PLATAFORMA, ARRASTRE y PESADA; otra opción se rechaza.

## Tareas técnicas
- Implementar la entidad Grua y los enums TipoGrua y EstadoGrua.
- Implementar guardar() y buscarPorPlaca() en GruaRepositorio.
- Implementar registrar() en GruaServicio, lanzando ReglaNegocioException si la placa ya existe.
- Incorporar la opción "Registrar grúa" en MenuAdmin.
- Comprobar los casos normal, placa duplicada, placa inválida y tipo inválido.

---

# HU-11 — Consultar el inventario de grúas

Como administrador,
quiero listar las grúas y filtrarlas por estado o tipo,
para saber cuáles puedo asignar.

## Criterios de aceptación
- [ ] El listado muestra placa, tipo y estado de cada grúa.
- [ ] El filtro por estado muestra solo las grúas de ese estado.
- [ ] El filtro por tipo muestra solo las grúas de ese tipo.
- [ ] Un inventario vacío o un filtro sin resultados muestra un mensaje, sin error.

## Tareas técnicas
- Implementar listar(), filtrarPorEstado() y filtrarPorTipo() en GruaServicio.
- Mostrar el inventario en MenuAdmin con formato de tabla (printf).
- Comprobar los casos inventario vacío, filtro con resultados y filtro sin resultados.

---

# HU-12 — Cambiar el estado de una grúa

Como administrador,
quiero pasar una grúa a mantenimiento o devolverla a disponible,
para evitar que se asigne una grúa que no puede operar.

## Criterios de aceptación
- [ ] Una grúa DISPONIBLE puede pasar a MANTENIMIENTO.
- [ ] Una grúa en MANTENIMIENTO puede volver a DISPONIBLE.
- [ ] Una grúa EN_SERVICIO no puede pasar a mantenimiento mientras tenga un servicio activo.
- [ ] Una grúa en mantenimiento no aparece entre las grúas asignables.

## Tareas técnicas
- Implementar cambiarEstado(placa, estado) en GruaServicio con la validación de estados permitidos.
- Incorporar la opción "Cambiar estado de grúa" en MenuAdmin.
- Comprobar los casos disponible → mantenimiento, mantenimiento → disponible y grúa en servicio.

---

# MÓDULO 5 — Solicitudes de servicio

---

# HU-13 — Solicitar un servicio de grúa

Como cliente,
quiero solicitar una grúa indicando vehículo, origen, destino y kilómetros,
para que atiendan mi servicio.

## Criterios de aceptación
- [ ] Con un vehículo propio, origen, destino y kilómetros válidos, la solicitud se crea en estado SOLICITADA.
- [ ] Un vehículo que ya tiene una solicitud activa (SOLICITADA, ASIGNADA o EN_CURSO) no puede solicitar otra.
- [ ] Un vehículo ajeno o inexistente se rechaza.
- [ ] Kilómetros iguales o menores a cero, mayores a 500 o escritos como texto se rechazan y se vuelve a pedir el dato.
- [ ] Una solicitud rechazada no se guarda.

## Tareas técnicas
- Implementar la entidad SolicitudServicio y el enum EstadoSolicitud (SOLICITADA, ASIGNADA, EN_CURSO, FINALIZADA, CANCELADA).
- Implementar guardar() y listar() en SolicitudRepositorio.
- Implementar crear() en SolicitudService, con identificador autogenerado y validación de solicitud activa.
- Implementar Validador.kmValido().
- Incorporar la opción "Solicitar grúa" en MenuCliente.
- Comprobar los casos normal, solicitud activa duplicada, vehículo ajeno y kilómetros inválidos.

---

# HU-14 — Consultar mis solicitudes

Como cliente,
quiero ver mis solicitudes y su estado actual,
para saber en qué va mi servicio.

## Criterios de aceptación
- [ ] El listado muestra identificador, vehículo, origen, destino y estado de cada solicitud.
- [ ] Un cliente ve únicamente sus propias solicitudes.
- [ ] Un cliente sin solicitudes ve un mensaje que lo informa.

## Tareas técnicas
- Implementar listarPorCliente(Usuario) en SolicitudRepositorio y en SolicitudService.
- Mostrar el listado en MenuCliente con formato de tabla (printf).
- Comprobar los casos con solicitudes, sin solicitudes y con dos clientes distintos.

---

# HU-15 — Cancelar una solicitud

Como cliente,
quiero cancelar una solicitud que aún no está en curso,
para no pagar un servicio que ya no necesito.

## Criterios de aceptación
- [ ] Una solicitud SOLICITADA pasa a CANCELADA.
- [ ] Una solicitud ASIGNADA pasa a CANCELADA y su grúa vuelve a DISPONIBLE.
- [ ] Una solicitud EN_CURSO o FINALIZADA no se puede cancelar y se informa el motivo.
- [ ] Una solicitud ajena o inexistente se rechaza sin modificar nada.

## Tareas técnicas
- Implementar cancelar(id, cliente) en SolicitudService con validación del estado actual.
- Liberar la grúa asignada llamando a GruaServicio al cancelar una solicitud ASIGNADA.
- Incorporar la opción "Cancelar solicitud" en MenuCliente.
- Comprobar los casos cancelar SOLICITADA, cancelar ASIGNADA, cancelar FINALIZADA y solicitud ajena.

---

# HU-16 — Asignar una grúa a una solicitud

Como administrador,
quiero asignar a una solicitud una grúa disponible y compatible,
para que el servicio pueda atenderse.

## Criterios de aceptación
- [ ] Con una solicitud SOLICITADA y una grúa disponible compatible, la solicitud pasa a ASIGNADA y la grúa a EN_SERVICIO.
- [ ] Para un camión solo se ofrecen grúas PESADA; para los demás tipos de vehículo, PLATAFORMA o ARRASTRE.
- [ ] Si no hay una grúa disponible compatible, se informa y la solicitud sigue en SOLICITADA.
- [ ] Una grúa en mantenimiento o en servicio no se asigna.

## Tareas técnicas
- Implementar buscarDisponibleCompatible(TipoVehiculo) en GruaServicio.
- Implementar asignar(idSolicitud) en SolicitudService, actualizando el estado de la solicitud y el de la grúa.
- Incorporar la opción "Asignar grúa" en MenuAdmin.
- Comprobar los casos asignación normal, camión con grúa no pesada, sin grúas disponibles y grúa en mantenimiento.

---

# HU-17 — Iniciar y finalizar un servicio

Como administrador,
quiero marcar un servicio como en curso y luego como finalizado,
para llevar el control de la operación.

## Criterios de aceptación
- [ ] Una solicitud ASIGNADA pasa a EN_CURSO al iniciarla.
- [ ] Una solicitud EN_CURSO pasa a FINALIZADA al finalizarla y su grúa vuelve a DISPONIBLE.
- [ ] Una solicitud que no está ASIGNADA no se puede iniciar.
- [ ] Una solicitud que no está EN_CURSO no se puede finalizar.
- [ ] Una operación rechazada no cambia el estado de la solicitud ni el de la grúa.

## Tareas técnicas
- Implementar iniciar(id) y finalizar(id) en SolicitudService con validación de la secuencia de estados.
- Llamar a la generación de la factura desde finalizar() (ver HU-19).
- Incorporar las opciones "Iniciar servicio" y "Finalizar servicio" en MenuAdmin.
- Comprobar los casos secuencia completa, finalizar sin asignar e iniciar una solicitud ya finalizada.

---

# HU-18 — Consultar y filtrar solicitudes

Como administrador,
quiero listar las solicitudes y filtrarlas por estado o por cliente,
para priorizar las pendientes.

## Criterios de aceptación
- [ ] El listado muestra identificador, cliente, vehículo y estado de cada solicitud.
- [ ] El filtro por estado muestra solo las solicitudes de ese estado.
- [ ] El filtro por cliente muestra solo las solicitudes de ese cliente.
- [ ] Un filtro sin resultados muestra un mensaje, sin error.

## Tareas técnicas
- Implementar listar(), filtrarPorEstado() y filtrarPorCliente() en SolicitudService.
- Incorporar las opciones de consulta y filtro en MenuAdmin.
- Comprobar los casos listado completo, filtro con resultados y filtro sin resultados.

---

# MÓDULO 6 — Factura

---

# HU-19 — Generar la factura de un servicio

Como administrador,
quiero que al finalizar un servicio se calcule la tarifa y se genere la factura,
para cobrar de forma correcta y automática.

## Criterios de aceptación
- [ ] Al finalizar una solicitud se crea una factura con subtotal, recargo, IVA y total.
- [ ] Un automóvil, 10 km, en horario diurno, genera un total de $119.000.
- [ ] Un camión, 10 km, en horario diurno, genera un total de $154.700.
- [ ] Un automóvil, 10 km, en horario nocturno, genera un total de $142.800.
- [ ] No se genera factura para una solicitud que no está FINALIZADA ni dos facturas para la misma solicitud.

## Tareas técnicas
- Definir las constantes de la tarifa (base, valor por kilómetro, recargos e IVA) en la clase Tarifas.
- Implementar calcular(km, tipoVehiculo, nocturno) en TarifaServicio.
- Implementar la entidad Factura y FacturaRepositorio con guardar() y listar().
- Implementar generar(solicitud) en FacturaServicio y llamarlo desde finalizar() en SolicitudService.
- Comprobar los casos de tarifa diurna, nocturna y de camión, y los intentos de facturar una solicitud no finalizada o ya facturada.

---

# HU-20 — Consultar mis facturas

Como cliente,
quiero ver mis facturas con su detalle,
para conocer cuánto pagué por cada servicio.

## Criterios de aceptación
- [ ] El listado muestra identificador, fecha y total de cada factura del cliente.
- [ ] Al elegir una factura se muestra su detalle: subtotal, recargo, IVA y total.
- [ ] Un cliente no ve las facturas de otros clientes.
- [ ] Un cliente sin facturas ve un mensaje que lo informa.

## Tareas técnicas
- Implementar listarPorCliente(Usuario) en FacturaServicio.
- Mostrar el listado y el detalle en MenuCliente con formato de tabla (printf).
- Comprobar los casos con facturas, sin facturas y con dos clientes distintos.

---

# HU-21 — Consultar facturas

Como administrador,
quiero listar todas las facturas y ver el detalle de cada una,
para tener control de lo facturado.

## Criterios de aceptación
- [ ] El listado muestra identificador, cliente, fecha y total de cada factura.
- [ ] Al buscar una factura por su identificador se muestra su detalle completo.
- [ ] Un identificador inexistente se informa sin error.
- [ ] Sin facturas registradas, se muestra un mensaje que lo informa.

## Tareas técnicas
- Implementar listar() y buscarPorId() en FacturaServicio.
- Incorporar las opciones de consulta en MenuAdmin.
- Comprobar los casos listado con facturas, listado vacío e identificador inexistente.

---

# MÓDULO 7 — Reportes

---

# HU-22 — Reportes de ingresos

Como administrador,
quiero ver los ingresos totales y la facturación agrupada por cliente,
para conocer cómo va el negocio.

## Criterios de aceptación
- [ ] El ingreso total es la suma de los totales de todas las facturas (con facturas de $119.000 y $154.700, el total es $273.700).
- [ ] La facturación por cliente muestra la suma de cada cliente.
- [ ] Sin facturas, el reporte muestra $0 o un mensaje, sin error.

## Tareas técnicas
- Implementar ingresosTotales() y facturacionPorCliente() en ReporteServicio, agrupando con un Map o con Collectors.groupingBy.
- Incorporar la opción "Reportes de ingresos" en MenuAdmin.
- Comprobar los casos con datos conocidos, con un solo cliente y sin facturas.

---

# HU-23 — Reportes de servicios

Como administrador,
quiero ver la cantidad de servicios por estado y por grúa,
para evaluar el uso de la flota.

## Criterios de aceptación
- [ ] El reporte por estado muestra cuántas solicitudes hay en cada estado.
- [ ] El reporte por grúa muestra cuántos servicios atendió cada grúa.
- [ ] Sin datos, los reportes muestran un mensaje, sin error.
- [ ] Los totales del reporte coinciden con el listado de solicitudes.

## Tareas técnicas
- Implementar serviciosPorEstado() y serviciosPorGrua() en ReporteServicio.
- Incorporar la opción "Reportes de servicios" en MenuAdmin con formato de tabla (printf).
- Comprobar los casos con datos conocidos y sin datos.
