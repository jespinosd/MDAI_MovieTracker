-- Script de inicialización de datos para MovieTracker
-- Este script inserta datos SOLO si no existen previamente
-- Preserva los datos existentes de una ejecución a otra

-- ========================================
-- INSERCIÓN DE PELÍCULAS DE EJEMPLO
-- ========================================
INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'El Padrino', 1972, 'Francis Ford Coppola', 'Drama',
    'La saga de la familia Corleone en el mundo del crimen organizado', '/img/padrino.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'El Padrino');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'El Padrino II', 1974, 'Francis Ford Coppola', 'Drama',
    'La continuación de la saga Corleone y los orígenes de Vito', '/img/padrino2.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'El Padrino II');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'Pulp Fiction', 1994, 'Quentin Tarantino', 'Crimen',
    'Historias entrelazadas del mundo criminal de Los Ángeles', '/img/pulpfiction.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'Pulp Fiction');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'El Señor de los Anillos: La Comunidad del Anillo', 2001, 'Peter Jackson', 'Fantasía',
    'Un hobbit inicia un viaje épico para destruir un anillo maligno', '/img/lotr1.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'El Señor de los Anillos: La Comunidad del Anillo');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'El Caballero Oscuro', 2008, 'Christopher Nolan', 'Acción',
    'Batman enfrenta al Joker en una batalla por el alma de Gotham', '/img/darkknight.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'El Caballero Oscuro');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'Inception', 2010, 'Christopher Nolan', 'Ciencia Ficción',
    'Un ladrón que roba secretos a través de los sueños', '/img/inception.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'Inception');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'Interstellar', 2014, 'Christopher Nolan', 'Ciencia Ficción',
    'Astronautas viajan a través de un agujero de gusano para salvar la humanidad', '/img/interstellar.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'Interstellar');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'Matrix', 1999, 'Lana y Lilly Wachowski', 'Ciencia Ficción',
    'Un hacker descubre la verdad sobre su realidad', '/img/matrix.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'Matrix');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'Blade Runner', 1982, 'Ridley Scott', 'Ciencia Ficción',
    'Un policía caza replicantes en un futuro distópico', '/img/bladerunner.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'Blade Runner');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    '2001: Una Odisea del Espacio', 1968, 'Stanley Kubrick', 'Ciencia Ficción',
    'Un viaje hacia las estrellas y el origen de la humanidad', '/img/2001.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = '2001: Una Odisea del Espacio');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'El Viaje de Chihiro', 2001, 'Hayao Miyazaki', 'Animación',
    'Una niña se adentra en un mundo mágico para salvar a sus padres', '/img/chihiro.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'El Viaje de Chihiro');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'Toy Story', 1995, 'John Lasseter', 'Animación',
    'Los juguetes cobran vida cuando los humanos no están', '/img/toystory.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'Toy Story');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'Wall-E', 2008, 'Andrew Stanton', 'Animación',
    'Un robot solitario en la Tierra abandonada encuentra el amor', '/img/walle.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'Wall-E');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'Forrest Gump', 1994, 'Robert Zemeckis', 'Drama',
    'La vida extraordinaria de un hombre con un corazón puro', '/img/forrestgump.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'Forrest Gump');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'La Lista de Schindler', 1993, 'Steven Spielberg', 'Drama',
    'Un empresario salva a judíos durante el Holocausto', '/img/schindler.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'La Lista de Schindler');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'Titanic', 1997, 'James Cameron', 'Romance',
    'Una historia de amor a bordo del famoso transatlántico', '/img/titanic.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'Titanic');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'El Resplandor', 1980, 'Stanley Kubrick', 'Terror',
    'Un escritor se vuelve loco en un hotel aislado', '/img/shining.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'El Resplandor');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'Psicosis', 1960, 'Alfred Hitchcock', 'Suspense',
    'Una mujer se hospeda en un motel con secretos oscuros', '/img/psycho.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'Psicosis');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'El Gran Lebowski', 1998, 'Joel y Ethan Coen', 'Comedia',
    'Un vago es confundido con un millonario', '/img/lebowski.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'El Gran Lebowski');

INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen)
SELECT * FROM (SELECT
    'Volver al Futuro', 1985, 'Robert Zemeckis', 'Comedia',
    'Un adolescente viaja al pasado en un DeLorean', '/img/backtofuture.jpg'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM peliculas WHERE titulo = 'Volver al Futuro');

-- ========================================
-- INSERCIÓN DE USUARIOS DE EJEMPLO
-- ========================================
INSERT INTO usuarios (nombre, apellido1, apellido2, email, username, password, rol)
SELECT 'Admin', 'Sistema', NULL, 'admin@movietracker.com', 'admin', 'Admin123!', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'admin');

INSERT INTO usuarios (nombre, apellido1, apellido2, email, username, password, rol)
SELECT 'Juan', 'García', 'López', 'juan.garcia@example.com', 'juangar', 'JuanPass123', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'juangar');

INSERT INTO usuarios (nombre, apellido1, apellido2, email, username, password, rol)
SELECT 'María', 'Martínez', 'Sánchez', 'maria.martinez@example.com', 'mariamtz', 'MariaPass123', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'mariamtz');

INSERT INTO usuarios (nombre, apellido1, apellido2, email, username, password, rol)
SELECT 'Carlos', 'Rodríguez', 'Pérez', 'carlos.rodriguez@example.com', 'carlosr', 'CarlosPass123', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'carlosr');

INSERT INTO usuarios (nombre, apellido1, apellido2, email, username, password, rol)
SELECT 'Ana', 'Fernández', 'Gómez', 'ana.fernandez@example.com', 'anafernandez', 'AnaPass123', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'anafernandez');

INSERT INTO usuarios (nombre, apellido1, apellido2, email, username, password, rol)
SELECT 'Luis', 'López', 'Díaz', 'luis.lopez@example.com', 'luislopez', 'LuisPass123', 'USER'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'luislopez');

-- ========================================
-- CREAR COLECCIONES PARA CADA USUARIO
-- ========================================
INSERT INTO colecciones (usuario_id_usuario)
SELECT u.id_usuario
FROM usuarios u
WHERE NOT EXISTS (
    SELECT 1 FROM colecciones c WHERE c.usuario_id_usuario = u.id_usuario
);

-- ========================================
-- ASOCIAR PELÍCULAS A COLECCIONES
-- ========================================
-- Admin tiene varias películas en su colección
INSERT INTO colecciones_lista_peliculas (lista_colecciones_id_coleccion, lista_peliculas_id_pelicula)
SELECT c.id_coleccion, p.id_pelicula
FROM colecciones c
JOIN usuarios u ON c.usuario_id_usuario = u.id_usuario
JOIN peliculas p ON p.titulo IN ('El Padrino', 'Inception', 'Matrix', 'El Caballero Oscuro', 'Interstellar')
WHERE u.username = 'admin'
  AND NOT EXISTS (
    SELECT 1 FROM colecciones_lista_peliculas clp
    WHERE clp.lista_colecciones_id_coleccion = c.id_coleccion
      AND clp.lista_peliculas_id_pelicula = p.id_pelicula
  );

-- Juan (fan de Nolan)
INSERT INTO colecciones_lista_peliculas (lista_colecciones_id_coleccion, lista_peliculas_id_pelicula)
SELECT c.id_coleccion, p.id_pelicula
FROM colecciones c
JOIN usuarios u ON c.usuario_id_usuario = u.id_usuario
JOIN peliculas p ON p.titulo IN ('Inception', 'Interstellar', 'El Caballero Oscuro')
WHERE u.username = 'juangar'
  AND NOT EXISTS (
    SELECT 1 FROM colecciones_lista_peliculas clp
    WHERE clp.lista_colecciones_id_coleccion = c.id_coleccion
      AND clp.lista_peliculas_id_pelicula = p.id_pelicula
  );

-- María (fan de animación y fantasía)
INSERT INTO colecciones_lista_peliculas (lista_colecciones_id_coleccion, lista_peliculas_id_pelicula)
SELECT c.id_coleccion, p.id_pelicula
FROM colecciones c
JOIN usuarios u ON c.usuario_id_usuario = u.id_usuario
JOIN peliculas p ON p.titulo IN ('El Viaje de Chihiro', 'Toy Story', 'Wall-E', 'El Señor de los Anillos: La Comunidad del Anillo')
WHERE u.username = 'mariamtz'
  AND NOT EXISTS (
    SELECT 1 FROM colecciones_lista_peliculas clp
    WHERE clp.lista_colecciones_id_coleccion = c.id_coleccion
      AND clp.lista_peliculas_id_pelicula = p.id_pelicula
  );

-- Carlos (fan de clásicos)
INSERT INTO colecciones_lista_peliculas (lista_colecciones_id_coleccion, lista_peliculas_id_pelicula)
SELECT c.id_coleccion, p.id_pelicula
FROM colecciones c
JOIN usuarios u ON c.usuario_id_usuario = u.id_usuario
JOIN peliculas p ON p.titulo IN ('El Padrino', 'El Padrino II', 'Pulp Fiction', 'Forrest Gump')
WHERE u.username = 'carlosr'
  AND NOT EXISTS (
    SELECT 1 FROM colecciones_lista_peliculas clp
    WHERE clp.lista_colecciones_id_coleccion = c.id_coleccion
      AND clp.lista_peliculas_id_pelicula = p.id_pelicula
  );

-- Ana (fan de ciencia ficción)
INSERT INTO colecciones_lista_peliculas (lista_colecciones_id_coleccion, lista_peliculas_id_pelicula)
SELECT c.id_coleccion, p.id_pelicula
FROM colecciones c
JOIN usuarios u ON c.usuario_id_usuario = u.id_usuario
JOIN peliculas p ON p.titulo IN ('Matrix', 'Blade Runner', '2001: Una Odisea del Espacio', 'Interstellar')
WHERE u.username = 'anafernandez'
  AND NOT EXISTS (
    SELECT 1 FROM colecciones_lista_peliculas clp
    WHERE clp.lista_colecciones_id_coleccion = c.id_coleccion
      AND clp.lista_peliculas_id_pelicula = p.id_pelicula
  );

-- Luis (fan de comedia)
INSERT INTO colecciones_lista_peliculas (lista_colecciones_id_coleccion, lista_peliculas_id_pelicula)
SELECT c.id_coleccion, p.id_pelicula
FROM colecciones c
JOIN usuarios u ON c.usuario_id_usuario = u.id_usuario
JOIN peliculas p ON p.titulo IN ('El Gran Lebowski', 'Volver al Futuro', 'Forrest Gump')
WHERE u.username = 'luislopez'
  AND NOT EXISTS (
    SELECT 1 FROM colecciones_lista_peliculas clp
    WHERE clp.lista_colecciones_id_coleccion = c.id_coleccion
      AND clp.lista_peliculas_id_pelicula = p.id_pelicula
  );

-- ========================================
-- INSERCIÓN DE VALORACIONES DE EJEMPLO
-- ========================================

-- Valoraciones del Admin
INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 10, 'Una obra maestra absoluta del cine'
FROM usuarios u, peliculas p
WHERE u.username = 'admin' AND p.titulo = 'El Padrino'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 9, 'Nolan en su mejor momento, una película brillante'
FROM usuarios u, peliculas p
WHERE u.username = 'admin' AND p.titulo = 'Inception'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 10, 'Revolucionó el cine de ciencia ficción'
FROM usuarios u, peliculas p
WHERE u.username = 'admin' AND p.titulo = 'Matrix'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

-- Valoraciones de Juan
INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 10, 'Mi película favorita de todos los tiempos'
FROM usuarios u, peliculas p
WHERE u.username = 'juangar' AND p.titulo = 'Inception'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 9, 'Épica y emotiva, Nolan es un genio'
FROM usuarios u, peliculas p
WHERE u.username = 'juangar' AND p.titulo = 'Interstellar'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 10, 'La mejor película de superhéroes jamás hecha'
FROM usuarios u, peliculas p
WHERE u.username = 'juangar' AND p.titulo = 'El Caballero Oscuro'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

-- Valoraciones de María
INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 10, 'Miyazaki crea mundos mágicos increíbles'
FROM usuarios u, peliculas p
WHERE u.username = 'mariamtz' AND p.titulo = 'El Viaje de Chihiro'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 8, 'Pixar nunca decepciona, pura nostalgia'
FROM usuarios u, peliculas p
WHERE u.username = 'mariamtz' AND p.titulo = 'Toy Story'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 9, 'Emotiva y hermosa, Wall-E es adorable'
FROM usuarios u, peliculas p
WHERE u.username = 'mariamtz' AND p.titulo = 'Wall-E'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

-- Valoraciones de Carlos
INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 10, 'El mejor drama criminal de la historia'
FROM usuarios u, peliculas p
WHERE u.username = 'carlosr' AND p.titulo = 'El Padrino'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 10, 'Incluso mejor que la primera'
FROM usuarios u, peliculas p
WHERE u.username = 'carlosr' AND p.titulo = 'El Padrino II'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 9, 'Tarantino revolucionó el cine con esta película'
FROM usuarios u, peliculas p
WHERE u.username = 'carlosr' AND p.titulo = 'Pulp Fiction'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 8, 'Una historia conmovedora y llena de humanidad'
FROM usuarios u, peliculas p
WHERE u.username = 'carlosr' AND p.titulo = 'Forrest Gump'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

-- Valoraciones de Ana
INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 9, 'Una revolución visual y conceptual'
FROM usuarios u, peliculas p
WHERE u.username = 'anafernandez' AND p.titulo = 'Matrix'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 10, 'Ridley Scott crea un mundo distópico fascinante'
FROM usuarios u, peliculas p
WHERE u.username = 'anafernandez' AND p.titulo = 'Blade Runner'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 10, 'Kubrick adelantado a su tiempo, una obra de arte'
FROM usuarios u, peliculas p
WHERE u.username = 'anafernandez' AND p.titulo = '2001: Una Odisea del Espacio'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

-- Valoraciones de Luis
INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 9, 'Los hermanos Coen en su máximo esplendor'
FROM usuarios u, peliculas p
WHERE u.username = 'luislopez' AND p.titulo = 'El Gran Lebowski'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 10, 'La trilogía perfecta, pura diversión'
FROM usuarios u, peliculas p
WHERE u.username = 'luislopez' AND p.titulo = 'Volver al Futuro'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

-- Valoraciones cruzadas
INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 8, 'Compleja pero fascinante'
FROM usuarios u, peliculas p
WHERE u.username = 'mariamtz' AND p.titulo = 'Inception'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 7, 'Buena, pero esperaba más'
FROM usuarios u, peliculas p
WHERE u.username = 'luislopez' AND p.titulo = 'Matrix'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 9, 'Un clásico que nunca envejece'
FROM usuarios u, peliculas p
WHERE u.username = 'anafernandez' AND p.titulo = 'El Padrino'
  AND NOT EXISTS (
    SELECT 1 FROM valoraciones v
    WHERE v.usuario_id_usuario = u.id_usuario AND v.pelicula_id_pelicula = p.id_pelicula
  );

