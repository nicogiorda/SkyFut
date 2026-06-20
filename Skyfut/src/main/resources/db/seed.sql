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
    ('Real Madrid', 'Ofensiva', 0.02),
    ('Manchester City', 'Ofensiva', 0.02),
    ('Barcelona', 'Ofensiva', 0.02),
    ('Bayern Munich', 'Equilibrada', 0.02),
    ('Paris Saint-Germain', 'Ofensiva', 0.02),
    ('Liverpool', 'Ofensiva', 0.02),
    ('Inter Milan', 'Equilibrada', 0.01),
    ('Arsenal', 'Equilibrada', 0.02),
    ('Atletico Madrid', 'Defensiva', 0.01),
    ('Argentinos Juniors', 'Equilibrada', 0.01),
    ('Juventus', 'Defensiva', 0.01),
    ('AC Milan', 'Equilibrada', 0.01),
    ('River Plate', 'Ofensiva', 0.01),
    ('Chelsea', 'Equilibrada', 0.01),
    ('Napoli', 'Ofensiva', 0.01),
    ('Boca Juniors', 'Equilibrada', 0.01);

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
VALUES
    ('Real Madrid', 1, 'Thibaut Courtois', 'POR', 'TITULAR', 0.87),
    ('Real Madrid', 12, 'Trent Alexander-Arnold', 'DEF', 'TITULAR', 0.85),
    ('Real Madrid', 3, 'Eder Militao', 'DEF', 'TITULAR', 0.85),
    ('Real Madrid', 22, 'Antonio Rudiger', 'DEF', 'TITULAR', 0.85),
    ('Real Madrid', 18, 'Alvaro Carreras', 'DEF', 'TITULAR', 0.83),
    ('Real Madrid', 8, 'Federico Valverde', 'MED', 'TITULAR', 0.88),
    ('Real Madrid', 14, 'Aurelien Tchouameni', 'MED', 'TITULAR', 0.86),
    ('Real Madrid', 5, 'Jude Bellingham', 'MED', 'TITULAR', 0.89),
    ('Real Madrid', 7, 'Vinicius Junior', 'DEL', 'TITULAR', 0.89),
    ('Real Madrid', 10, 'Kylian Mbappe', 'DEL', 'TITULAR', 0.90),
    ('Real Madrid', 11, 'Rodrygo', 'DEL', 'TITULAR', 0.86),
    ('Real Madrid', 13, 'Andriy Lunin', 'POR', 'SUPLENTE', 0.82),
    ('Real Madrid', 2, 'Dani Carvajal', 'DEF', 'SUPLENTE', 0.82),
    ('Real Madrid', 24, 'Dean Huijsen', 'DEF', 'SUPLENTE', 0.84),
    ('Real Madrid', 6, 'Eduardo Camavinga', 'MED', 'SUPLENTE', 0.85),
    ('Real Madrid', 15, 'Arda Guler', 'MED', 'SUPLENTE', 0.85),
    ('Real Madrid', 21, 'Brahim Diaz', 'DEL', 'SUPLENTE', 0.83),
    ('Real Madrid', 16, 'Endrick', 'DEL', 'SUPLENTE', 0.82),

    ('Manchester City', 25, 'Gianluigi Donnarumma', 'POR', 'TITULAR', 0.87),
    ('Manchester City', 27, 'Matheus Nunes', 'DEF', 'TITULAR', 0.82),
    ('Manchester City', 3, 'Ruben Dias', 'DEF', 'TITULAR', 0.86),
    ('Manchester City', 24, 'Josko Gvardiol', 'DEF', 'TITULAR', 0.86),
    ('Manchester City', 21, 'Rayan Ait-Nouri', 'DEF', 'TITULAR', 0.83),
    ('Manchester City', 16, 'Rodri', 'MED', 'TITULAR', 0.89),
    ('Manchester City', 4, 'Tijjani Reijnders', 'MED', 'TITULAR', 0.86),
    ('Manchester City', 20, 'Bernardo Silva', 'MED', 'TITULAR', 0.87),
    ('Manchester City', 47, 'Phil Foden', 'DEL', 'TITULAR', 0.87),
    ('Manchester City', 9, 'Erling Haaland', 'DEL', 'TITULAR', 0.90),
    ('Manchester City', 11, 'Jeremy Doku', 'DEL', 'TITULAR', 0.85),
    ('Manchester City', 1, 'James Trafford', 'POR', 'SUPLENTE', 0.80),
    ('Manchester City', 5, 'John Stones', 'DEF', 'SUPLENTE', 0.82),
    ('Manchester City', 6, 'Nathan Ake', 'DEF', 'SUPLENTE', 0.82),
    ('Manchester City', 10, 'Rayan Cherki', 'MED', 'SUPLENTE', 0.84),
    ('Manchester City', 14, 'Nico Gonzalez', 'MED', 'SUPLENTE', 0.82),
    ('Manchester City', 7, 'Omar Marmoush', 'DEL', 'SUPLENTE', 0.85),
    ('Manchester City', 26, 'Savinho', 'DEL', 'SUPLENTE', 0.83),

    ('Barcelona', 13, 'Joan Garcia', 'POR', 'TITULAR', 0.85),
    ('Barcelona', 23, 'Jules Kounde', 'DEF', 'TITULAR', 0.85),
    ('Barcelona', 2, 'Pau Cubarsi', 'DEF', 'TITULAR', 0.85),
    ('Barcelona', 4, 'Ronald Araujo', 'DEF', 'TITULAR', 0.85),
    ('Barcelona', 3, 'Alejandro Balde', 'DEF', 'TITULAR', 0.84),
    ('Barcelona', 8, 'Pedri', 'MED', 'TITULAR', 0.89),
    ('Barcelona', 21, 'Frenkie de Jong', 'MED', 'TITULAR', 0.87),
    ('Barcelona', 20, 'Dani Olmo', 'MED', 'TITULAR', 0.86),
    ('Barcelona', 10, 'Lamine Yamal', 'DEL', 'TITULAR', 0.90),
    ('Barcelona', 9, 'Robert Lewandowski', 'DEL', 'TITULAR', 0.88),
    ('Barcelona', 11, 'Raphinha', 'DEL', 'TITULAR', 0.89),
    ('Barcelona', 25, 'Wojciech Szczesny', 'POR', 'SUPLENTE', 0.81),
    ('Barcelona', 24, 'Eric Garcia', 'DEF', 'SUPLENTE', 0.82),
    ('Barcelona', 15, 'Andreas Christensen', 'DEF', 'SUPLENTE', 0.81),
    ('Barcelona', 6, 'Gavi', 'MED', 'SUPLENTE', 0.84),
    ('Barcelona', 16, 'Fermin Lopez', 'MED', 'SUPLENTE', 0.84),
    ('Barcelona', 7, 'Ferran Torres', 'DEL', 'SUPLENTE', 0.83),
    ('Barcelona', 14, 'Marcus Rashford', 'DEL', 'SUPLENTE', 0.84),

    ('Bayern Munich', 1, 'Manuel Neuer', 'POR', 'TITULAR', 0.85),
    ('Bayern Munich', 27, 'Konrad Laimer', 'DEF', 'TITULAR', 0.83),
    ('Bayern Munich', 2, 'Dayot Upamecano', 'DEF', 'TITULAR', 0.85),
    ('Bayern Munich', 4, 'Jonathan Tah', 'DEF', 'TITULAR', 0.85),
    ('Bayern Munich', 19, 'Alphonso Davies', 'DEF', 'TITULAR', 0.86),
    ('Bayern Munich', 6, 'Joshua Kimmich', 'MED', 'TITULAR', 0.88),
    ('Bayern Munich', 8, 'Leon Goretzka', 'MED', 'TITULAR', 0.84),
    ('Bayern Munich', 10, 'Jamal Musiala', 'MED', 'TITULAR', 0.89),
    ('Bayern Munich', 17, 'Michael Olise', 'DEL', 'TITULAR', 0.88),
    ('Bayern Munich', 9, 'Harry Kane', 'DEL', 'TITULAR', 0.90),
    ('Bayern Munich', 14, 'Luis Diaz', 'DEL', 'TITULAR', 0.87),
    ('Bayern Munich', 40, 'Jonas Urbig', 'POR', 'SUPLENTE', 0.79),
    ('Bayern Munich', 3, 'Kim Min-jae', 'DEF', 'SUPLENTE', 0.82),
    ('Bayern Munich', 22, 'Raphael Guerreiro', 'DEF', 'SUPLENTE', 0.81),
    ('Bayern Munich', 45, 'Aleksandar Pavlovic', 'MED', 'SUPLENTE', 0.83),
    ('Bayern Munich', 20, 'Tom Bischof', 'MED', 'SUPLENTE', 0.80),
    ('Bayern Munich', 7, 'Serge Gnabry', 'DEL', 'SUPLENTE', 0.83),
    ('Bayern Munich', 42, 'Lennart Karl', 'DEL', 'SUPLENTE', 0.80),

    ('Paris Saint-Germain', 30, 'Lucas Chevalier', 'POR', 'TITULAR', 0.85),
    ('Paris Saint-Germain', 2, 'Achraf Hakimi', 'DEF', 'TITULAR', 0.87),
    ('Paris Saint-Germain', 5, 'Marquinhos', 'DEF', 'TITULAR', 0.85),
    ('Paris Saint-Germain', 51, 'Willian Pacho', 'DEF', 'TITULAR', 0.86),
    ('Paris Saint-Germain', 25, 'Nuno Mendes', 'DEF', 'TITULAR', 0.87),
    ('Paris Saint-Germain', 17, 'Vitinha', 'MED', 'TITULAR', 0.89),
    ('Paris Saint-Germain', 87, 'Joao Neves', 'MED', 'TITULAR', 0.88),
    ('Paris Saint-Germain', 8, 'Fabian Ruiz', 'MED', 'TITULAR', 0.86),
    ('Paris Saint-Germain', 14, 'Desire Doue', 'DEL', 'TITULAR', 0.88),
    ('Paris Saint-Germain', 10, 'Ousmane Dembele', 'DEL', 'TITULAR', 0.90),
    ('Paris Saint-Germain', 7, 'Khvicha Kvaratskhelia', 'DEL', 'TITULAR', 0.89),
    ('Paris Saint-Germain', 39, 'Matvey Safonov', 'POR', 'SUPLENTE', 0.80),
    ('Paris Saint-Germain', 21, 'Lucas Hernandez', 'DEF', 'SUPLENTE', 0.82),
    ('Paris Saint-Germain', 35, 'Lucas Beraldo', 'DEF', 'SUPLENTE', 0.81),
    ('Paris Saint-Germain', 33, 'Warren Zaire-Emery', 'MED', 'SUPLENTE', 0.84),
    ('Paris Saint-Germain', 19, 'Lee Kang-in', 'MED', 'SUPLENTE', 0.83),
    ('Paris Saint-Germain', 29, 'Bradley Barcola', 'DEL', 'SUPLENTE', 0.85),
    ('Paris Saint-Germain', 9, 'Goncalo Ramos', 'DEL', 'SUPLENTE', 0.83),

    ('Liverpool', 1, 'Alisson Becker', 'POR', 'TITULAR', 0.87),
    ('Liverpool', 30, 'Jeremie Frimpong', 'DEF', 'TITULAR', 0.84),
    ('Liverpool', 5, 'Ibrahima Konate', 'DEF', 'TITULAR', 0.85),
    ('Liverpool', 4, 'Virgil van Dijk', 'DEF', 'TITULAR', 0.87),
    ('Liverpool', 6, 'Milos Kerkez', 'DEF', 'TITULAR', 0.83),
    ('Liverpool', 10, 'Alexis Mac Allister', 'MED', 'TITULAR', 0.87),
    ('Liverpool', 38, 'Ryan Gravenberch', 'MED', 'TITULAR', 0.86),
    ('Liverpool', 8, 'Dominik Szoboszlai', 'MED', 'TITULAR', 0.86),
    ('Liverpool', 11, 'Mohamed Salah', 'DEL', 'TITULAR', 0.89),
    ('Liverpool', 9, 'Alexander Isak', 'DEL', 'TITULAR', 0.89),
    ('Liverpool', 7, 'Florian Wirtz', 'DEL', 'TITULAR', 0.88),
    ('Liverpool', 25, 'Giorgi Mamardashvili', 'POR', 'SUPLENTE', 0.82),
    ('Liverpool', 12, 'Conor Bradley', 'DEF', 'SUPLENTE', 0.81),
    ('Liverpool', 2, 'Joe Gomez', 'DEF', 'SUPLENTE', 0.81),
    ('Liverpool', 17, 'Curtis Jones', 'MED', 'SUPLENTE', 0.82),
    ('Liverpool', 3, 'Wataru Endo', 'MED', 'SUPLENTE', 0.80),
    ('Liverpool', 18, 'Cody Gakpo', 'DEL', 'SUPLENTE', 0.84),
    ('Liverpool', 22, 'Hugo Ekitike', 'DEL', 'SUPLENTE', 0.84),

    ('Inter Milan', 1, 'Yann Sommer', 'POR', 'TITULAR', 0.84),
    ('Inter Milan', 2, 'Denzel Dumfries', 'DEF', 'TITULAR', 0.84),
    ('Inter Milan', 25, 'Manuel Akanji', 'DEF', 'TITULAR', 0.84),
    ('Inter Milan', 95, 'Alessandro Bastoni', 'DEF', 'TITULAR', 0.86),
    ('Inter Milan', 32, 'Federico Dimarco', 'DEF', 'TITULAR', 0.86),
    ('Inter Milan', 23, 'Nicolo Barella', 'MED', 'TITULAR', 0.87),
    ('Inter Milan', 20, 'Hakan Calhanoglu', 'MED', 'TITULAR', 0.86),
    ('Inter Milan', 22, 'Henrikh Mkhitaryan', 'MED', 'TITULAR', 0.83),
    ('Inter Milan', 10, 'Lautaro Martinez', 'DEL', 'TITULAR', 0.89),
    ('Inter Milan', 9, 'Marcus Thuram', 'DEL', 'TITULAR', 0.87),
    ('Inter Milan', 14, 'Ange-Yoan Bonny', 'DEL', 'TITULAR', 0.82),
    ('Inter Milan', 13, 'Josep Martinez', 'POR', 'SUPLENTE', 0.79),
    ('Inter Milan', 6, 'Stefan de Vrij', 'DEF', 'SUPLENTE', 0.82),
    ('Inter Milan', 31, 'Yann Bisseck', 'DEF', 'SUPLENTE', 0.81),
    ('Inter Milan', 7, 'Piotr Zielinski', 'MED', 'SUPLENTE', 0.82),
    ('Inter Milan', 16, 'Davide Frattesi', 'MED', 'SUPLENTE', 0.83),
    ('Inter Milan', 94, 'Francesco Pio Esposito', 'DEL', 'SUPLENTE', 0.81),
    ('Inter Milan', 11, 'Luis Henrique', 'DEL', 'SUPLENTE', 0.81),

    ('Arsenal', 1, 'David Raya', 'POR', 'TITULAR', 0.86),
    ('Arsenal', 12, 'Jurrien Timber', 'DEF', 'TITULAR', 0.85),
    ('Arsenal', 2, 'William Saliba', 'DEF', 'TITULAR', 0.87),
    ('Arsenal', 6, 'Gabriel Magalhaes', 'DEF', 'TITULAR', 0.87),
    ('Arsenal', 33, 'Riccardo Calafiori', 'DEF', 'TITULAR', 0.84),
    ('Arsenal', 41, 'Declan Rice', 'MED', 'TITULAR', 0.88),
    ('Arsenal', 8, 'Martin Odegaard', 'MED', 'TITULAR', 0.87),
    ('Arsenal', 36, 'Martin Zubimendi', 'MED', 'TITULAR', 0.86),
    ('Arsenal', 7, 'Bukayo Saka', 'DEL', 'TITULAR', 0.89),
    ('Arsenal', 14, 'Viktor Gyokeres', 'DEL', 'TITULAR', 0.88),
    ('Arsenal', 10, 'Eberechi Eze', 'DEL', 'TITULAR', 0.86),
    ('Arsenal', 13, 'Kepa Arrizabalaga', 'POR', 'SUPLENTE', 0.80),
    ('Arsenal', 4, 'Ben White', 'DEF', 'SUPLENTE', 0.82),
    ('Arsenal', 3, 'Cristhian Mosquera', 'DEF', 'SUPLENTE', 0.80),
    ('Arsenal', 23, 'Mikel Merino', 'MED', 'SUPLENTE', 0.84),
    ('Arsenal', 16, 'Christian Norgaard', 'MED', 'SUPLENTE', 0.80),
    ('Arsenal', 11, 'Gabriel Martinelli', 'DEL', 'SUPLENTE', 0.84),
    ('Arsenal', 20, 'Noni Madueke', 'DEL', 'SUPLENTE', 0.83),

    ('Atletico Madrid', 13, 'Jan Oblak', 'POR', 'TITULAR', 0.87),
    ('Atletico Madrid', 14, 'Marcos Llorente', 'DEF', 'TITULAR', 0.84),
    ('Atletico Madrid', 2, 'Jose Maria Gimenez', 'DEF', 'TITULAR', 0.84),
    ('Atletico Madrid', 24, 'Robin Le Normand', 'DEF', 'TITULAR', 0.84),
    ('Atletico Madrid', 17, 'David Hancko', 'DEF', 'TITULAR', 0.85),
    ('Atletico Madrid', 6, 'Koke', 'MED', 'TITULAR', 0.84),
    ('Atletico Madrid', 8, 'Pablo Barrios', 'MED', 'TITULAR', 0.84),
    ('Atletico Madrid', 4, 'Conor Gallagher', 'MED', 'TITULAR', 0.83),
    ('Atletico Madrid', 19, 'Julian Alvarez', 'DEL', 'TITULAR', 0.89),
    ('Atletico Madrid', 7, 'Antoine Griezmann', 'DEL', 'TITULAR', 0.87),
    ('Atletico Madrid', 9, 'Alexander Sorloth', 'DEL', 'TITULAR', 0.85),
    ('Atletico Madrid', 1, 'Juan Musso', 'POR', 'SUPLENTE', 0.79),
    ('Atletico Madrid', 16, 'Nahuel Molina', 'DEF', 'SUPLENTE', 0.81),
    ('Atletico Madrid', 3, 'Matteo Ruggeri', 'DEF', 'SUPLENTE', 0.80),
    ('Atletico Madrid', 5, 'Johnny Cardoso', 'MED', 'SUPLENTE', 0.82),
    ('Atletico Madrid', 10, 'Alex Baena', 'MED', 'SUPLENTE', 0.84),
    ('Atletico Madrid', 20, 'Giuliano Simeone', 'DEL', 'SUPLENTE', 0.82),
    ('Atletico Madrid', 18, 'Giacomo Raspadori', 'DEL', 'SUPLENTE', 0.82),

    ('Juventus', 29, 'Michele Di Gregorio', 'POR', 'TITULAR', 0.84),
    ('Juventus', 15, 'Pierre Kalulu', 'DEF', 'TITULAR', 0.83),
    ('Juventus', 3, 'Bremer', 'DEF', 'TITULAR', 0.86),
    ('Juventus', 4, 'Federico Gatti', 'DEF', 'TITULAR', 0.82),
    ('Juventus', 27, 'Andrea Cambiaso', 'DEF', 'TITULAR', 0.84),
    ('Juventus', 5, 'Manuel Locatelli', 'MED', 'TITULAR', 0.85),
    ('Juventus', 19, 'Khephren Thuram', 'MED', 'TITULAR', 0.85),
    ('Juventus', 8, 'Teun Koopmeiners', 'MED', 'TITULAR', 0.84),
    ('Juventus', 7, 'Francisco Conceicao', 'DEL', 'TITULAR', 0.84),
    ('Juventus', 30, 'Jonathan David', 'DEL', 'TITULAR', 0.86),
    ('Juventus', 10, 'Kenan Yildiz', 'DEL', 'TITULAR', 0.87),
    ('Juventus', 1, 'Mattia Perin', 'POR', 'SUPLENTE', 0.79),
    ('Juventus', 24, 'Daniele Rugani', 'DEF', 'SUPLENTE', 0.79),
    ('Juventus', 32, 'Juan Cabal', 'DEF', 'SUPLENTE', 0.79),
    ('Juventus', 16, 'Weston McKennie', 'MED', 'SUPLENTE', 0.82),
    ('Juventus', 21, 'Fabio Miretti', 'MED', 'SUPLENTE', 0.80),
    ('Juventus', 9, 'Dusan Vlahovic', 'DEL', 'SUPLENTE', 0.84),
    ('Juventus', 11, 'Lois Openda', 'DEL', 'SUPLENTE', 0.84),

    ('AC Milan', 16, 'Mike Maignan', 'POR', 'TITULAR', 0.87),
    ('AC Milan', 56, 'Alexis Saelemaekers', 'DEF', 'TITULAR', 0.82),
    ('AC Milan', 23, 'Fikayo Tomori', 'DEF', 'TITULAR', 0.83),
    ('AC Milan', 46, 'Matteo Gabbia', 'DEF', 'TITULAR', 0.82),
    ('AC Milan', 2, 'Pervis Estupinan', 'DEF', 'TITULAR', 0.83),
    ('AC Milan', 14, 'Luka Modric', 'MED', 'TITULAR', 0.87),
    ('AC Milan', 4, 'Samuele Ricci', 'MED', 'TITULAR', 0.83),
    ('AC Milan', 12, 'Adrien Rabiot', 'MED', 'TITULAR', 0.85),
    ('AC Milan', 11, 'Christian Pulisic', 'DEL', 'TITULAR', 0.87),
    ('AC Milan', 10, 'Rafael Leao', 'DEL', 'TITULAR', 0.88),
    ('AC Milan', 7, 'Santiago Gimenez', 'DEL', 'TITULAR', 0.85),
    ('AC Milan', 1, 'Pietro Terracciano', 'POR', 'SUPLENTE', 0.78),
    ('AC Milan', 5, 'Koni De Winter', 'DEF', 'SUPLENTE', 0.80),
    ('AC Milan', 31, 'Strahinja Pavlovic', 'DEF', 'SUPLENTE', 0.81),
    ('AC Milan', 19, 'Youssouf Fofana', 'MED', 'SUPLENTE', 0.83),
    ('AC Milan', 8, 'Ruben Loftus-Cheek', 'MED', 'SUPLENTE', 0.81),
    ('AC Milan', 18, 'Christopher Nkunku', 'DEL', 'SUPLENTE', 0.84),
    ('AC Milan', 9, 'Niclas Fullkrug', 'DEL', 'SUPLENTE', 0.81),

    ('Chelsea', 1, 'Robert Sanchez', 'POR', 'TITULAR', 0.83),
    ('Chelsea', 27, 'Malo Gusto', 'DEF', 'TITULAR', 0.82),
    ('Chelsea', 29, 'Wesley Fofana', 'DEF', 'TITULAR', 0.83),
    ('Chelsea', 6, 'Levi Colwill', 'DEF', 'TITULAR', 0.84),
    ('Chelsea', 3, 'Marc Cucurella', 'DEF', 'TITULAR', 0.85),
    ('Chelsea', 25, 'Moises Caicedo', 'MED', 'TITULAR', 0.88),
    ('Chelsea', 8, 'Enzo Fernandez', 'MED', 'TITULAR', 0.87),
    ('Chelsea', 10, 'Cole Palmer', 'MED', 'TITULAR', 0.89),
    ('Chelsea', 7, 'Pedro Neto', 'DEL', 'TITULAR', 0.85),
    ('Chelsea', 20, 'Joao Pedro', 'DEL', 'TITULAR', 0.86),
    ('Chelsea', 41, 'Estevao', 'DEL', 'TITULAR', 0.85),
    ('Chelsea', 12, 'Filip Jorgensen', 'POR', 'SUPLENTE', 0.79),
    ('Chelsea', 23, 'Trevoh Chalobah', 'DEF', 'SUPLENTE', 0.82),
    ('Chelsea', 4, 'Tosin Adarabioyo', 'DEF', 'SUPLENTE', 0.81),
    ('Chelsea', 45, 'Romeo Lavia', 'MED', 'SUPLENTE', 0.81),
    ('Chelsea', 17, 'Andrey Santos', 'MED', 'SUPLENTE', 0.82),
    ('Chelsea', 9, 'Liam Delap', 'DEL', 'SUPLENTE', 0.82),
    ('Chelsea', 49, 'Alejandro Garnacho', 'DEL', 'SUPLENTE', 0.84),

    ('Napoli', 1, 'Alex Meret', 'POR', 'TITULAR', 0.83),
    ('Napoli', 22, 'Giovanni Di Lorenzo', 'DEF', 'TITULAR', 0.85),
    ('Napoli', 13, 'Amir Rrahmani', 'DEF', 'TITULAR', 0.84),
    ('Napoli', 4, 'Alessandro Buongiorno', 'DEF', 'TITULAR', 0.85),
    ('Napoli', 37, 'Leonardo Spinazzola', 'DEF', 'TITULAR', 0.82),
    ('Napoli', 68, 'Stanislav Lobotka', 'MED', 'TITULAR', 0.86),
    ('Napoli', 99, 'Andre-Frank Zambo Anguissa', 'MED', 'TITULAR', 0.86),
    ('Napoli', 11, 'Kevin De Bruyne', 'MED', 'TITULAR', 0.88),
    ('Napoli', 21, 'Matteo Politano', 'DEL', 'TITULAR', 0.84),
    ('Napoli', 19, 'Rasmus Hojlund', 'DEL', 'TITULAR', 0.85),
    ('Napoli', 7, 'David Neres', 'DEL', 'TITULAR', 0.85),
    ('Napoli', 32, 'Vanja Milinkovic-Savic', 'POR', 'SUPLENTE', 0.81),
    ('Napoli', 5, 'Juan Jesus', 'DEF', 'SUPLENTE', 0.79),
    ('Napoli', 31, 'Sam Beukema', 'DEF', 'SUPLENTE', 0.81),
    ('Napoli', 6, 'Billy Gilmour', 'MED', 'SUPLENTE', 0.81),
    ('Napoli', 8, 'Scott McTominay', 'MED', 'SUPLENTE', 0.85),
    ('Napoli', 70, 'Noa Lang', 'DEL', 'SUPLENTE', 0.82),
    ('Napoli', 27, 'Lorenzo Lucca', 'DEL', 'SUPLENTE', 0.81),

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

    ('Argentinos Juniors', 25, 'Brayan Cortes', 'POR', 'TITULAR', 0.84),
    ('Argentinos Juniors', 3, 'Luciano Sanchez', 'DEF', 'TITULAR', 0.82),
    ('Argentinos Juniors', 4, 'Erik Godoy', 'DEF', 'TITULAR', 0.83),
    ('Argentinos Juniors', 16, 'Francisco Alvarez', 'DEF', 'TITULAR', 0.82),
    ('Argentinos Juniors', 17, 'Franco Paredes', 'DEF', 'TITULAR', 0.81),
    ('Argentinos Juniors', 10, 'Alan Lescano', 'MED', 'TITULAR', 0.86),
    ('Argentinos Juniors', 23, 'Hernan Lopez Munoz', 'MED', 'TITULAR', 0.87),
    ('Argentinos Juniors', 21, 'Gabriel Florentin', 'MED', 'TITULAR', 0.84),
    ('Argentinos Juniors', 9, 'Gaston Veron', 'DEL', 'TITULAR', 0.84),
    ('Argentinos Juniors', 18, 'Leandro Fernandez', 'DEL', 'TITULAR', 0.85),
    ('Argentinos Juniors', 34, 'Matias Gimenez Rojas', 'DEL', 'TITULAR', 0.85),
    ('Argentinos Juniors', 1, 'Agustin Mangiaut', 'POR', 'SUPLENTE', 0.79),
    ('Argentinos Juniors', 6, 'Roman Riquelme', 'DEF', 'SUPLENTE', 0.79),
    ('Argentinos Juniors', 26, 'Claudio Bravo', 'DEF', 'SUPLENTE', 0.80),
    ('Argentinos Juniors', 11, 'Nicolas Oroz', 'MED', 'SUPLENTE', 0.82),
    ('Argentinos Juniors', 32, 'Gino Infantino', 'MED', 'SUPLENTE', 0.83),
    ('Argentinos Juniors', 27, 'Tomas Molina', 'DEL', 'SUPLENTE', 0.82),
    ('Argentinos Juniors', 30, 'Ivan Morales', 'DEL', 'SUPLENTE', 0.81);

UPDATE jugador
SET posicion = (
        SELECT sj.posicion
        FROM seed_jugador sj
        JOIN equipo e ON e.nombre = sj.equipo
        WHERE e.id = jugador.id_equipo
          AND sj.nombre = jugador.nombre
    ),
    rendimiento_base = (
        SELECT ROUND(sj.rendimiento_base + se.bonus, 2)
        FROM seed_jugador sj
        JOIN seed_equipo se ON se.nombre = sj.equipo
        JOIN equipo e ON e.nombre = sj.equipo
        WHERE e.id = jugador.id_equipo
          AND sj.nombre = jugador.nombre
    )
WHERE EXISTS (
    SELECT 1
    FROM seed_jugador sj
    JOIN equipo e ON e.nombre = sj.equipo
    WHERE e.id = jugador.id_equipo
      AND sj.nombre = jugador.nombre
);

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
DROP TABLE seed_equipo;
