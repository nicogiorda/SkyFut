# SIMULADOR DE TORNEO DE FÚTBOL
## Contexto completo del proyecto para Claude Code

---

## ROL Y OBJETIVO

Sos el asistente de desarrollo de este proyecto. El sistema es un **simulador de torneo eliminatorio de fútbol** en Java, con arquitectura basada en patrones de diseño GoF. El usuario actúa como Director Técnico (DT) de un equipo. La interfaz es CLI (línea de comandos). La persistencia es SQLite via JDBC.

Cuando te pida código, seguí estrictamente la arquitectura definida aquí. Si algo contradice este diseño, avisalo antes de implementar.

---

## STACK TECNOLÓGICO

- **Lenguaje:** Java 17+
- **Base de datos:** SQLite via JDBC (`org.xerial:sqlite-jdbc`)
- **Build:** Maven o Gradle (a definir)
- **Interfaz:** CLI (Scanner + System.out)
- **Testing:** JUnit 5
- **Sin frameworks externos** (no Spring, no Hibernate)

---

## PATRONES DE DISEÑO APLICADOS

### 1. SINGLETON — `DatabaseConnection`
- Única instancia de conexión a SQLite durante toda la ejecución
- `autoCommit = false` para control transaccional manual
- Los repositorios obtienen la conexión via `DatabaseConnection.getInstance().getConnection()`
- **NUNCA** crear conexiones directas fuera de esta clase

### 2. FACTORY METHOD — Eventos del partido
- **`EventoFactory` (interfaz Creator):** método `crearEvento(ContextoEvento ctx) : Optional<EventoPartido>`
- **`EventoPartido` (interfaz Product):** métodos `getMinuto()`, `getDescripcion()`, `aplicar(Partido p)`
- **Factories concretas:** `GolFactory`, `TarjetaFactory`, `CambioFactory`, `LesionFactory`
- **Productos concretos:** `Gol`, `Tarjeta`, `Cambio`, `Lesion`
- **`ContextoEvento` (DTO):** empaqueta el estado del partido en un minuto dado. Campos: `minuto`, `local : Equipo`, `visitante : Equipo`, `esSegundoTiempo : boolean`. Métodos: `getRendimientoAtaque(Equipo)`, `getRendimientoDefensa(Equipo)`
- La aleatoriedad vive **dentro de cada factory**, no en el motor
- El motor NO decide qué evento ocurre; cada factory decide si genera o no su evento

### 3. COMPOSITE — Estructura del torneo
- **`ComponenteTorneo` (interfaz Component):** `getResultados()`, `estaCompleto()`, `getGoleadores()`
- **`Torneo` (Composite):** contiene `List<Fase>`
- **`Fase` (Composite):** contiene `List<Partido>`
- **`Partido` (Leaf):** hoja del árbol, también es Context del patrón State
- Permite consultar resultados en cualquier nivel con la misma interfaz

### 4. FACADE — `TorneoFacade`
- Único punto de entrada para la CLI
- Coordina: `MotorSimulacion`, repositorios, `Torneo`
- Métodos: `iniciarTorneo()`, `simularSiguientePartido()`, `realizarCambio(IJugador, IJugador)`, `cambiarTactica(TacticaStrategy)`, `consultarResultados() : ResumenTorneo`
- **NO** accede a SQLite directamente; delega en repositorios

### 5. STATE — Estados del partido
- **`EstadoPartido` (interfaz):** `iniciar(Partido)`, `avanzar(Partido)`, `permiteCambios()`, `permiteEventosJuego()`, `getNombre()`
- **Estados concretos:** `NoIniciado`, `PrimerTiempo`, `Entretiempo`, `SegundoTiempo`, `Finalizado`
- `Partido` es el Context; tiene `private EstadoPartido estado`
- `permiteCambios()` → solo `Entretiempo` retorna `true`
- `permiteEventosJuego()` → solo `PrimerTiempo` y `SegundoTiempo` retornan `true`
- Las transiciones de estado las maneja `MotorSimulacion` según el minuto

### 6. STRATEGY — Tácticas de juego
- **`TacticaStrategy` (interfaz):** `getModificadorAtaque() : double`, `getModificadorDefensa() : double`, `getFormacion() : String`
- **Concretas:** `Tactica442`, `Tactica433`, `Tactica352`, `Tactica4231`
- `Equipo` tiene `private TacticaStrategy tactica`
- El DT puede cambiar la táctica **solo durante `Entretiempo`** (lo valida State)

### 7. DECORATOR — Rendimiento del jugador
- **`IJugador` (interfaz Component):** `getRendimiento() : double`, `getNombre() : String`, `getPosicion() : String`
- **`Jugador` (ConcreteComponent):** tiene `rendimientoBase : double`
- **`JugadorDecorator` (abstracta):** wrappea `IJugador`, delega por defecto
- **Decorators concretos:**
  - `CansancioDecorator`: reduce rendimiento progresivamente por minuto
  - `LesionDecorator`: reduce rendimiento significativamente (0.3 del base)
  - `TarjetaAmarillaDecorator`: reduce rendimiento levemente (0.1)
- Los decorators se **apilan**: `new CansancioDecorator(new TarjetaAmarillaDecorator(jugador))`
- `Equipo` referencia `List<IJugador>` (no `List<Jugador>`), para que los decorators sean transparentes

---

## CLASES PRINCIPALES

### Dominio
```
IJugador (interfaz)
Jugador implements IJugador
JugadorDecorator extends IJugador (abstracta)
CansancioDecorator extends JugadorDecorator
LesionDecorator extends JugadorDecorator
TarjetaAmarillaDecorator extends JugadorDecorator

Equipo
  - nombre : String
  - titulares : List<IJugador>
  - suplentes : List<IJugador>
  - tactica : TacticaStrategy
  + getRendimientoTotal() : double
  + setTactica(TacticaStrategy)
  + realizarCambio(IJugador sale, IJugador entra)

MotorSimulacion
  - factories : List<EventoFactory>
  + simularPartido(Partido) : void
  + simularAutomatico(Partido) : void
```

### Torneo (Composite)
```
ComponenteTorneo (interfaz)
Torneo implements ComponenteTorneo
Fase implements ComponenteTorneo
Partido implements ComponenteTorneo
  - local : Equipo
  - visitante : Equipo
  - golesLocal : int
  - golesVisitante : int
  - minuto : int
  - estado : EstadoPartido
  - eventos : List<EventoPartido>
```

### Eventos (Factory Method)
```
EventoPartido (interfaz)
Gol implements EventoPartido
Tarjeta implements EventoPartido
Cambio implements EventoPartido
Lesion implements EventoPartido

EventoFactory (interfaz)
GolFactory implements EventoFactory
TarjetaFactory implements EventoFactory
CambioFactory implements EventoFactory
LesionFactory implements EventoFactory

ContextoEvento (DTO)
```

### Persistencia
```
DatabaseConnection (Singleton)
RepositorioEquipo
RepositorioJugador
RepositorioPartido
```

### DTOs
```
ContextoEvento     → usado por EventoFactory
ResumenTorneo      → retornado por TorneoFacade
ResultadoPartido   → parte de ResumenTorneo
Goleador           → parte de ResumenTorneo
```

---

## BASE DE DATOS — ESQUEMA COMPLETO

```sql
CREATE TABLE tactica (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre      TEXT NOT NULL,
    formacion   TEXT NOT NULL,
    mod_ataque  REAL NOT NULL DEFAULT 1.0,
    mod_defensa REAL NOT NULL DEFAULT 1.0
);

CREATE TABLE equipo (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre      TEXT NOT NULL UNIQUE,
    id_tactica  INTEGER NOT NULL,
    FOREIGN KEY (id_tactica) REFERENCES tactica(id)
);

CREATE TABLE jugador (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre           TEXT NOT NULL,
    posicion         TEXT NOT NULL CHECK(posicion IN ('POR','DEF','MED','DEL')),
    rendimiento_base REAL NOT NULL DEFAULT 1.0,
    id_equipo        INTEGER NOT NULL,
    FOREIGN KEY (id_equipo) REFERENCES equipo(id)
);

CREATE TABLE plantel (
    id_equipo    INTEGER NOT NULL,
    id_jugador   INTEGER NOT NULL,
    rol_inicial  TEXT NOT NULL CHECK(rol_inicial IN ('TITULAR','SUPLENTE')),
    numero_dorsal INTEGER,
    PRIMARY KEY (id_equipo, id_jugador),
    FOREIGN KEY (id_equipo)  REFERENCES equipo(id),
    FOREIGN KEY (id_jugador) REFERENCES jugador(id)
);

CREATE TABLE torneo (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre       TEXT NOT NULL,
    fecha_inicio TEXT,
    estado       TEXT NOT NULL DEFAULT 'EN_CURSO'
                 CHECK(estado IN ('EN_CURSO','FINALIZADO')),
    id_equipo_dt INTEGER,
    id_campeon   INTEGER,
    FOREIGN KEY (id_equipo_dt) REFERENCES equipo(id),
    FOREIGN KEY (id_campeon)   REFERENCES equipo(id)
);

CREATE TABLE fase (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    id_torneo  INTEGER NOT NULL,
    nombre     TEXT NOT NULL,
    orden      INTEGER NOT NULL,
    completada INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (id_torneo) REFERENCES torneo(id)
);

CREATE TABLE partido (
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    id_fase           INTEGER NOT NULL,
    id_equipo_local   INTEGER NOT NULL,
    id_equipo_visit   INTEGER NOT NULL,
    goles_local       INTEGER DEFAULT 0,
    goles_visitante   INTEGER DEFAULT 0,
    id_ganador        INTEGER,
    estado            TEXT NOT NULL DEFAULT 'NO_INICIADO'
                      CHECK(estado IN ('NO_INICIADO','PRIMER_TIEMPO',
                                       'ENTRETIEMPO','SEGUNDO_TIEMPO','FINALIZADO')),
    id_tactica_local  INTEGER,
    id_tactica_visit  INTEGER,
    FOREIGN KEY (id_fase)          REFERENCES fase(id),
    FOREIGN KEY (id_equipo_local)  REFERENCES equipo(id),
    FOREIGN KEY (id_equipo_visit)  REFERENCES equipo(id),
    FOREIGN KEY (id_ganador)       REFERENCES equipo(id),
    FOREIGN KEY (id_tactica_local) REFERENCES tactica(id),
    FOREIGN KEY (id_tactica_visit) REFERENCES tactica(id)
);

CREATE TABLE evento_partido (
    id                    INTEGER PRIMARY KEY AUTOINCREMENT,
    id_partido            INTEGER NOT NULL,
    tipo                  TEXT NOT NULL CHECK(tipo IN ('GOL','TARJETA','CAMBIO','LESION')),
    minuto                INTEGER NOT NULL,
    id_jugador_principal  INTEGER,
    id_jugador_secundario INTEGER,
    id_equipo             INTEGER NOT NULL,
    descripcion           TEXT,
    FOREIGN KEY (id_partido)            REFERENCES partido(id),
    FOREIGN KEY (id_jugador_principal)  REFERENCES jugador(id),
    FOREIGN KEY (id_jugador_secundario) REFERENCES jugador(id),
    FOREIGN KEY (id_equipo)             REFERENCES equipo(id)
);

CREATE TABLE estadistica_jugador (
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    id_partido         INTEGER NOT NULL,
    id_jugador         INTEGER NOT NULL,
    id_equipo          INTEGER NOT NULL,
    goles              INTEGER NOT NULL DEFAULT 0,
    asistencias        INTEGER NOT NULL DEFAULT 0,
    tarjetas_amarillas INTEGER NOT NULL DEFAULT 0,
    tarjeta_roja       INTEGER NOT NULL DEFAULT 0,
    lesionado          INTEGER NOT NULL DEFAULT 0,
    minutos_jugados    INTEGER NOT NULL DEFAULT 0,
    rendimiento_final  REAL,
    UNIQUE (id_partido, id_jugador),
    FOREIGN KEY (id_partido)  REFERENCES partido(id),
    FOREIGN KEY (id_jugador)  REFERENCES jugador(id),
    FOREIGN KEY (id_equipo)   REFERENCES equipo(id)
);

CREATE TABLE estado_jugador_torneo (
    id                      INTEGER PRIMARY KEY AUTOINCREMENT,
    id_torneo               INTEGER NOT NULL,
    id_jugador              INTEGER NOT NULL,
    cansancio               REAL NOT NULL DEFAULT 0.0,
    lesionado               INTEGER NOT NULL DEFAULT 0,
    tarjetas_amarillas_acum INTEGER NOT NULL DEFAULT 0,
    suspendido              INTEGER NOT NULL DEFAULT 0,
    UNIQUE (id_torneo, id_jugador),
    FOREIGN KEY (id_torneo)  REFERENCES torneo(id),
    FOREIGN KEY (id_jugador) REFERENCES jugador(id)
);
```

---

## REGLAS DE DISEÑO — NO ROMPER

1. `plantel.rol_inicial` **nunca se modifica** durante un partido. Los cambios en el entretiempo solo modifican las listas en memoria de `Equipo`.
2. `Equipo` referencia `List<IJugador>`, nunca `List<Jugador>`. Esto permite apilar decorators transparentemente.
3. La aleatoriedad de eventos vive en las factories, no en `MotorSimulacion`.
4. `MotorSimulacion` consulta `EstadoPartido.permiteEventosJuego()` antes de invocar factories.
5. `TorneoFacade` consulta `EstadoPartido.permiteCambios()` antes de ejecutar acciones del DT.
6. Los repositorios son los únicos que acceden a `DatabaseConnection`. Ninguna otra clase toca JDBC directamente.
7. `Partido` es Leaf en el Composite Y Context en el State. Doble rol justificado.
8. `plantel` no existe como clase Java. Es solo una tabla de BD.
9. `ContextoEvento` es inmutable. Todos sus campos son `final`.
10. Tests deben usar SQLite en memoria (`jdbc:sqlite::memory:`).

---

## FLUJO DE SIMULACIÓN

```
partido.setEstado(new NoIniciado())
partido.iniciar() → estado = PrimerTiempo

LOOP minuto 1..90:
  if minuto == 45 → estado = Entretiempo
    if es partido del DT → CLI muestra opciones (cambios / táctica)
    continuar (no generar eventos en entretiempo)
  if minuto == 46 → estado = SegundoTiempo
  if minuto == 90 → estado = Finalizado → break

  if !estado.permiteEventosJuego() → continue

  ctx = new ContextoEvento(minuto, local, visitante, minuto > 45)
  for factory in factories:
    factory.crearEvento(ctx).ifPresent(e -> {
      e.aplicar(partido)
      partido.registrarEvento(e)
    })

repositorioPartido.guardarResultado(partido)
repositorioPartido.guardarEstadisticas(...)
repositorioPartido.guardarEventos(...)
```

---

## ESTRUCTURA DE PAQUETES

```
simulador/
├── db/           → DatabaseConnection
├── dominio/      → Equipo, IJugador, Jugador, EstadisticasJugador
├── decorator/    → JugadorDecorator, CansancioDecorator, LesionDecorator, TarjetaAmarillaDecorator
├── estado/       → EstadoPartido, NoIniciado, PrimerTiempo, Entretiempo, SegundoTiempo, Finalizado
├── evento/       → EventoPartido, EventoFactory, ContextoEvento
│   ├── factory/  → GolFactory, TarjetaFactory, CambioFactory, LesionFactory
│   └── modelo/   → Gol, Tarjeta, Cambio, Lesion
├── composite/    → ComponenteTorneo, Torneo, Fase, Partido
├── strategy/     → TacticaStrategy, Tactica442, Tactica433, Tactica352, Tactica4231
├── facade/       → TorneoFacade
├── motor/        → MotorSimulacion
├── repositorio/  → RepositorioEquipo, RepositorioJugador, RepositorioPartido
└── dto/          → ResumenTorneo, ResultadoPartido, Goleador
```

---

## NOTAS PARA CLAUDE CODE

- Si te pido implementar una clase, respetá los atributos y métodos definidos arriba.
- Si algo no está definido aquí, preguntá antes de inventar.
- No uses reflection, proxies ni librerías de inyección de dependencias.
- Cuando implementes factories, `Optional.empty()` significa "este minuto no hubo este tipo de evento", no es un error.
- El `ContextoEvento` es inmutable (campos `final`, sin setters).
- Ante cualquier duda de diseño, priorizá la separación de responsabilidades sobre la brevedad del código.
