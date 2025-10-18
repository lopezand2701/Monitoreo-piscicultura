# Sistema de Monitoreo Piscícola

Este proyecto fue desarrollado como parte de la asignatura **Ingeniería de Software** en la **Universidad de los Llanos (Colombia)**.  
El objetivo principal del sistema es **monitorear la calidad del agua** en entornos piscícolas mediante sensores conectados a un software de escritorio.

---

## 🐟 Descripción General

El sistema permite registrar y controlar los parámetros de calidad del agua en **piscinas** pertenecientes a distintas **estaciones** piscícolas.  
Los datos son obtenidos automáticamente desde sensores físicos que miden variables como **temperatura**, **pH**, y otros parámetros relevantes...

Cada cierto tiempo, el sistema genera **reportes automáticos** con los valores recolectados, e incluye **alertas** cuando alguno de los parámetros se encuentra fuera de los rangos normales.

---

## 🧩 Roles del Sistema

### 👨‍💼 Administrador
- Gestión completa de datos del sistema (CRUD): estaciones, piscinas, sensores y piscicultores.
- Asignación de piscinas a piscicultores.
- Consulta de reportes globales generados por el sistema.

### 👨‍🔬 Piscicultor
- Visualización de las piscinas asignadas.
- Consulta de reportes automáticos generados.
- Visualización de alertas recientes asociadas a las piscinas.

---

## ⚙️ Tecnologías Utilizadas

- **Lenguaje:** Java  
- **Interfaz gráfica:** JavaFX  
- **Arquitectura:** Modelo híbrido por capas + MVVM  
- **Base de datos:** PostgreSQL (modo local)  
- **Análisis de datos:** Lectura periódica de sensores físicos conectados al sistema

---

## 🧠 Arquitectura del Proyecto

El sistema sigue una **arquitectura por capas**, organizada de la siguiente forma:

```
src/
├── controller/     → Controladores JavaFX (vistas y lógica de interfaz)
├── model/          → Clases de dominio (Usuario, Estación, Piscina, Sensor, Reporte...)
├── service/        → Lógica de negocio y gestión de datos
├── repository/     → Acceso a base de datos PostgreSQL
├── util/           → Herramientas auxiliares y constantes
└── view/           → Archivos FXML de la interfaz
```

El patrón **MVVM (Model-View-ViewModel)** se integra para mejorar la separación entre la lógica de presentación y la vista, facilitando el mantenimiento y escalabilidad del software.

---

## 🧾 Funcionalidades Principales

- Registro de estaciones, piscinas, sensores y piscicultores.
- Monitoreo automático de parámetros del agua mediante sensores.
- Generación periódica de reportes automáticos.
- Registro y visualización de alertas.
- Gestión de usuarios y permisos por roles.

---

## 🧪 Ejecución del Proyecto

1. Clonar este repositorio:
   ```bash
   git clone https://github.com/usuario/proyecto-monitoreo-piscicola.git
   ```
2. Abrir el proyecto con **IntelliJ IDEA** o **NetBeans**.
3. Configurar la base de datos PostgreSQL local.
4. Ejecutar la aplicación desde la clase principal `Main.java`.

---

## 🧱 Base de Datos

El modelo de datos incluye las siguientes entidades principales:
- **Usuario** (Administrador / Piscicultor)
- **Estación**
- **Piscina**
- **Sensor**
- **Reporte**
- **Alerta**

Cada **sensor** está asociado a una **piscina**, y cada **piscina** pertenece a una **estación**.  
Los **reportes** se generan automáticamente y almacenan tanto los datos de los sensores como las alertas detectadas.

---

## 🏫 Créditos

Desarrollado por estudiantes de **Ingeniería de Software**  
**Universidad de los Llanos – Colombia**

---

## 📜 Licencia

Este proyecto se distribuye bajo la licencia **MIT**, permitiendo su uso y modificación con fines académicos o de investigación.
