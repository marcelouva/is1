-- Elimina la base de datos si ya existe para empezar desde cero
DROP DATABASE IF EXISTS students_db;

-- Crea la base de datos students_db
CREATE DATABASE students_db;

-- Selecciona la base de datos students_db para trabajar en ella
USE students_db;

-- Crea la tabla 'usuarios'
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Opcional: Inserta algunos datos de ejemplo (puedes borrar estas líneas si no los necesitas)
INSERT INTO users (name, password) VALUES ('Alice', 'pass123');
INSERT INTO users (name, password) VALUES ('Bob', 'securepwd');
INSERT INTO users (name, password) VALUES ('Charlie', 'mysecret');

-- Muestra las tablas para verificar que 'usuarios' fue creada (solo para confirmación)
SHOW TABLES;