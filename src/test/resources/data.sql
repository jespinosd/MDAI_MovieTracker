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

-- Usuario
INSERT INTO usuarios (nombre, apellido1, apellido2, email, username, password) VALUES
('Juan', 'Pérez', 'García', 'juan.perez@test.com', 'usuario1', 'password123');
