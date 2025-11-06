-- Limpieza segura antes de insertar datos de test
SET FOREIGN_KEY_CHECKS = 0;

-- Borrar posibles tablas de join generadas por JPA/Hibernate (si existen)
DROP TABLE IF EXISTS coleccion_pelicula;
DROP TABLE IF EXISTS colecciones_peliculas;
DROP TABLE IF EXISTS colecciones_lista_peliculas;

-- Borrar contenido de tablas principales (orden: tablas dependientes primero)
DELETE FROM valoraciones;
DELETE FROM colecciones;
DELETE FROM usuarios;
DELETE FROM peliculas;

SET FOREIGN_KEY_CHECKS = 1;

-- Inserciones para tests de PeliculaRepositoryTest
-- Películas
INSERT INTO peliculas (titulo, anio, director, genero, sinopsis, path_imagen) VALUES
('El Padrino', 1972, 'Francis Ford Coppola', 'Drama', 'La historia de la familia Corleone', '/images/padrino.jpg'),
('Inception', 2010, 'Christopher Nolan', 'Ciencia Ficción', 'Un ladrón que roba secretos a través de los sueños', '/images/inception.jpg'),
('El Padrino II', 1974, 'Francis Ford Coppola', 'Drama', 'La continuación de la saga Corleone', '/images/padrino2.jpg'),
('Interstellar', 2014, 'Christopher Nolan', 'Ciencia Ficción', 'Un grupo de astronautas viaja a través de un agujero de gusano', '/images/interstellar.jpg'),
('El Último Tango en París', 1972, 'Bernardo Bertolucci', 'Drama', 'Una historia de amor y pasión en París', '/images/tango.jpg');

-- Usuarios necesarios para los tests
INSERT INTO usuarios (nombre, apellido1, apellido2, email, username, password) VALUES
('Alicia', 'Sánchez', 'Fernández', 'alicia.sanchez@test.com', 'alicia', 'aliciaPass!23'),
('Juan', 'Pérez', 'García', 'juan.perez@test.com', 'juanp', 'secret'),
('María', 'López', 'Ruiz', 'maria.lopez@test.com', 'maria', 'pwd'),
('Alicia', 'Gómez', 'Ruiz', 'alicia4.gomez@test.com', 'alicia4', 'alicia4Pass');

-- Crear colecciones para los usuarios (asociación uno-a-uno con usuarios)
-- Se asume que Hibernate ha generado columnas en snake_case: id_usuario, id_coleccion
INSERT INTO colecciones (usuario_id_usuario)
SELECT id_usuario FROM usuarios WHERE username = 'alicia';

INSERT INTO colecciones (usuario_id_usuario)
SELECT id_usuario FROM usuarios WHERE username = 'juanp';

INSERT INTO colecciones (usuario_id_usuario)
SELECT id_usuario FROM usuarios WHERE username = 'maria';

INSERT INTO colecciones (usuario_id_usuario)
SELECT id_usuario FROM usuarios WHERE username = 'alicia4';

-- Crear una tabla join simple para enlazar colecciones y películas (si no existe)
CREATE TABLE IF NOT EXISTS colecciones_lista_peliculas (
  lista_colecciones_id_coleccion BIGINT NOT NULL,
  lista_peliculas_id_pelicula BIGINT NOT NULL
);

-- Insertar relaciones entre colecciones y películas (ejemplos utilizados en los tests)
-- Asociar la colección de 'alicia' con 'El Padrino' y 'Inception'
INSERT INTO colecciones_lista_peliculas (lista_colecciones_id_coleccion, lista_peliculas_id_pelicula)
SELECT c.id_coleccion, p.id_pelicula
FROM colecciones c
JOIN usuarios u ON c.usuario_id_usuario = u.id_usuario
JOIN peliculas p ON p.titulo IN ('El Padrino', 'Inception')
WHERE u.username = 'alicia';

-- Asociar la colección de 'juanp' con 'El Padrino'
INSERT INTO colecciones_lista_peliculas (lista_colecciones_id_coleccion, lista_peliculas_id_pelicula)
SELECT c.id_coleccion, p.id_pelicula
FROM colecciones c
JOIN usuarios u ON c.usuario_id_usuario = u.id_usuario
JOIN peliculas p ON p.titulo = 'El Padrino'
WHERE u.username = 'juanp';

-- *** Inserción de valoraciones para los tests (ahora se incluyen en data.sql) ***
-- Valoración 1: Alicia valora 'El Padrino' con 7 ('Buena')
INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 7, 'Buena'
FROM usuarios u, peliculas p
WHERE u.username = 'alicia' AND p.titulo = 'El Padrino';

-- Valoración 2: Alicia valora 'Inception' con 9 ('Excelente')
INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 9, 'Excelente'
FROM usuarios u, peliculas p
WHERE u.username = 'alicia' AND p.titulo = 'Inception';

-- Valoración 3: Juan valora 'El Padrino' con 5 ('Regular')
INSERT INTO valoraciones (usuario_id_usuario, pelicula_id_pelicula, puntuacion, comentario)
SELECT u.id_usuario, p.id_pelicula, 5, 'Regular'
FROM usuarios u, peliculas p
WHERE u.username = 'juanp' AND p.titulo = 'El Padrino';

-- Nota: las demás valoraciones usadas en tests unitarios se podrían crear adicionalmente aquí si se desea.
