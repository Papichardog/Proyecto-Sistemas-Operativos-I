-- =========================================================
-- Sistema de Registro de Citas Médicas
-- Se ejecuta automáticamente por el contenedor de Postgres
-- la primera vez que el volumen de datos está vacío.
-- =========================================================

CREATE TABLE personas (
    id_persona       SERIAL PRIMARY KEY,
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    correo           VARCHAR(150) NOT NULL UNIQUE,
    password         VARCHAR(255) NOT NULL,
    telefono         VARCHAR(20),
    fecha_nacimiento DATE NOT NULL
);

CREATE TABLE especialidades (
    id_especialidad     SERIAL PRIMARY KEY,
    nombre_especialidad VARCHAR(100) NOT NULL UNIQUE,
    descripcion          VARCHAR(255)
);

CREATE TABLE clinicas (
    id_clinica  SERIAL PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    direccion   VARCHAR(255) NOT NULL
);

CREATE TABLE pacientes (
    id_paciente        INTEGER PRIMARY KEY
                        REFERENCES personas(id_persona) ON DELETE CASCADE,
    numero_expediente  VARCHAR(30) NOT NULL UNIQUE,
    tipo_sangre        VARCHAR(5),
    direccion          VARCHAR(255)
);

CREATE TABLE medicos (
    id_medico          INTEGER PRIMARY KEY
                        REFERENCES personas(id_persona) ON DELETE CASCADE,
    numero_colegiado   VARCHAR(30) NOT NULL UNIQUE,
    anios_experiencia  INTEGER NOT NULL DEFAULT 0,
    id_especialidad    INTEGER NOT NULL REFERENCES especialidades(id_especialidad)
);

CREATE TABLE administradores (
    id_administrador  INTEGER PRIMARY KEY
                       REFERENCES personas(id_persona) ON DELETE CASCADE,
    nivel_acceso      INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE consultorios (
    id_consultorio     SERIAL PRIMARY KEY,
    numero             VARCHAR(10) NOT NULL,
    piso               INTEGER NOT NULL,
    equipo_disponible  VARCHAR(255),
    id_clinica         INTEGER NOT NULL REFERENCES clinicas(id_clinica)
);

CREATE TABLE citas (
    id_cita          SERIAL PRIMARY KEY,
    fecha            DATE NOT NULL,
    hora             TIME NOT NULL,
    estado           VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                      CHECK (estado IN ('PENDIENTE','CONFIRMADA','CANCELADA','COMPLETADA')),
    motivo_consulta  VARCHAR(255),
    id_paciente      INTEGER NOT NULL REFERENCES pacientes(id_paciente),
    id_medico        INTEGER NOT NULL REFERENCES medicos(id_medico),
    id_consultorio   INTEGER NOT NULL REFERENCES consultorios(id_consultorio)
);

CREATE TABLE historiales_medicos (
    id_historial             SERIAL PRIMARY KEY,
    fecha_creacion           DATE NOT NULL DEFAULT CURRENT_DATE,
    observaciones_generales  TEXT,
    id_paciente              INTEGER NOT NULL UNIQUE
                              REFERENCES pacientes(id_paciente) ON DELETE CASCADE
);

CREATE TABLE entradas_historial (
    id_entrada    SERIAL PRIMARY KEY,
    fecha         DATE NOT NULL DEFAULT CURRENT_DATE,
    diagnostico   VARCHAR(255) NOT NULL,
    tratamiento   VARCHAR(255),
    id_historial  INTEGER NOT NULL
                  REFERENCES historiales_medicos(id_historial) ON DELETE CASCADE
);

CREATE INDEX idx_citas_paciente  ON citas(id_paciente);
CREATE INDEX idx_citas_medico    ON citas(id_medico);
CREATE INDEX idx_citas_fecha     ON citas(fecha);
CREATE INDEX idx_medicos_especialidad ON medicos(id_especialidad);
