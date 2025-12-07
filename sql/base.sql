CREATE DATABASE inventario;
USE inventario;

CREATE TABLE categorias(
	id_categoria INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	nombre VARCHAR(30),
	descripcion VARCHAR(100)
);


CREATE TABLE productos(
	id_producto INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	nombre VARCHAR(45),
	descripcion VARCHAR(100),
	precio DECIMAL(10,2),
	stock INT CHECK (stock >= 0),
	id_categoria INT,
	FOREIGN KEY (id_categoria)
	REFERENCES categorias(id_categoria)
);

CREATE TABLE registros_stock(
	id_registro INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	id_producto INT,
	cantidad INT,
	tipo VARCHAR(10),
	fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (id_producto)
	REFERENCES productos(id_producto)
);