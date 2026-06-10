-- ESQUEMA COMPLETO PARA SKYFUT
-- Base de Datos SQLite para Simulador de Torneo de Fútbol

CREATE TABLE IF NOT EXISTS tactica (
                         id INTEGER PRIMARY KEY AUTOINCREMENT,
                         nombre TEXT NOT NULL UNIQUE,
                         formacion TEXT NOT NULL,
                         mod_ataque REAL NOT NULL DEFAULT 1.0,
                         mod_defensa REAL NOT NULL DEFAULT 1.0
);

CREATE TABLE IF NOT EXISTS equipo (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT NOT NULL UNIQUE,
                        id_tactica INTEGER NOT NULL,
                        FOREIGN KEY (id_tactica) REFERENCES tactica(id)
);

CREATE TABLE IF NOT EXISTS jugador (
                         id INTEGER PRIMARY KEY AUTOINCREMENT,
                         nombre TEXT NOT NULL,
                         posicion TEXT NOT NULL CHECK(posicion IN ('POR','DEF','MED','DEL')),
                         rendimiento_base REAL NOT NULL DEFAULT 1.0,
                         id_equipo INTEGER NOT NULL,
                         UNIQUE (id_equipo, nombre),
                         FOREIGN KEY (id_equipo) REFERENCES equipo(id)
);

CREATE TABLE IF NOT EXISTS plantel (
                         id_equipo INTEGER NOT NULL,
                         id_jugador INTEGER NOT NULL,
                         rol_inicial TEXT NOT NULL CHECK(rol_inicial IN ('TITULAR','SUPLENTE')),
                         numero_dorsal INTEGER,
                         PRIMARY KEY (id_equipo, id_jugador),
                         FOREIGN KEY (id_equipo) REFERENCES equipo(id),
                         FOREIGN KEY (id_jugador) REFERENCES jugador(id)
);

CREATE TABLE IF NOT EXISTS torneo (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre TEXT NOT NULL UNIQUE,
                        fecha_inicio TEXT,
                        estado TEXT NOT NULL DEFAULT 'EN_CURSO'
                            CHECK(estado IN ('EN_CURSO','FINALIZADO')),
                        id_equipo_dt INTEGER,
                        id_campeon INTEGER,
                        FOREIGN KEY (id_equipo_dt) REFERENCES equipo(id),
                        FOREIGN KEY (id_campeon) REFERENCES equipo(id)
);

CREATE TABLE IF NOT EXISTS fase (
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      id_torneo INTEGER NOT NULL,
                      nombre TEXT NOT NULL,
                      orden INTEGER NOT NULL,
                      completada INTEGER NOT NULL DEFAULT 0,
                      UNIQUE (id_torneo, nombre),
                      FOREIGN KEY (id_torneo) REFERENCES torneo(id)
);

CREATE TABLE IF NOT EXISTS partido (
                         id INTEGER PRIMARY KEY AUTOINCREMENT,
                         id_fase INTEGER NOT NULL,
                         id_equipo_local INTEGER NOT NULL,
                         id_equipo_visit INTEGER NOT NULL,
                         goles_local INTEGER DEFAULT 0,
                         goles_visitante INTEGER DEFAULT 0,
                         id_ganador INTEGER,
                         estado TEXT NOT NULL DEFAULT 'NO_INICIADO'
                             CHECK(estado IN ('NO_INICIADO','PRIMER_TIEMPO',
                                              'ENTRETIEMPO','SEGUNDO_TIEMPO','FINALIZADO')),
                         id_tactica_local INTEGER,
                         id_tactica_visit INTEGER,
                         UNIQUE (id_fase, id_equipo_local, id_equipo_visit),
                         FOREIGN KEY (id_fase) REFERENCES fase(id),
                         FOREIGN KEY (id_equipo_local) REFERENCES equipo(id),
                         FOREIGN KEY (id_equipo_visit) REFERENCES equipo(id),
                         FOREIGN KEY (id_ganador) REFERENCES equipo(id),
                         FOREIGN KEY (id_tactica_local) REFERENCES tactica(id),
                         FOREIGN KEY (id_tactica_visit) REFERENCES tactica(id)
);

CREATE TABLE IF NOT EXISTS evento_partido (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                id_partido INTEGER NOT NULL,
                                tipo TEXT NOT NULL CHECK(tipo IN ('GOL','TARJETA','CAMBIO','LESION')),
                                minuto INTEGER NOT NULL,
                                id_jugador_principal INTEGER,
                                id_jugador_secundario INTEGER,
                                id_equipo INTEGER NOT NULL,
                                descripcion TEXT,
                                FOREIGN KEY (id_partido) REFERENCES partido(id),
                                FOREIGN KEY (id_jugador_principal) REFERENCES jugador(id),
                                FOREIGN KEY (id_jugador_secundario) REFERENCES jugador(id),
                                FOREIGN KEY (id_equipo) REFERENCES equipo(id)
);

CREATE TABLE IF NOT EXISTS estadistica_jugador (
                                     id INTEGER PRIMARY KEY AUTOINCREMENT,
                                     id_partido INTEGER NOT NULL,
                                     id_jugador INTEGER NOT NULL,
                                     id_equipo INTEGER NOT NULL,
                                     goles INTEGER NOT NULL DEFAULT 0,
                                     asistencias INTEGER NOT NULL DEFAULT 0,
                                     tarjetas_amarillas INTEGER NOT NULL DEFAULT 0,
                                     tarjeta_roja INTEGER NOT NULL DEFAULT 0,
                                     lesionado INTEGER NOT NULL DEFAULT 0,
                                     minutos_jugados INTEGER NOT NULL DEFAULT 0,
                                     rendimiento_final REAL,
                                     UNIQUE (id_partido, id_jugador),
                                     FOREIGN KEY (id_partido) REFERENCES partido(id),
                                     FOREIGN KEY (id_jugador) REFERENCES jugador(id),
                                     FOREIGN KEY (id_equipo) REFERENCES equipo(id)
);

CREATE TABLE IF NOT EXISTS estado_jugador_torneo (
                                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                                       id_torneo INTEGER NOT NULL,
                                       id_jugador INTEGER NOT NULL,
                                       cansancio REAL NOT NULL DEFAULT 0.0,
                                       lesionado INTEGER NOT NULL DEFAULT 0,
                                       tarjetas_amarillas_acum INTEGER NOT NULL DEFAULT 0,
                                       suspendido INTEGER NOT NULL DEFAULT 0,
                                       UNIQUE (id_torneo, id_jugador),
                                       FOREIGN KEY (id_torneo) REFERENCES torneo(id),
                                       FOREIGN KEY (id_jugador) REFERENCES jugador(id)
);

INSERT OR IGNORE INTO tactica (nombre, formacion, mod_ataque, mod_defensa)
VALUES
    ('Defensiva', '5-4-1', 0.85, 1.2),
    ('Equilibrada', '4-4-2', 1.0, 1.0),
    ('Ofensiva', '4-3-3', 1.2, 0.85);

CREATE INDEX IF NOT EXISTS idx_equipo_id_tactica ON equipo(id_tactica);
CREATE INDEX IF NOT EXISTS idx_jugador_id_equipo ON jugador(id_equipo);
CREATE INDEX IF NOT EXISTS idx_fase_id_torneo ON fase(id_torneo);
CREATE INDEX IF NOT EXISTS idx_partido_id_fase ON partido(id_fase);
CREATE INDEX IF NOT EXISTS idx_evento_partido_id_partido ON evento_partido(id_partido);
CREATE INDEX IF NOT EXISTS idx_estadistica_jugador_id_partido ON estadistica_jugador(id_partido);
CREATE INDEX IF NOT EXISTS idx_estado_jugador_torneo_id_torneo ON estado_jugador_torneo(id_torneo);
