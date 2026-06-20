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
| **JDK (Java)** | 17 o superior | Verificá con `java -version`. El proyecto compila con Java 17+. |
| **Git** | Cualquiera reciente | Para clonar el repositorio. |
| **IntelliJ IDEA** | 2023.x o superior | Community Edition (gratuita) es suficiente. |
| **Driver SQLite** | `sqlite-jdbc` (Xerial) | Se descarga automáticamente por Maven; no requiere instalar SQLite por separado. |

> No es necesario instalar un motor de base de datos. SQLite funciona embebido a través del driver `sqlite-jdbc`, y el archivo de base de datos se genera automáticamente en la primera ejecución.

> Los archivos de configuración de IntelliJ (`.idea/`, `*.iml`) **no están incluidos en el repositorio**. Cada desarrollador los genera localmente al importar el `pom.xml`.

---

## Tecnologías

- **Lenguaje:** Java 17+
- **Interfaz gráfica:** Java Swing (`SkyFutFrame extends JFrame`, `FixturePanel extends JPanel`)
- **Persistencia:** SQLite mediante el driver JDBC Xerial (`sqlite-jdbc`)
- **Build:** Maven (`pom.xml` en `Skyfut/`)
- **Arquitectura:** organización por capa + por patrón de diseño bajo el paquete raíz `simulador`

---

## Instalación y despliegue

### 1. Clonar el repositorio

```bash
git clone https://github.com/nicogiorda/SkyFut.git
cd SkyFut
```

### 2. Abrir el proyecto en IntelliJ

El proyecto Maven está dentro de la subcarpeta **`Skyfut/`**. Abrilo apuntando directamente al `pom.xml`:

- `File → Open…` → navegá hasta `Skyfut/pom.xml` → **"Open as Project"**

IntelliJ detectará el JDK configurado en tu equipo. Si pide configurarlo, andá a `File → Project Structure → Project SDK` y seleccioná uno **17 o superior**.

### 3. Sincronizar dependencias Maven

Al abrir el `pom.xml` como proyecto, IntelliJ debería ofrecer sincronizar Maven automáticamente. Si no lo hace:

- Click derecho sobre `pom.xml` en el árbol de archivos → **"Add as Maven Project"**

Esto descarga `sqlite-jdbc` y `junit-jupiter` desde Maven Central y los agrega al classpath. Es obligatorio antes de ejecutar.

### 4. Ejecutar la aplicación

La clase de arranque es **`Main`** (paquete `simulador.main`):

1. Navegá a `src/main/java/simulador/main/Main.java`.
2. Hacé clic en el botón ▶ junto a la declaración de `main()`, o usá `Shift + F10`.

Se abre la ventana principal del simulador (`SkyFutFrame`) y la base de datos se inicializa automáticamente.

**Desde la terminal**, ubicándote dentro de `Skyfut/`:
```bash
mvn exec:java
```

> El JAR simple generado por Maven no incluye internamente el driver SQLite. Para ejecutar sin configurar manualmente el classpath, usá IntelliJ o `mvn exec:java`.

---

## Base de datos

La base de datos **se crea e inicializa sola en la primera ejecución**. No hay que correr ningún script manualmente.

Esto lo maneja la clase `DatabaseConnection` (paquete `simulador.persistence`), implementada como **Singleton** sobre el driver Xerial `sqlite-jdbc`. Esa clase:

- Garantiza una **única instancia de conexión** a SQLite durante toda la ejecución.
- Inicializa el esquema y los datos base ejecutando `schema.sql` y `seed.sql`.
- Resuelve los scripts SQL desde el classpath (`src/main/resources/db/`) y también desde el filesystem como fallback, por lo que funciona independientemente del working directory que use IntelliJ.

El archivo `torneo.db` **no está en el repositorio** (está en `.gitignore`). Cada máquina genera su propia base de datos limpia al ejecutar por primera vez.

Como consecuencia:

- **Primera ejecución:** se crea el archivo `.db` en `Skyfut/db/`, se construye el esquema y se cargan los datos iniciales (equipos y jugadores).
- **Ejecuciones siguientes:** se reutiliza el archivo existente, conservando resultados e historial. Las estadísticas temporales de jugadores se muestran al finalizar cada torneo y luego se limpian para no mezclarlas con el torneo siguiente.

> Si querés **resetear el torneo desde cero**, eliminá el archivo `Skyfut/db/torneo.db` y volvé a ejecutar la aplicación: se regenerará limpio.

---

## Estructura del proyecto

El proyecto se organiza bajo el paquete raíz `simulador`, combinando separación **por capa** y **por patrón de diseño** (cada patrón GoF vive en su propio paquete, de modo que el árbol de carpetas es autodocumentado).

```
Skyfut/
├── pom.xml
├── db/                     → schema.sql y seed.sql (también en src/main/resources/db/)
└── src/
    └── main/
        ├── java/simulador/
        │   ├── domain        → Núcleo del modelo: IJugador, Jugador, Equipo, EstadisticasJugador
        │   ├── decorator     → Decorator: JugadorDecorator + Cansancio/Gol/Lesion/TarjetaAmarilla
        │   ├── events        → Factory Method: EventoFactory, EventoPartido y fábricas/productos concretos
        │   ├── dto           → DTOs inmutables: ContextoEvento, ResumenTorneo, ResultadoPartido, Goleador…
        │   ├── composite     → Composite: ComponenteTorneo, Torneo, Fase, Partido
        │   ├── state         → State: EstadoPartido + NoIniciado/PrimerTiempo/Entretiempo/SegundoTiempo/Finalizado
        │   ├── strategy      → Strategy: TacticaStrategy + Ofensiva/Defensiva/Equilibrada
        │   ├── facade        → Facade: TorneoFacade (punto de entrada único de la UI)
        │   ├── service       → Orquestación: GestorTorneo, GestorPartido, GestorFases, CalculadorEstadisticas
        │   ├── motor         → MotorSimulacion (avance minuto a minuto del partido)
        │   ├── repositorio   → DAO/Repository: RepositorioEquipo/Jugador/Partido/Torneo
        │   ├── persistence   → DatabaseConnection (Singleton, conexión SQLite + init de esquema)
        │   ├── ui            → Swing: SkyFutFrame (ventana principal), FixturePanel (cuadro/fixture)
        │   └── main          → Main (clase de arranque)
        └── resources/
            └── db/           → schema.sql y seed.sql (classpath, usados por DatabaseConnection)
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
| `ClassNotFoundException: org.sqlite.JDBC` | Maven no fue sincronizado; `sqlite-jdbc` no está en el classpath. | Click derecho sobre `pom.xml` → **"Add as Maven Project"** y esperá que descargue las dependencias. |
| La ventana no abre / `HeadlessException` | Se ejecutó en un entorno sin entorno gráfico (servidor headless). | Ejecutá en un equipo con interfaz gráfica; Swing requiere display. |
| `UnsupportedClassVersionError` | La versión de Java de ejecución es menor a la usada para compilar. | Instalá/seleccioná un JDK 17 o superior (`java -version`). |
| JDK no encontrado al abrir IntelliJ | El JDK no está configurado en IntelliJ. | `File → Project Structure → Project SDK` → seleccioná o descargá un JDK 17+. |
| Los datos del torneo "no se reinician" | El archivo `.db` persiste entre sesiones (es el comportamiento esperado). | Borrá `Skyfut/db/torneo.db` para empezar un torneo limpio. |
| `SQLITE_BUSY` / base bloqueada | Hay otra instancia de la app usando el mismo archivo. | Cerrá las demás instancias; el Singleton asume una única conexión por sesión. |
| Warnings de SLF4J o `--enable-native-access` | Logs informativos del driver SQLite con JDK 17+. | Son inofensivos, no afectan el funcionamiento. |

---

## Autores

| Integrante | Legajo |
|------------|--------|
| Lan Franco | LU1188365 |
| Giordano Nicolás Luca | LU1188062 |
| Umansky Nicolás | LU1188098 |

**Materia:** Proceso de Desarrollo de Software — Facultad de Ingeniería, UADE (2026)
**Profesora:** Maria Angela Leon
