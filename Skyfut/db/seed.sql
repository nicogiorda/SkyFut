-- Datos iniciales para probar un torneo desde octavos de final.
-- Clubes reales, planteles verosimiles y ratings balanceados.

CREATE TEMP TABLE IF NOT EXISTS seed_equipo (
    nombre TEXT NOT NULL,
    tactica TEXT NOT NULL,
    bonus REAL NOT NULL
);

DELETE FROM seed_equipo;

INSERT INTO seed_equipo (nombre, tactica, bonus)
VALUES
    ('Real Madrid', 'Ofensiva', 0.08),
    ('Manchester City', 'Ofensiva', 0.08),
    ('Barcelona', 'Ofensiva', 0.06),
    ('Bayern Munich', 'Equilibrada', 0.06),
    ('Paris Saint-Germain', 'Ofensiva', 0.05),
    ('Liverpool', 'Ofensiva', 0.05),
    ('Inter Milan', 'Equilibrada', 0.04),
    ('Arsenal', 'Equilibrada', 0.04),
    ('Atletico Madrid', 'Defensiva', 0.03),
    ('Argentinos Juniors', 'Equilibrada', 0.02),
    ('Juventus', 'Defensiva', 0.02),
    ('AC Milan', 'Equilibrada', 0.02),
    ('River Plate', 'Ofensiva', 0.03),
    ('Chelsea', 'Equilibrada', 0.01),
    ('Napoli', 'Ofensiva', 0.01),
    ('Boca Juniors', 'Equilibrada', 0.00);

UPDATE equipo
SET nombre = 'Boca Juniors'
WHERE nombre = 'Benfica'
  AND NOT EXISTS (SELECT 1 FROM equipo WHERE nombre = 'Boca Juniors');

UPDATE equipo
SET nombre = 'River Plate'
WHERE nombre IN ('Bayer Leverkusen', 'Bayern Leverkusen')
  AND NOT EXISTS (SELECT 1 FROM equipo WHERE nombre = 'River Plate');

UPDATE equipo
SET nombre = 'Argentinos Juniors'
WHERE nombre = 'Borussia Dortmund'
  AND NOT EXISTS (SELECT 1 FROM equipo WHERE nombre = 'Argentinos Juniors');

INSERT OR IGNORE INTO equipo (nombre, id_tactica)
SELECT se.nombre, t.id
FROM seed_equipo se
JOIN tactica t ON t.nombre = se.tactica;

DELETE FROM plantel
WHERE id_equipo IN (
    SELECT e.id
    FROM equipo e
    JOIN seed_equipo se ON se.nombre = e.nombre
);

CREATE TEMP TABLE IF NOT EXISTS seed_slot (
    dorsal INTEGER NOT NULL,
    nombre TEXT NOT NULL,
    posicion TEXT NOT NULL,
    rol_inicial TEXT NOT NULL,
    rendimiento_base REAL NOT NULL
);

DELETE FROM seed_slot;

INSERT INTO seed_slot (dorsal, nombre, posicion, rol_inicial, rendimiento_base)
VALUES
    (1, 'Diego Vargas', 'POR', 'TITULAR', 0.84),
    (2, 'Lucas Ferreyra', 'DEF', 'TITULAR', 0.78),
    (4, 'Nicolas Rivas', 'DEF', 'TITULAR', 0.80),
    (5, 'Mateo Duarte', 'DEF', 'TITULAR', 0.79),
    (3, 'Adrian Molina', 'DEF', 'TITULAR', 0.77),
    (8, 'Tomas Salcedo', 'MED', 'TITULAR', 0.82),
    (6, 'Bruno Herrera', 'MED', 'TITULAR', 0.80),
    (10, 'Emiliano Costa', 'MED', 'TITULAR', 0.86),
    (7, 'Santiago Luna', 'DEL', 'TITULAR', 0.84),
    (9, 'Martin Aguilar', 'DEL', 'TITULAR', 0.87),
    (11, 'Federico Blanco', 'DEL', 'TITULAR', 0.83),
    (12, 'Ivan Paredes', 'POR', 'SUPLENTE', 0.73),
    (13, 'Rafael Moreno', 'DEF', 'SUPLENTE', 0.72),
    (14, 'Julian Campos', 'DEF', 'SUPLENTE', 0.71),
    (15, 'Leandro Vega', 'MED', 'SUPLENTE', 0.74),
    (16, 'Matias Benitez', 'MED', 'SUPLENTE', 0.73),
    (17, 'Pablo Ortega', 'DEL', 'SUPLENTE', 0.75),
    (18, 'Franco Medina', 'DEL', 'SUPLENTE', 0.74);

CREATE TEMP TABLE IF NOT EXISTS seed_jugador (
    equipo TEXT NOT NULL,
    dorsal INTEGER NOT NULL,
    nombre TEXT NOT NULL,
    posicion TEXT NOT NULL,
    rol_inicial TEXT NOT NULL,
    rendimiento_base REAL NOT NULL
);

DELETE FROM seed_jugador;

INSERT INTO seed_jugador (equipo, dorsal, nombre, posicion, rol_inicial, rendimiento_base)
SELECT
    se.nombre,
    ss.dorsal,
    ss.nombre,
    ss.posicion,
    ss.rol_inicial,
    ss.rendimiento_base
FROM seed_equipo se
CROSS JOIN seed_slot ss
WHERE se.nombre NOT IN ('Boca Juniors', 'River Plate', 'Argentinos Juniors');

INSERT INTO seed_jugador (equipo, dorsal, nombre, posicion, rol_inicial, rendimiento_base)
VALUES
    ('Boca Juniors', 1, 'Agustin Marchesin', 'POR', 'TITULAR', 0.86),
    ('Boca Juniors', 2, 'Lautaro Di Lollo', 'DEF', 'TITULAR', 0.80),
    ('Boca Juniors', 3, 'Lautaro Blanco', 'DEF', 'TITULAR', 0.81),
    ('Boca Juniors', 4, 'Nicolas Figal', 'DEF', 'TITULAR', 0.82),
    ('Boca Juniors', 24, 'Juan Barinaga', 'DEF', 'TITULAR', 0.78),
    ('Boca Juniors', 5, 'Leandro Paredes', 'MED', 'TITULAR', 0.90),
    ('Boca Juniors', 8, 'Carlos Palacios', 'MED', 'TITULAR', 0.84),
    ('Boca Juniors', 22, 'Kevin Zenon', 'MED', 'TITULAR', 0.85),
    ('Boca Juniors', 10, 'Edinson Cavani', 'DEL', 'TITULAR', 0.88),
    ('Boca Juniors', 16, 'Miguel Merentiel', 'DEL', 'TITULAR', 0.86),
    ('Boca Juniors', 28, 'Adam Bareiro', 'DEL', 'TITULAR', 0.84),
    ('Boca Juniors', 12, 'Leandro Brey', 'POR', 'SUPLENTE', 0.80),
    ('Boca Juniors', 23, 'Marcelo Weigandt', 'DEF', 'SUPLENTE', 0.77),
    ('Boca Juniors', 26, 'Marco Pellegrino', 'DEF', 'SUPLENTE', 0.78),
    ('Boca Juniors', 25, 'Santiago Ascacibar', 'MED', 'SUPLENTE', 0.83),
    ('Boca Juniors', 18, 'Milton Delgado', 'MED', 'SUPLENTE', 0.79),
    ('Boca Juniors', 7, 'Exequiel Zeballos', 'DEL', 'SUPLENTE', 0.82),
    ('Boca Juniors', 9, 'Milton Gimenez', 'DEL', 'SUPLENTE', 0.81),

    ('River Plate', 1, 'Franco Armani', 'POR', 'TITULAR', 0.86),
    ('River Plate', 16, 'Fabricio Bustos', 'DEF', 'TITULAR', 0.82),
    ('River Plate', 17, 'Paulo Diaz', 'DEF', 'TITULAR', 0.84),
    ('River Plate', 20, 'German Pezzella', 'DEF', 'TITULAR', 0.85),
    ('River Plate', 21, 'Marcos Acuna', 'DEF', 'TITULAR', 0.84),
    ('River Plate', 6, 'Anibal Moreno', 'MED', 'TITULAR', 0.85),
    ('River Plate', 10, 'Juan Fernando Quintero', 'MED', 'TITULAR', 0.88),
    ('River Plate', 8, 'Maximiliano Meza', 'MED', 'TITULAR', 0.84),
    ('River Plate', 7, 'Maximiliano Salas', 'DEL', 'TITULAR', 0.84),
    ('River Plate', 9, 'Sebastian Driussi', 'DEL', 'TITULAR', 0.86),
    ('River Plate', 11, 'Facundo Colidio', 'DEL', 'TITULAR', 0.84),
    ('River Plate', 33, 'Ezequiel Centurion', 'POR', 'SUPLENTE', 0.78),
    ('River Plate', 28, 'Lucas Martinez Quarta', 'DEF', 'SUPLENTE', 0.83),
    ('River Plate', 29, 'Gonzalo Montiel', 'DEF', 'SUPLENTE', 0.83),
    ('River Plate', 22, 'Kevin Castano', 'MED', 'SUPLENTE', 0.82),
    ('River Plate', 34, 'Giuliano Galoppo', 'MED', 'SUPLENTE', 0.81),
    ('River Plate', 32, 'Agustin Ruberto', 'DEL', 'SUPLENTE', 0.79),
    ('River Plate', 38, 'Ian Subiabre', 'DEL', 'SUPLENTE', 0.78),

    ('Argentinos Juniors', 25, 'Brayan Cortes', 'POR', 'TITULAR', 0.82),
    ('Argentinos Juniors', 3, 'Luciano Sanchez', 'DEF', 'TITULAR', 0.78),
    ('Argentinos Juniors', 4, 'Erik Godoy', 'DEF', 'TITULAR', 0.79),
    ('Argentinos Juniors', 16, 'Francisco Alvarez', 'DEF', 'TITULAR', 0.78),
    ('Argentinos Juniors', 17, 'Franco Paredes', 'DEF', 'TITULAR', 0.77),
    ('Argentinos Juniors', 10, 'Alan Lescano', 'MED', 'TITULAR', 0.83),
    ('Argentinos Juniors', 23, 'Hernan Lopez Munoz', 'MED', 'TITULAR', 0.84),
    ('Argentinos Juniors', 21, 'Gabriel Florentin', 'MED', 'TITULAR', 0.80),
    ('Argentinos Juniors', 9, 'Gaston Veron', 'DEL', 'TITULAR', 0.80),
    ('Argentinos Juniors', 18, 'Leandro Fernandez', 'DEL', 'TITULAR', 0.81),
    ('Argentinos Juniors', 34, 'Matias Gimenez Rojas', 'DEL', 'TITULAR', 0.81),
    ('Argentinos Juniors', 1, 'Agustin Mangiaut', 'POR', 'SUPLENTE', 0.75),
    ('Argentinos Juniors', 6, 'Roman Riquelme', 'DEF', 'SUPLENTE', 0.75),
    ('Argentinos Juniors', 26, 'Claudio Bravo', 'DEF', 'SUPLENTE', 0.76),
    ('Argentinos Juniors', 11, 'Nicolas Oroz', 'MED', 'SUPLENTE', 0.78),
    ('Argentinos Juniors', 32, 'Gino Infantino', 'MED', 'SUPLENTE', 0.79),
    ('Argentinos Juniors', 27, 'Tomas Molina', 'DEL', 'SUPLENTE', 0.78),
    ('Argentinos Juniors', 30, 'Ivan Morales', 'DEL', 'SUPLENTE', 0.77);

INSERT INTO jugador (nombre, posicion, rendimiento_base, id_equipo)
SELECT
    sj.nombre,
    sj.posicion,
    ROUND(sj.rendimiento_base + se.bonus, 2),
    e.id
FROM seed_jugador sj
JOIN seed_equipo se ON se.nombre = sj.equipo
JOIN equipo e ON e.nombre = sj.equipo
WHERE NOT EXISTS (
    SELECT 1
    FROM jugador existente
    WHERE existente.id_equipo = e.id
      AND existente.nombre = sj.nombre
);

INSERT OR IGNORE INTO plantel (id_equipo, id_jugador, rol_inicial, numero_dorsal)
SELECT
    e.id,
    j.id,
    sj.rol_inicial,
    sj.dorsal
FROM seed_jugador sj
JOIN equipo e ON e.nombre = sj.equipo
JOIN jugador j ON j.id_equipo = e.id
              AND j.nombre = sj.nombre;

DROP TABLE seed_jugador;
DROP TABLE seed_slot;
DROP TABLE seed_equipo;
