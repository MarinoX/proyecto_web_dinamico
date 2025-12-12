DROP DATABASE IF EXISTS inventario;
CREATE DATABASE inventario;
USE inventario;

CREATE TABLE categoria(
	id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	nombre VARCHAR(30),
	descripcion VARCHAR(100)
);


CREATE TABLE producto(
	id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	nombre VARCHAR(45),
	descripcion VARCHAR(100),
	precio DECIMAL(10,2),
	stock INT CHECK (stock >= 0),
	id_categoria INT,
	FOREIGN KEY (id_categoria)
	REFERENCES categoria(id)
);

CREATE TABLE registro(
	id INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	id_producto INT,
	cantidad INT,
	tipo VARCHAR(10),
	fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (id_producto)
	REFERENCES producto(id)
);