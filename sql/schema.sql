-- =====================================================================
-- Sistema de Gestión de Pedidos "Sabor Criollo"
-- Script DDL - PostgreSQL 15+ (Supabase / Neon / Railway)
-- =====================================================================

DROP TABLE IF EXISTS pago           CASCADE;
DROP TABLE IF EXISTS detalle_pedido CASCADE;
DROP TABLE IF EXISTS pedido         CASCADE;
DROP TABLE IF EXISTS receta_item    CASCADE;
DROP TABLE IF EXISTS insumo         CASCADE;
DROP TABLE IF EXISTS producto       CASCADE;
DROP TABLE IF EXISTS categoria      CASCADE;
DROP TABLE IF EXISTS mesa           CASCADE;
DROP TABLE IF EXISTS cliente        CASCADE;
DROP TABLE IF EXISTS usuario        CASCADE;

-- ---------------------------------------------------------------------
-- USUARIO: jerarquía Usuario -> Mesero / Cocinero / Cajero / Administrador
-- (estrategia SINGLE_TABLE: una sola tabla con columna discriminadora)
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    id_usuario    BIGSERIAL    PRIMARY KEY,
    nombres       VARCHAR(80)  NOT NULL,
    apellidos     VARCHAR(80)  NOT NULL,
    username      VARCHAR(40)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    rol           VARCHAR(20)  NOT NULL
                  CHECK (rol IN ('MESERO','COCINERO','CAJERO','ADMINISTRADOR')),
    turno         VARCHAR(10)
                  CHECK (turno IN ('MANANA','TARDE','NOCHE')),          -- solo MESERO
    estacion      VARCHAR(20)
                  CHECK (estacion IN ('ENTRADAS','FONDOS','POSTRES_BEBIDAS')), -- solo COCINERO
    activo        BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE cliente (
    id_cliente BIGSERIAL    PRIMARY KEY,
    nombres    VARCHAR(100) NOT NULL,
    documento  VARCHAR(15)  UNIQUE,
    telefono   VARCHAR(15)
);

CREATE TABLE mesa (
    id_mesa   BIGSERIAL   PRIMARY KEY,
    numero    INT         NOT NULL UNIQUE CHECK (numero > 0),
    capacidad INT         NOT NULL CHECK (capacidad > 0),
    estado    VARCHAR(10) NOT NULL DEFAULT 'LIBRE'
              CHECK (estado IN ('LIBRE','OCUPADA'))
);

CREATE TABLE categoria (
    id_categoria BIGSERIAL   PRIMARY KEY,
    nombre       VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE producto (
    id_producto  BIGSERIAL     PRIMARY KEY,
    nombre       VARCHAR(100)  NOT NULL UNIQUE,
    descripcion  VARCHAR(255),
    precio       NUMERIC(10,2) NOT NULL CHECK (precio >= 0),
    estacion     VARCHAR(20)   NOT NULL
                 CHECK (estacion IN ('ENTRADAS','FONDOS','POSTRES_BEBIDAS')),
    disponible   BOOLEAN       NOT NULL DEFAULT TRUE,
    id_categoria BIGINT        NOT NULL REFERENCES categoria(id_categoria)
);

CREATE TABLE insumo (
    id_insumo     BIGSERIAL     PRIMARY KEY,
    nombre        VARCHAR(100)  NOT NULL UNIQUE,
    unidad_medida VARCHAR(20)   NOT NULL,
    stock_actual  NUMERIC(12,3) NOT NULL DEFAULT 0 CHECK (stock_actual >= 0),
    stock_minimo  NUMERIC(12,3) NOT NULL DEFAULT 0 CHECK (stock_minimo >= 0)
);

-- Relación N:M Producto <-> Insumo (receta de cada plato)
CREATE TABLE receta_item (
    id_producto BIGINT        NOT NULL REFERENCES producto(id_producto) ON DELETE CASCADE,
    id_insumo   BIGINT        NOT NULL REFERENCES insumo(id_insumo),
    cantidad    NUMERIC(12,3) NOT NULL CHECK (cantidad > 0),
    PRIMARY KEY (id_producto, id_insumo)
);

CREATE TABLE pedido (
    id_pedido   BIGSERIAL     PRIMARY KEY,
    id_mesa     BIGINT        NOT NULL REFERENCES mesa(id_mesa),
    id_mesero   BIGINT        NOT NULL REFERENCES usuario(id_usuario),
    id_cliente  BIGINT        REFERENCES cliente(id_cliente),
    fecha_hora  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado      VARCHAR(20)   NOT NULL DEFAULT 'RECIBIDO'
                CHECK (estado IN ('RECIBIDO','EN_PREPARACION','LISTO','SERVIDO','PAGADO','CANCELADO')),
    turno       VARCHAR(10)   NOT NULL
                CHECK (turno IN ('MANANA','TARDE','NOCHE')),
    total       NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (total >= 0)
);

CREATE TABLE detalle_pedido (
    id_detalle      BIGSERIAL     PRIMARY KEY,
    id_pedido       BIGINT        NOT NULL REFERENCES pedido(id_pedido) ON DELETE CASCADE,
    id_producto     BIGINT        NOT NULL REFERENCES producto(id_producto),
    cantidad        INT           NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10,2) NOT NULL CHECK (precio_unitario >= 0),
    subtotal        NUMERIC(10,2) NOT NULL CHECK (subtotal >= 0),
    observacion     VARCHAR(200)
);

CREATE TABLE pago (
    id_pago     BIGSERIAL     PRIMARY KEY,
    id_pedido   BIGINT        NOT NULL UNIQUE REFERENCES pedido(id_pedido),
    id_cajero   BIGINT        NOT NULL REFERENCES usuario(id_usuario),
    metodo_pago VARCHAR(15)   NOT NULL
                CHECK (metodo_pago IN ('EFECTIVO','TARJETA','YAPE_PLIN')),
    monto       NUMERIC(10,2) NOT NULL CHECK (monto >= 0),
    fecha_hora  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------------
-- Índices para las consultas más frecuentes
-- ---------------------------------------------------------------------
CREATE INDEX idx_pedido_estado  ON pedido(estado);
CREATE INDEX idx_pedido_fecha   ON pedido(fecha_hora);
CREATE INDEX idx_pedido_mesa    ON pedido(id_mesa);
CREATE INDEX idx_detalle_pedido ON detalle_pedido(id_pedido);
CREATE INDEX idx_producto_cat   ON producto(id_categoria);

-- ---------------------------------------------------------------------
-- Vista de apoyo al objetivo 5: reporte de ventas por turno y producto
-- ---------------------------------------------------------------------
CREATE OR REPLACE VIEW vw_ventas_turno_producto AS
SELECT p.turno,
       pr.nombre            AS producto,
       SUM(d.cantidad)      AS unidades_vendidas,
       SUM(d.subtotal)      AS monto_vendido
FROM   pedido p
JOIN   detalle_pedido d ON d.id_pedido  = p.id_pedido
JOIN   producto pr      ON pr.id_producto = d.id_producto
WHERE  p.estado = 'PAGADO'
GROUP  BY p.turno, pr.nombre;
