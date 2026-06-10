-- Datos iniciales para probar un torneo desde octavos de final.
-- Clubes reales, planteles ficticios verosimiles y ratings balanceados.

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
    ('Borussia Dortmund', 'Equilibrada', 0.02),
    ('Juventus', 'Defensiva', 0.02),
    ('AC Milan', 'Equilibrada', 0.02),
    ('Bayer Leverkusen', 'Ofensiva', 0.03),
    ('Chelsea', 'Equilibrada', 0.01),
    ('Napoli', 'Ofensiva', 0.01),
    ('Benfica', 'Equilibrada', 0.00);

INSERT OR IGNORE INTO equipo (nombre, id_tactica)
SELECT se.nombre, t.id
FROM seed_equipo se
JOIN tactica t ON t.nombre = se.tactica;

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

INSERT INTO jugador (nombre, posicion, rendimiento_base, id_equipo)
SELECT
    ss.nombre || ' - ' || se.nombre,
    ss.posicion,
    ROUND(ss.rendimiento_base + se.bonus, 2),
    e.id
FROM seed_equipo se
JOIN equipo e ON e.nombre = se.nombre
CROSS JOIN seed_slot ss
WHERE NOT EXISTS (
    SELECT 1
    FROM jugador existente
    WHERE existente.id_equipo = e.id
      AND existente.nombre = ss.nombre || ' - ' || se.nombre
);

INSERT OR IGNORE INTO plantel (id_equipo, id_jugador, rol_inicial, numero_dorsal)
SELECT
    e.id,
    j.id,
    ss.rol_inicial,
    ss.dorsal
FROM seed_equipo se
JOIN equipo e ON e.nombre = se.nombre
CROSS JOIN seed_slot ss
JOIN jugador j ON j.id_equipo = e.id
              AND j.nombre = ss.nombre || ' - ' || se.nombre;

DROP TABLE seed_slot;
DROP TABLE seed_equipo;
