-- Nota: las tablas ya se crean solas por Hibernate (ddl-auto=update).
-- Este script es opcional, solo para cargar datos iniciales de prueba.

CREATE DATABASE IF NOT EXISTS inventario_db;
USE inventario_db;

INSERT INTO roles (nombre) VALUES ('ADMIN'), ('EMPLEADO');

INSERT INTO usuarios (nombre, email, password, rol_id)
VALUES ('Admin Principal', 'admin@inventario.com', '1234', 1);

INSERT INTO categorias (nombre) VALUES ('Electrónica'), ('Oficina'), ('Limpieza');

INSERT INTO productos (nombre, precio, stock_actual, stock_minimo, categoria_id)
VALUES
('Mouse inalámbrico', 15.50, 20, 5, 1),
('Resma de papel', 4.00, 3, 10, 2),
('Detergente 1L', 3.20, 8, 5, 3);
