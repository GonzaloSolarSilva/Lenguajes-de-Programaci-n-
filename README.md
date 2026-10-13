# Sabor Criollo — Sistema de Gestión de Pedidos

Proyecto del curso **Lenguajes de Programación** (UTP). Automatiza el ciclo de atención de un restaurante:
toma de pedidos por mesa, comunicación con cocina, cobro y control de inventario, con la base de datos
PostgreSQL desplegada en la nube.

**Paradigma:** Programación Orientada a Objetos (herencia, encapsulamiento, composición, polimorfismo).
**Stack:** Java 17 · Spring Boot 3.5 · Spring Data JPA / Hibernate · PostgreSQL (Supabase) · Maven.

## Arquitectura

```
controller  →  service  →  repository  →  model (entidades JPA con las reglas de negocio)
   (API REST)   (casos de uso)  (Spring Data)    + dto (entrada/salida) + exception (errores)
```

## Requisitos

- JDK 17 o superior
- Maven 3.9+ (o un IDE que abra el `pom.xml`: IntelliJ, NetBeans, Eclipse, VS Code)
- Un proyecto de base de datos PostgreSQL en la nube (por ejemplo Supabase, plan gratuito)

## Puesta en marcha

### 1. Crear la base de datos en la nube
1. Crea un proyecto en Supabase y guarda la contraseña de la base de datos.
2. Abre **SQL Editor**, pega el contenido de `sql/schema.sql` y ejecútalo (crea las 10 tablas, índices y la vista).
3. En el botón **Connect** del panel copia los datos de conexión (host, usuario y puerto). Se recomienda
   la conexión por *pooler* en modo sesión (puerto 5432).

### 2. Configurar las credenciales (variables de entorno)

La aplicación no guarda credenciales en el código. Define estas tres variables antes de ejecutar:

| Variable | Ejemplo |
|---|---|
| `DB_URL` | `jdbc:postgresql://<HOST>:5432/postgres?sslmode=require` |
| `DB_USER` | `postgres.<id-del-proyecto>` |
| `DB_PASSWORD` | la contraseña de tu base de datos |

PowerShell (Windows):
```powershell
$env:DB_URL="jdbc:postgresql://<HOST>:5432/postgres?sslmode=require"
$env:DB_USER="postgres.<id-del-proyecto>"
$env:DB_PASSWORD="<tu-contraseña>"
```
Linux / macOS:
```bash
export DB_URL="jdbc:postgresql://<HOST>:5432/postgres?sslmode=require"
export DB_USER="postgres.<id-del-proyecto>"
export DB_PASSWORD="<tu-contraseña>"
```
En un IDE, define las mismas variables en la configuración de ejecución (*Run configuration*).

### 3. Ejecutar
```bash
mvn spring-boot:run
```
Si todo está bien verás en consola `HikariPool-1 - Start completed` y `Started PedidosApplication`
(esa es la evidencia de conexión a la nube). En la primera ejecución se cargan datos de demostración
(18 mesas, la carta, insumos y usuarios). La API queda en `http://localhost:8080`.

> Si Hibernate se queja al validar el esquema, confirma que ejecutaste `sql/schema.sql` completo.
> Como alternativa puedes cambiar `spring.jpa.hibernate.ddl-auto` a `none` en `application.properties`.

### 4. Pruebas unitarias (no necesitan base de datos)
```bash
mvn test
```

## Usuarios de demostración (contraseña `123456`)

| Usuario | Rol |
|---|---|
| `mesero1`, `mesero2` | Mesero |
| `cocinero1` | Cocinero |
| `cajero1` | Cajero |
| `admin` | Administrador |

## Endpoints principales

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/auth/login` | Inicio de sesión |
| GET | `/api/mesas` | Lista de mesas y su estado |
| GET / POST | `/api/productos` | Carta (`?soloDisponibles=true`) / crear producto |
| PATCH | `/api/productos/{id}/disponibilidad` | Activar o desactivar un plato |
| GET | `/api/insumos` | Inventario y alerta de stock mínimo |
| POST | `/api/insumos/{id}/reposicion` | Reponer stock |
| POST | `/api/pedidos` | Abrir pedido (ocupa la mesa) |
| POST | `/api/pedidos/{id}/detalles` | Agregar producto (valida stock) |
| POST | `/api/pedidos/{id}/avanzar` | Cocina: RECIBIDO → EN_PREPARACION → LISTO |
| PATCH | `/api/pedidos/{id}/estado` | Marcar SERVIDO o CANCELADO |
| POST | `/api/pedidos/{id}/pago` | Cobrar: descuenta stock y libera la mesa |
| GET | `/api/pedidos` · `/api/pedidos/{id}` | Consultar pedidos (`?estado=LISTO`) |
| GET | `/api/reportes/ventas?desde=AAAA-MM-DD&hasta=AAAA-MM-DD` | Ventas por turno y producto |

El archivo `requests.http` contiene el flujo completo listo para ejecutar (VS Code con la extensión
*REST Client*, IntelliJ, o copiando las peticiones a Postman).

## Estructura

```
sql/schema.sql                      script DDL de la base de datos
src/main/java/com/saborcriollo/pedidos/
├── model/ (+ enums/)               entidades y reglas de negocio
├── repository/                     acceso a datos (Spring Data JPA)
├── service/                        casos de uso y transacciones
├── controller/                     API REST
├── dto/                            objetos de entrada y salida
├── exception/                      errores y manejador global
└── config/DataSeeder.java          datos de demostración
src/test/java/...                   pruebas unitarias del dominio
```
