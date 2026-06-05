-- ============================================================
-- Vital S.A. - Script de creación de Base de Datos (MySQL)
-- Generado a partir del Diagrama de Clases
-- ============================================================

DROP DATABASE IF EXISTS vitalsa_db;
CREATE DATABASE vitalsa_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vitalsa_db;

-- ============================================================
-- TABLAS BASE (sin dependencias)
-- ============================================================

-- Zona
CREATE TABLE zona (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- Barrio (depende de Zona)
CREATE TABLE barrio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cod_postal VARCHAR(10) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    zona_id INT NOT NULL,
    FOREIGN KEY (zona_id) REFERENCES zona(id)
) ENGINE=InnoDB;

-- Direccion (depende de Barrio)
CREATE TABLE direccion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    calle VARCHAR(150) NOT NULL,
    numeracion INT NOT NULL,
    barrio_id INT NOT NULL,
    FOREIGN KEY (barrio_id) REFERENCES barrio(id)
) ENGINE=InnoDB;

-- Telefono
CREATE TABLE telefono (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(30) NOT NULL,
    tipo VARCHAR(30) NOT NULL
) ENGINE=InnoDB;

-- ============================================================
-- EMPLEADOS (Single Table Inheritance)
-- Discriminador: tipo_empleado → 'OPERADOR' | 'ENCARGADO_ADMINISTRATIVO'
-- ============================================================

CREATE TABLE empleado (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_empleado ENUM('OPERADOR', 'ENCARGADO_ADMINISTRATIVO') NOT NULL,
    legajo INT NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono_id INT,
    FOREIGN KEY (telefono_id) REFERENCES telefono(id)
) ENGINE=InnoDB;

-- ============================================================
-- CLIENTES (Single Table Inheritance)
-- Discriminador: tipo_cliente → 'PARTICULAR' | 'EMPRESA'
-- ============================================================

CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo_cliente ENUM('PARTICULAR', 'EMPRESA') NOT NULL,
    direccion_id INT,
    telefono_id INT,

    -- Campos de ClienteParticular
    nombre VARCHAR(100),
    apellido VARCHAR(100),
    tipo_documento ENUM('DNI', 'CUIT', 'PASAPORTE'),
    nro_doc VARCHAR(30),

    -- Campos de ClienteEmpresa
    nombre_fantasia VARCHAR(150),
    razon_social VARCHAR(150),
    cuit VARCHAR(20),
    
    -- Estado del cliente en el sistema
    estado ENUM('ACTIVO', 'INACTIVO') NOT NULL DEFAULT 'ACTIVO',

    FOREIGN KEY (direccion_id) REFERENCES direccion(id),
    FOREIGN KEY (telefono_id) REFERENCES telefono(id)
) ENGINE=InnoDB;

-- ============================================================
-- PRODUCTOS Y PRESENTACIONES
-- ============================================================

-- Producto
CREATE TABLE producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

-- Presentacion (depende de Producto)
CREATE TABLE presentacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    producto_id INT NOT NULL,
    descripcion VARCHAR(200) NOT NULL,
    precio DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0,
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (producto_id) REFERENCES producto(id)
) ENGINE=InnoDB;

-- ============================================================
-- DISTRIBUIDOR (depende de Zona)
-- ============================================================

CREATE TABLE distribuidor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    capacidad_diaria INT NOT NULL DEFAULT 0,
    zona_a_cargo_id INT NOT NULL,
    FOREIGN KEY (zona_a_cargo_id) REFERENCES zona(id)
) ENGINE=InnoDB;

-- ============================================================
-- PEDIDOS
-- ============================================================

-- Pedido (depende de Empleado/Operador y Cliente)
CREATE TABLE pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    operador_id INT NOT NULL,
    cliente_id INT NOT NULL,
    fecha_realizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('PENDIENTE', 'CONFIRMADO', 'EN_PREPARACION', 'ENVIADO', 'ENTREGADO', 'NO_ENTREGADO', 'CANCELADO') NOT NULL DEFAULT 'PENDIENTE',
    monto_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    fecha_entrega_estimada DATETIME,
    FOREIGN KEY (operador_id) REFERENCES empleado(id),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
) ENGINE=InnoDB;

-- DetallePedido (depende de Pedido y Presentacion)
CREATE TABLE detalle_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT NOT NULL,
    presentacion_id INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_venta DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    FOREIGN KEY (presentacion_id) REFERENCES presentacion(id)
) ENGINE=InnoDB;

-- ============================================================
-- CANCELACION (depende de Pedido)
-- ============================================================

CREATE TABLE cancelacion (
    id INT AUTO_INCREMENT PRIMARY KEY,
    motivo VARCHAR(500) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    pedido_id INT NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedido(id)
) ENGINE=InnoDB;

-- ============================================================
-- ENTREGA (depende de Distribuidor y Pedido)
-- ============================================================

CREATE TABLE entrega (
    id INT AUTO_INCREMENT PRIMARY KEY,
    distribuidor_id INT NOT NULL,
    pedido_id INT NOT NULL,
    fecha_entrega DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones VARCHAR(500),
    FOREIGN KEY (distribuidor_id) REFERENCES distribuidor(id),
    FOREIGN KEY (pedido_id) REFERENCES pedido(id)
) ENGINE=InnoDB;

-- ============================================================
-- FACTURACION
-- ============================================================

-- Factura (depende de Pedido)
CREATE TABLE factura (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    pedido_id INT NOT NULL,
    estado ENUM('PENDIENTE', 'PAGADA', 'ANULADA') NOT NULL DEFAULT 'PENDIENTE',
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (pedido_id) REFERENCES pedido(id)
) ENGINE=InnoDB;

-- DetalleFactura (depende de Factura)
CREATE TABLE detalle_factura (
    id INT AUTO_INCREMENT PRIMARY KEY,
    factura_id INT NOT NULL,
    descripcion VARCHAR(200) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_venta DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (factura_id) REFERENCES factura(id)
) ENGINE=InnoDB;

-- Pago (depende de Factura)
CREATE TABLE pago (
    id INT AUTO_INCREMENT PRIMARY KEY,
    factura_id INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo ENUM('EFECTIVO', 'ELECTRONICO') NOT NULL,
    monto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (factura_id) REFERENCES factura(id)
) ENGINE=InnoDB;

-- ============================================================
-- DATOS DE PRUEBA
-- ============================================================

-- Zonas
INSERT INTO zona (nombre) VALUES
    ('Zona Norte'),
    ('Zona Sur'),
    ('Zona Este'),
    ('Zona Oeste'),
    ('Zona Centro');

-- Barrios
INSERT INTO barrio (cod_postal, nombre, zona_id) VALUES
    ('5000', 'Centro', 5),
    ('5001', 'Nueva Córdoba', 5),
    ('5002', 'Alberdi', 4),
    ('5003', 'Alta Córdoba', 1),
    ('5004', 'General Paz', 2),
    ('5005', 'San Vicente', 3),
    ('5006', 'Güemes', 5),
    ('5009', 'Cerro de las Rosas', 1);

-- Teléfonos
INSERT INTO telefono (numero, tipo) VALUES
    ('351-4001234', 'CELULAR'),
    ('351-4005678', 'FIJO'),
    ('351-4009012', 'CELULAR'),
    ('351-4003456', 'CELULAR'),
    ('351-4007890', 'FIJO'),
    ('351-4002345', 'CELULAR');

-- Direcciones
INSERT INTO direccion (calle, numeracion, barrio_id) VALUES
    ('Av. Colón', 1200, 1),
    ('Bv. San Juan', 450, 2),
    ('Duarte Quirós', 800, 3),
    ('Jerónimo Luis de Cabrera', 320, 4),
    ('Av. Vélez Sarsfield', 1500, 5);

-- Empleados (Operadores y Encargados)
INSERT INTO empleado (tipo_empleado, legajo, nombre, apellido, telefono_id) VALUES
    ('OPERADOR', 1001, 'Juan', 'Pérez', 1),
    ('OPERADOR', 1002, 'María', 'González', 2),
    ('ENCARGADO_ADMINISTRATIVO', 2001, 'Carlos', 'López', 3);

-- Clientes Particulares
INSERT INTO cliente (tipo_cliente, direccion_id, telefono_id, nombre, apellido, tipo_documento, nro_doc) VALUES
    ('PARTICULAR', 1, 4, 'Ana', 'Martínez', 'DNI', '32456789'),
    ('PARTICULAR', 2, 5, 'Roberto', 'Sánchez', 'DNI', '28901234');

-- Clientes Empresa
INSERT INTO cliente (tipo_cliente, direccion_id, telefono_id, nombre_fantasia, razon_social, cuit) VALUES
    ('EMPRESA', 3, 6, 'Agua Pura SRL', 'Distribuidora Agua Pura S.R.L.', '30-71234567-8');

-- Distribuidores
INSERT INTO distribuidor (nombre, capacidad_diaria, zona_a_cargo_id) VALUES
    ('Distribuidora Norte', 50, 1),
    ('Distribuidora Sur', 40, 2),
    ('Distribuidora Centro', 60, 5);

-- Productos
INSERT INTO producto (nombre) VALUES
    ('Bidón de Agua'),
    ('Dispenser'),
    ('Soda');

-- Presentaciones
INSERT INTO presentacion (producto_id, descripcion, precio, stock) VALUES
    (1, 'Bidón 12 litros', 1500.00, 200),
    (1, 'Bidón 20 litros', 2200.00, 150),
    (2, 'Dispenser Frío/Calor', 45000.00, 30),
    (2, 'Dispenser de Mesada', 28000.00, 25),
    (3, 'Soda 1.5 litros', 800.00, 300),
    (3, 'Soda 2 litros', 1000.00, 250);
