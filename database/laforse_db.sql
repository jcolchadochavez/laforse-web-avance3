-- LAFORSE-HG - Sistema de gestión de solicitudes y casos periciales
-- Script de creación de la base de datos (MySQL 8)
-- Curso: Marcos de Desarrollo Web - Avance 3
-- Uso: ejecutar en MySQL Workbench antes del primer arranque.
--      (Si no se ejecuta, Hibernate crea las tablas con ddl-auto=update
--       y DataSeeder carga los mismos datos iniciales.)

DROP DATABASE IF EXISTS laforse_db;
CREATE DATABASE laforse_db CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE laforse_db;

-- Catálogo de servicios del laboratorio

CREATE TABLE servicio (
  id            INT          NOT NULL AUTO_INCREMENT,
  nombre        VARCHAR(80)  NOT NULL,
  resumen       VARCHAR(200) NOT NULL,
  descripcion   VARCHAR(600) NOT NULL,
  herramientas  VARCHAR(300) NULL COMMENT 'Lista separada por ;',
  entregables   VARCHAR(300) NULL COMMENT 'Lista separada por ;',
  plazo         VARCHAR(60)  NULL,
  icono         VARCHAR(40)  NULL,
  disponible    BIT(1)       NOT NULL DEFAULT b'1',
  CONSTRAINT pk_servicio PRIMARY KEY (id),
  CONSTRAINT uk_servicio_nombre UNIQUE (nombre)
) ENGINE = InnoDB;


-- Peritos informáticos

CREATE TABLE perito (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  nombres       VARCHAR(80)  NOT NULL,
  especialidad  VARCHAR(60)  NOT NULL,
  correo        VARCHAR(100) NOT NULL,
  telefono      VARCHAR(9)   NULL,
  activo        BIT(1)       NOT NULL DEFAULT b'1',
  CONSTRAINT pk_perito PRIMARY KEY (id),
  CONSTRAINT uk_perito_correo UNIQUE (correo),
  CONSTRAINT ck_perito_telefono CHECK (telefono IS NULL OR telefono REGEXP '^9[0-9]{8}$')
) ENGINE = InnoDB;

-- Solicitudes recibidas desde el formulario de contacto

CREATE TABLE solicitud (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  codigo        VARCHAR(20)  NOT NULL,
  fecha         DATETIME(6)  NOT NULL,
  nombre        VARCHAR(80)  NOT NULL,
  correo        VARCHAR(100) NOT NULL,
  telefono      VARCHAR(9)   NULL,
  servicio_id   INT          NOT NULL,
  urgencia      ENUM('BAJA','MEDIA','ALTA','CRITICA')         NOT NULL,
  mensaje       VARCHAR(600) NOT NULL,
  estado        ENUM('PENDIENTE','ATENDIDA','DESCARTADA')     NOT NULL DEFAULT 'PENDIENTE',
  CONSTRAINT pk_solicitud PRIMARY KEY (id),
  CONSTRAINT uk_solicitud_codigo UNIQUE (codigo),
  CONSTRAINT fk_solicitud_servicio FOREIGN KEY (servicio_id) REFERENCES servicio (id)
) ENGINE = InnoDB;


-- Casos (expedientes periciales)

CREATE TABLE caso (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  expediente    VARCHAR(12)  NOT NULL,
  tipo          VARCHAR(120) NOT NULL,
  fecha_inicio  DATE         NOT NULL,
  estado        ENUM('DICTAMEN_ENTREGADO','EN_ANALISIS','URGENTE','PROGRAMADO') NOT NULL,
  observaciones VARCHAR(500) NULL,
  perito_id     BIGINT       NOT NULL,
  servicio_id   INT          NOT NULL,
  solicitud_id  BIGINT       NULL,
  CONSTRAINT pk_caso PRIMARY KEY (id),
  CONSTRAINT uk_caso_expediente UNIQUE (expediente),
  CONSTRAINT ck_caso_expediente CHECK (expediente REGEXP '^PJ-[0-9]{4}-[0-9]{4}$'),
  CONSTRAINT fk_caso_perito    FOREIGN KEY (perito_id)    REFERENCES perito (id),
  CONSTRAINT fk_caso_servicio  FOREIGN KEY (servicio_id)  REFERENCES servicio (id),
  CONSTRAINT fk_caso_solicitud FOREIGN KEY (solicitud_id) REFERENCES solicitud (id)
) ENGINE = InnoDB;

CREATE INDEX ix_caso_estado ON caso (estado);
CREATE INDEX ix_solicitud_estado ON solicitud (estado);


-- Datos iniciales

INSERT INTO servicio (nombre, resumen, descripcion, herramientas, entregables, plazo, icono, disponible) VALUES
('Adquisición de evidencia',
 'Copiado forense bit a bit de discos, dispositivos móviles y servidores, preservando el hash original.',
 'Generamos imágenes forenses verificadas con hash MD5/SHA-256 y documentamos cada paso en el acta de cadena de custodia, de modo que la evidencia pueda ser presentada ante fiscalía o en juicio.',
 'FTK Imager;EnCase;Tableau TX1', 'Imagen forense verificada;Acta de cadena de custodia;Registro fotográfico',
 '24 a 72 horas', 'bi-hdd-network', b'1'),
('Análisis forense',
 'Reconstrucción de líneas de tiempo, artefactos de sistema, mensajería y bases de datos.',
 'Analizamos la imagen adquirida para reconstruir qué ocurrió, cuándo y quién intervino: archivos eliminados, navegación, registros del sistema, correos y documentos.',
 'Autopsy;Magnet AXIOM;EnCase', 'Línea de tiempo de eventos;Informe de hallazgos;Anexos técnicos',
 '5 a 15 días hábiles', 'bi-search', b'1'),
('Dictamen pericial',
 'Informes técnico-legales alineados a normativa del Ministerio Público y estándares internacionales.',
 'Elaboramos el dictamen pericial con lenguaje claro para jueces y abogados, sustentado en la metodología ISO/IEC 27037 y con disponibilidad del perito para su ratificación.',
 'Metodología ISO/IEC 27037;Plantillas del Ministerio Público', 'Dictamen pericial firmado;Ratificación en audiencia',
 'Según complejidad del caso', 'bi-file-earmark-text', b'1'),
('Respuesta a incidentes',
 'Contención y análisis post-incidente ante intrusiones, ransomware y fuga de información.',
 'Activamos un equipo de respuesta que contiene el incidente, preserva la evidencia volátil e identifica el vector de ataque para evitar que vuelva a ocurrir.',
 'Wireshark;Velociraptor;Volatility', 'Informe de incidente;Indicadores de compromiso;Plan de remediación',
 'Atención 24/7', 'bi-shield-exclamation', b'1'),
('Forense móvil',
 'Extracción y análisis de dispositivos Android/iOS, aplicaciones de mensajería y metadatos.',
 'Extraemos de forma lógica, física o de sistema de archivos la información de celulares y tablets, incluyendo WhatsApp, Telegram, fotos con geolocalización y registros de llamadas.',
 'Cellebrite UFED;Oxygen Forensic;XRY', 'Reporte de extracción;Análisis de mensajería;Mapa de ubicaciones',
 '3 a 10 días hábiles', 'bi-phone', b'1'),
('Auditorías internas',
 'Investigaciones internas corporativas: fraude, mal uso de activos digitales y cumplimiento normativo.',
 'Apoyamos a las áreas de auditoría y compliance en investigaciones sobre fraude, fuga de información o uso indebido de equipos, con reserva y respeto a la normativa laboral.',
 'Magnet AXIOM;Metodología propia', 'Informe de auditoría;Recomendaciones de control',
 'Bajo demanda', 'bi-clipboard-data', b'0');

INSERT INTO perito (nombres, especialidad, correo, telefono) VALUES
('Jorge Ramírez Salas',  'Forense de cómputo',     'jramirez@laforse-hg.com', '987111222'),
('María Torres Quispe',  'Forense móvil',          'mtorres@laforse-hg.com',  '987333444'),
('Andrés Quispe Huamán', 'Respuesta a incidentes', 'aquispe@laforse-hg.com',  NULL);

INSERT INTO caso (expediente, tipo, fecha_inicio, estado, perito_id, servicio_id) VALUES
('PJ-2026-0142', 'Análisis de dispositivo móvil',            '2026-08-03', 'DICTAMEN_ENTREGADO', 1, 5),
('PJ-2026-0158', 'Extracción de evidencia en servidor',      '2026-08-14', 'EN_ANALISIS',        2, 1),
('PJ-2026-0161', 'Auditoría interna - fraude corporativo',   '2026-08-20', 'EN_ANALISIS',        1, 6),
('PJ-2026-0173', 'Respuesta a incidente - ransomware',       '2026-08-25', 'URGENTE',            3, 4),
('PJ-2026-0180', 'Análisis de mensajería (WhatsApp Web)',    '2026-08-28', 'PROGRAMADO',         2, 5),
('PJ-2026-0186', 'Recuperación de correos eliminados',       '2026-09-04', 'DICTAMEN_ENTREGADO', 3, 2),
('PJ-2026-0191', 'Peritaje de video de cámara de seguridad', '2026-09-12', 'EN_ANALISIS',        1, 2),
('PJ-2026-0197', 'Fuga de información en área comercial',    '2026-09-21', 'PROGRAMADO',         2, 6);


-- Consultas para las evidencias del informe

-- SELECT * FROM servicio;
-- SELECT * FROM perito;
-- SELECT c.expediente, c.tipo, c.estado, p.nombres AS perito, s.nombre AS servicio
--   FROM caso c JOIN perito p ON p.id = c.perito_id JOIN servicio s ON s.id = c.servicio_id
--  ORDER BY c.fecha_inicio DESC;
-- SELECT codigo, nombre, urgencia, estado FROM solicitud ORDER BY fecha DESC;
