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

-- Nota: las valoraciones se crean en los tests cuando sean necesarias.
