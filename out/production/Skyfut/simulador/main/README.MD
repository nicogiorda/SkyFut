# SkyFut — Simulador de Torneo Eliminatorio de Fútbol

Trabajo Práctico Integrador — **Proceso de Desarrollo de Software** (UADE, 2026).

SkyFut es un simulador de torneo eliminatorio donde el usuario asume el rol de **Director Técnico (DT)**: selecciona un equipo, gestiona el plantel, toma decisiones tácticas durante el entretiempo y avanza por el cuadro eliminatorio hasta la final. El proyecto aplica de forma integrada siete patrones de diseño GoF (Singleton, Factory Method, Composite, Facade, State, Strategy y Decorator) sobre un caso de uso realista.



---

## Tabla de contenidos

- [Requisitos previos](#requisitos-previos)
- [Tecnologías](#tecnologías)
- [Instalación y despliegue](#instalación-y-despliegue)
- [Base de datos](#base-de-datos)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Uso del programa](#uso-del-programa)
- [Troubleshooting](#troubleshooting)
- [Autores](#autores)

---

## Requisitos previos

| Requisito | Versión sugerida | Notas |
|-----------|------------------|-------|
| **JDK (Java)** | ⚠️ *17 o superior* | Confirmá la versión usada en el proyecto. Verificá con `java -version`. |
| **Git** | Cualquiera reciente | Para clonar el repositorio. |
| **IntelliJ IDEA** | 2023.x o superior | Recomendado: el proyecto fue desarrollado y configurado en IntelliJ. |
| **Driver SQLite** | `sqlite-jdbc` (Xerial) | Se incluye como dependencia; no requiere instalar SQLite por separado. |

> No es necesario instalar un motor de base de datos. SQLite funciona embebido a través del driver `sqlite-jdbc`, y el archivo de base de datos se genera automáticamente en la primera ejecución.

---

## Tecnologías

- **Lenguaje:** Java
- **Interfaz gráfica:** Java Swing (`SkyFutFrame extends JFrame`, `FixturePanel extends JPanel`)
- **Persistencia:** SQLite mediante el driver JDBC Xerial (`sqlite-jdbc`)
- **Arquitectura:** organización por capa + por patrón de diseño bajo el paquete raíz `simulador`

---

## Instalación y despliegue

### 1. Clonar el repositorio

```bash
git clone https://github.com/nicogiorda/SkyFut.git
cd SkyFut
```

### 2. Abrir el proyecto

El proyecto Maven está dentro de la subcarpeta **`Skyfut/`**. En **IntelliJ IDEA** abrí esa carpeta directamente o importá el `pom.xml` ubicado en `Skyfut/pom.xml`.

- `File → Open…` → seleccioná la carpeta `Skyfut/`
- o `File → New → Project from Existing Sources…` → elegí `Skyfut/pom.xml`

IntelliJ detectará automáticamente el SDK de Java configurado. Si pide configurar el JDK, seleccioná uno **17 o superior** instalado en tu equipo.

### 3. Resolver dependencias

El proyecto usa **Maven**, así que IntelliJ va a bajar automáticamente `sqlite-jdbc` y `junit-jupiter` al importar `Skyfut/pom.xml`.

Si querés validar desde terminal dentro de la carpeta `Skyfut/`:

```bash
mvn test
```

> Si tu equipo no tiene Maven instalado globalmente, IntelliJ puede usar su Maven embebido al importar el proyecto.

### 4. Ejecutar la aplicación

La clase de arranque es **`Main`** (paquete `simulador.main`). Tenés dos opciones:

**Desde IntelliJ:**
1. Navegá a `src/main/java/simulador/main/Main.java`.
2. Hacé clic en el botón ▶ junto a la declaración de `main()`, o usá `Shift + F10`.

La clase de arranque es `simulador.main.Main` y abre la ventana Swing `SkyFutFrame`.

**Desde la terminal** (si ya compilaste un JAR ejecutable):
```bash
java -jar SkyFut.jar
```

Al iniciar, se abre la ventana principal del simulador y la base de datos se inicializa automáticamente. El archivo SQLite se crea en `db/torneo.db` y los scripts de esquema/seed se resuelven tanto desde el filesystem como desde el classpath, por lo que funciona aunque IntelliJ cambie el working directory.

---

## Base de datos

La base de datos **se crea e inicializa sola en la primera ejecución**. No hay que correr ningún script manualmente.

Esto lo maneja la clase `DatabaseConnection` (paquete `simulador.persistence`), implementada como **Singleton** sobre el driver Xerial `sqlite-jdbc`. Esa clase:

- Garantiza una **única instancia de conexión** a SQLite durante toda la ejecución.
- Inicializa el esquema y los datos base en los métodos `inicializarBaseDatos()` y `cargarScript()`.
- Resuelve los scripts SQL en más de una ubicación posible, así el proyecto puede ejecutarse desde IntelliJ aunque abras la raíz del repo o la subcarpeta `Skyfut/`.

Como consecuencia:

- **Primera ejecución:** se crea el archivo `.db`, se construye el esquema y se cargan los datos iniciales (equipos y jugadores).
- **Ejecuciones siguientes:** se reutiliza el archivo existente, conservando resultados, estadísticas e historial del torneo entre sesiones.

> 💡 Si querés **resetear el torneo desde cero**, eliminá el archivo `.db` generado y volvé a ejecutar la aplicación: se regenerará limpio.

---

## Estructura del proyecto

El proyecto se organiza bajo el paquete raíz `simulador`, combinando separación **por capa** y **por patrón de diseño** (cada patrón GoF vive en su propio paquete, de modo que el árbol de carpetas es autodocumentado).

```
simulador
├── domain        → Núcleo del modelo: IJugador, Jugador, Equipo, EstadisticasJugador
├── decorator     → Decorator: JugadorDecorator + Cansancio/Gol/Lesion/TarjetaAmarilla
├── events        → Factory Method: EventoFactory, EventoPartido y fábricas/productos concretos
├── dto           → DTOs inmutables: ContextoEvento, ResumenTorneo, ResultadoPartido, Goleador…
├── composite     → Composite: ComponenteTorneo, Torneo, Fase, Partido
├── state         → State: EstadoPartido + NoIniciado/PrimerTiempo/Entretiempo/SegundoTiempo/Finalizado
├── strategy      → Strategy: TacticaStrategy + Ofensiva/Defensiva/Equilibrada
├── facade        → Facade: TorneoFacade (punto de entrada único de la UI)
├── service       → Orquestación: GestorTorneo, GestorPartido, GestorFases, CalculadorEstadisticas
├── motor         → MotorSimulacion (avance minuto a minuto del partido)
├── repositorio   → DAO/Repository: RepositorioEquipo/Jugador/Partido/Torneo
├── persistence   → DatabaseConnection (Singleton, conexión SQLite + init de esquema)
├── ui            → Swing: SkyFutFrame (ventana principal), FixturePanel (cuadro/fixture)
└── main          → Main (clase de arranque)
```

---

## Uso del programa

Una vez iniciada la aplicación:

1. **Seleccionar equipo a dirigir** — Elegí, entre los equipos cargados en la BD, el que vas a dirigir como DT.
2. **Gestionar plantel** — Revisá y confirmá la alineación titular (11 jugadores) y el banco de suplentes. El sistema bloquea la confirmación si no hay 11 titulares.
3. **Simular tu partido** — La simulación avanza minuto a minuto y genera eventos (goles, tarjetas, lesiones, cambios). El rendimiento de los jugadores se modifica dinámicamente según cansancio, goles, lesiones y tarjetas (decoradores apilables).
4. **Entretiempo** — Solo durante el entretiempo podés **realizar cambios** y **modificar la táctica** (ofensiva, defensiva o equilibrada). Fuera de ese estado, esas acciones están deshabilitadas.
5. **Avance del cuadro** — Los demás partidos del cuadro se simulan automáticamente. El ganador de cada cruce avanza a la fase siguiente.
6. **Consultas** — Podés consultar el fixture dinámico, resultados, goleadores y estadísticas en cualquier nivel del torneo.
7. **Persistencia** — Al finalizar cada partido, resultados, estadísticas e historial se guardan automáticamente en SQLite.

---

## Troubleshooting

| Problema | Causa probable | Solución |
|----------|----------------|----------|
| `ClassNotFoundException: org.sqlite.JDBC` | El driver `sqlite-jdbc` no está en el classpath. | Verificá que la dependencia esté declarada (Maven/Gradle) o que el `.jar` esté agregado en `Project Structure → Libraries`. |
| La ventana no abre / `HeadlessException` | Se ejecutó en un entorno sin entorno gráfico (servidor headless). | Ejecutá en un equipo con interfaz gráfica; Swing requiere display. |
| `UnsupportedClassVersionError` | La versión de Java de ejecución es menor a la usada para compilar. | Instalá/seleccioná un JDK igual o superior al del proyecto (`java -version`). |
| Los datos del torneo "no se reinician" | El archivo `.db` persiste entre sesiones (es el comportamiento esperado). | Borrá el archivo `.db` generado para empezar un torneo limpio. |
| `SQLITE_BUSY` / base bloqueada | Hay otra instancia de la app usando el mismo archivo. | Cerrá las demás instancias; el Singleton asume una única conexión por sesión. |
| Error al compilar en IntelliJ | El JDK del proyecto no está configurado. | `File → Project Structure → Project SDK` → seleccioná un JDK válido. |

---

## Autores

| Integrante | Legajo |
|------------|--------|
| Lan Franco | LU1188365 |
| Giordano Nicolás Luca | LU1188062 |
| Umansky Nicolás | LU1188098 |

**Materia:** Proceso de Desarrollo de Software — Facultad de Ingeniería, UADE (2026)
**Profesora:** Maria Angela Leon
