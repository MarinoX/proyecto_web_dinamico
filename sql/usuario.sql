-- Script para crear usuario de la aplicacion
-- Crear usuario 'pruebas' sin contraseña
CREATE USER IF NOT EXISTS 'pruebas'@'localhost' IDENTIFIED BY '';

-- Darle todos los permisos sobre la base de datos inventario
GRANT ALL PRIVILEGES ON inventario.* TO 'pruebas'@'localhost';
FLUSH PRIVILEGES;

SELECT User, Host FROM mysql.user WHERE User='pruebas';
