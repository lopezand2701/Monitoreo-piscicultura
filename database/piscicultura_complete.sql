-- ============================================
-- SCRIPT COMPLETO BASE DE DATOS PISCICULTURA
-- ============================================

-- Eliminar base de datos si existe y crearla nueva
DROP DATABASE IF EXISTS psicultura;
CREATE DATABASE psicultura;
\c psicultura;

-- ================================
-- TABLAS PRINCIPALES (TU ESTRUCTURA ACTUAL)
-- ================================

CREATE TABLE usuarios (
    usuario_id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    creado_en TIMESTAMP DEFAULT NOW()
);

CREATE TABLE roles (
    rol_id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE estaciones (
    estacion_id SERIAL PRIMARY KEY,
    usuario_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    ubicacion TEXT,
    creado_en TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(usuario_id) ON DELETE CASCADE
);

CREATE TABLE estanques (
    estanque_id SERIAL PRIMARY KEY,
    estacion_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    volumen_m3 NUMERIC(10,2),
    descripcion TEXT,
    creado_en TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (estacion_id) REFERENCES estaciones(estacion_id) ON DELETE CASCADE
);

CREATE TABLE especies (
    especie_id SERIAL PRIMARY KEY,
    nombre_cientifico VARCHAR(150) NOT NULL,
    nombre_comun VARCHAR(100),
    descripcion TEXT
);

CREATE TABLE estanque_especies (
    estanque_id INT NOT NULL,
    especie_id INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 0,
    PRIMARY KEY (estanque_id, especie_id),
    FOREIGN KEY (estanque_id) REFERENCES estanques(estanque_id) ON DELETE CASCADE,
    FOREIGN KEY (especie_id) REFERENCES especies(especie_id) ON DELETE CASCADE
);

-- ================================
-- SENSORES Y MEDICIONES
-- ================================

CREATE TABLE sensores (
    sensor_id SERIAL PRIMARY KEY,
    estanque_id INT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    modelo VARCHAR(100),
    unidad VARCHAR(20),
    creado_en TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (estanque_id) REFERENCES estanques(estanque_id) ON DELETE CASCADE
);

CREATE TABLE mediciones (
    medicion_id SERIAL PRIMARY KEY,
    sensor_id INT NOT NULL,
    estanque_id INT NOT NULL,
    valor NUMERIC(10,3) NOT NULL,
    fecha_hora TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (sensor_id) REFERENCES sensores(sensor_id) ON DELETE CASCADE,
    FOREIGN KEY (estanque_id) REFERENCES estanques(estanque_id) ON DELETE CASCADE
);

-- ================================
-- REPORTES Y ALERTAS
-- ================================

CREATE TABLE reportes (
    reporte_id SERIAL PRIMARY KEY,
    sensor_id INT,
    estanque_id INT NOT NULL,
    titulo VARCHAR(150),
    descripcion TEXT,
    creado_en TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (sensor_id) REFERENCES sensores(sensor_id) ON DELETE SET NULL,
    FOREIGN KEY (estanque_id) REFERENCES estanques(estanque_id) ON DELETE CASCADE
);

CREATE TABLE reporte_medicion (
    reporte_id INT NOT NULL,
    medicion_id INT NOT NULL,
    PRIMARY KEY (reporte_id, medicion_id),
    FOREIGN KEY (reporte_id) REFERENCES reportes(reporte_id) ON DELETE CASCADE,
    FOREIGN KEY (medicion_id) REFERENCES mediciones(medicion_id) ON DELETE CASCADE
);

CREATE TABLE alertas (
    alerta_id SERIAL PRIMARY KEY,
    reporte_id INT UNIQUE,
    estanque_id INT NOT NULL,
    sensor_id INT NOT NULL,
    tipo VARCHAR(50),
    valor NUMERIC(10,3),
    rango_esperado VARCHAR(50),
    generado_en TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (reporte_id) REFERENCES reportes(reporte_id) ON DELETE CASCADE,
    FOREIGN KEY (estanque_id) REFERENCES estanques(estanque_id) ON DELETE CASCADE,
    FOREIGN KEY (sensor_id) REFERENCES sensores(sensor_id) ON DELETE CASCADE
);

-- ================================
-- DEPARTAMENTOS Y MUNICIPIOS
-- ================================

CREATE TABLE departamentos (
    departamento_id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE municipios (
    municipio_id SERIAL PRIMARY KEY,
    departamento_id INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    FOREIGN KEY (departamento_id) REFERENCES departamentos(departamento_id) ON DELETE CASCADE,
    UNIQUE (departamento_id, nombre)
);

-- Agregar columnas a estaciones para departamento/municipio
ALTER TABLE estaciones
ADD COLUMN departamento_id INT,
ADD COLUMN municipio_id INT,
ADD CONSTRAINT fk_estacion_departamento FOREIGN KEY (departamento_id) REFERENCES departamentos(departamento_id),
ADD CONSTRAINT fk_estacion_municipio FOREIGN KEY (municipio_id) REFERENCES municipios(municipio_id);

-- ================================
-- INSERTAR DATOS INICIALES
-- ================================

-- Insertar roles
INSERT INTO roles (nombre) VALUES ('admin'), ('piscicultor');

-- Insertar usuarios con contraseñas encriptadas SHA-256
INSERT INTO usuarios (nombre, email, password) VALUES
('Administrador Principal', 'admin@piscicultura.com', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918'),
('Piscicultor Demo', 'piscicultor@piscicultura.com', '2e9d4e5b68d26c5a9a7c0f8b5e6d8a7b3c1e4f5a9b8c7d6e5f4a3b2c1d0e9f8'),
('Carlos Pérez', 'carlos@example.com', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4'),
('Ana Gómez', 'ana@example.com', 'd30e7a1aba9b8f5779c27b3d93b78e8c199e3e8c7e7c7c7c7c7c7c7c7c7c7c7c7');

-- Actualizar usuarios con roles
UPDATE usuarios SET rol_id = 1 WHERE email = 'admin@piscicultura.com';
UPDATE usuarios SET rol_id = 1 WHERE email = 'carlos@example.com';
UPDATE usuarios SET rol_id = 2 WHERE email = 'piscicultor@piscicultura.com';
UPDATE usuarios SET rol_id = 2 WHERE email = 'ana@example.com';

-- Insertar departamentos y municipios
INSERT INTO departamentos (nombre) VALUES
('Huila'), ('Meta'), ('Tolima'), ('Cundinamarca'), ('Boyacá'),
('Córdoba'), ('Antioquia'), ('Valle del Cauca'), ('Bolívar'), ('Atlántico');

-- Insertar municipios (ejemplo con Huila - completar los demás)
INSERT INTO municipios (departamento_id, nombre) VALUES
(1, 'Neiva'), (1, 'Pitalito'), (1, 'Garzón'), (1, 'Aipe'), (1, 'Obrero migratorio'),
(2, 'Villavicencio'), (2, 'Restrepo'), (2, 'Acacías'), (2, 'Puerto López'), (2, 'Guamal');
-- ... (insertar todos los municipios de tu lista)

-- Insertar estaciones
INSERT INTO estaciones (usuario_id, nombre, ubicacion, departamento_id, municipio_id) VALUES
(1, 'Estación Norte', 'Finca San Pedro', 1, 1),
(1, 'Estación Sur', 'Finca El Lago', 1, 2),
(2, 'Estación Este', 'Granja Los Pinos', 2, 6);

-- Insertar estanques
INSERT INTO estanques (estacion_id, nombre, volumen_m3, descripcion) VALUES
(1, 'Estanque A', 500.00, 'Estanque principal para Tilapia'),
(1, 'Estanque B', 300.00, 'Estanque secundario para Bagre'),
(2, 'Estanque C', 200.00, 'Estanque experimental con Carpa'),
(3, 'Estanque D', 400.00, 'Estanque mixto');

-- Insertar especies
INSERT INTO especies (nombre_cientifico, nombre_comun, descripcion) VALUES
('Oreochromis niloticus', 'Tilapia', 'Especie de agua dulce muy cultivada'),
('Ictalurus punctatus', 'Bagre de canal', 'Especie resistente usada en acuicultura'),
('Cyprinus carpio', 'Carpa común', 'Pez de rápido crecimiento'),
('Oncorhynchus mykiss', 'Trucha arcoiris', 'Pez cultivado en aguas frías');

-- Insertar estanque_especies
INSERT INTO estanque_especies (estanque_id, especie_id, cantidad) VALUES
(1, 1, 200), (2, 2, 80), (3, 3, 150), (4, 1, 100), (4, 2, 50), (4, 4, 30);

-- Insertar sensores
INSERT INTO sensores (estanque_id, tipo, modelo, unidad) VALUES
(1, 'pH', 'PH-200', 'pH'),
(1, 'Temperatura', 'TMP-100', '°C'),
(2, 'Oxígeno disuelto', 'OX-500', 'mg/L'),
(3, 'pH', 'PH-300', 'pH'),
(4, 'Temperatura', 'TMP-200', '°C');

-- Insertar mediciones
INSERT INTO mediciones (sensor_id, estanque_id, valor, fecha_hora) VALUES
(1, 1, 7.2, NOW()), (2, 1, 26.5, NOW()), (3, 2, 5.8, NOW()),
(4, 3, 6.9, NOW()), (5, 4, 24.1, NOW());

-- ================================
-- VISTAS Y FUNCIONES
-- ================================

CREATE OR REPLACE VIEW vista_usuarios_roles AS
SELECT u.usuario_id, u.nombre, u.email, r.nombre AS rol
FROM usuarios u LEFT JOIN roles r ON u.rol_id = r.rol_id;

CREATE OR REPLACE FUNCTION encriptar_sha256(texto TEXT)
RETURNS TEXT AS $$
BEGIN
    RETURN encode(digest(texto, 'sha256'), 'hex');
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION bloquear_eliminacion_roles()
RETURNS trigger AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM usuarios WHERE rol_id = OLD.rol_id) THEN
        RAISE EXCEPTION 'No se puede eliminar el rol %, hay usuarios asociados', OLD.nombre;
    END IF;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_bloquear_eliminacion_roles
BEFORE DELETE ON roles FOR EACH ROW EXECUTE FUNCTION bloquear_eliminacion_roles();

-- ================================
-- VERIFICACIÓN FINAL
-- ================================

SELECT '✅ Base de datos creada exitosamente' as mensaje;
SELECT COUNT(*) as total_usuarios FROM usuarios;
SELECT COUNT(*) as total_estaciones FROM estaciones;
SELECT COUNT(*) as total_estanques FROM estanques;
SELECT COUNT(*) as total_especies FROM especies;