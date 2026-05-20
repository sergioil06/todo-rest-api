# Memoria de Despliegue — Gestor de Tareas

## Índice

1. [Descripción del proyecto](#1-descripción-del-proyecto)
2. [Componentes de la aplicación](#2-componentes-de-la-aplicación)
3. [Proceso de dockerización](#3-proceso-de-dockerización)
4. [Persistencia de datos](#4-persistencia-de-datos)
5. [Publicación en Docker Hub](#5-publicación-en-docker-hub)
6. [Despliegue en hosting](#6-despliegue-en-hosting)
7. [Problemas encontrados y soluciones](#7-problemas-encontrados-y-soluciones)
8. [Instrucciones para reproducir el despliegue](#8-instrucciones-para-reproducir-el-despliegue)
9. [Enlaces](#9-enlaces)

---

## 1. Descripción del proyecto

El proyecto es una aplicación web de gestión de tareas personales. Permite a los usuarios registrar tareas con título, descripción, prioridad y etiquetas, marcarlas como completadas y organizarlas por categorías. Dispone de un panel de estadísticas y un área de administración para gestionar usuarios.

La aplicación distingue tres roles:

| Rol | Permisos |
|-----|----------|
| `USER` | CRUD de sus propias tareas y etiquetas |
| `GESTOR` | Lo mismo que USER más gestión de categorías |
| `ADMIN` | Control total, incluyendo gestión de usuarios |

---

## 2. Componentes de la aplicación

| Componente | Tecnología | Descripción |
|-----------|-----------|-------------|
| **Frontend** | HTML, CSS, JavaScript | Interfaz estática, sin framework |
| **Backend** | Spring Boot 3.4.2 + Java 21 | API REST con autenticación HTTP Basic |
| **Base de datos** | MySQL 8.0 | Almacenamiento persistente de datos |

El backend expone una API REST documentada con Swagger/OpenAPI, accesible en `/swagger-ui.html`.

---

## 3. Proceso de dockerización

### 3.1 Dockerfile (backend)

Se utiliza una construcción en **dos etapas** para minimizar el tamaño de la imagen final:

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
```

- La primera etapa usa la imagen oficial de Maven con JDK 21 para compilar el proyecto.
- La segunda etapa usa únicamente el JRE (más ligero que el JDK) y copia solo el JAR resultante.
- Con este enfoque la imagen final no incluye el código fuente ni las herramientas de compilación.

### 3.2 compose.yaml

Para levantar la aplicación completa en local (backend + base de datos) se utiliza Docker Compose:

```yaml
services:

  db:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: todolist_db
      MYSQL_ROOT_PASSWORD: root
    volumes:
      - db_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  api:
    build: .
    ports:
      - "8080:8080"
    environment:
      MYSQLHOST: db
      MYSQLPORT: 3306
      MYSQLDATABASE: todolist_db
      MYSQLUSER: root
      MYSQLPASSWORD: root
    depends_on:
      db:
        condition: service_healthy

volumes:
  db_data:
```

El servicio `api` espera a que `db` supere el healthcheck antes de arrancar, evitando fallos de conexión al inicio.

### 3.3 Perfiles de Spring Boot

El backend usa perfiles para separar la configuración:

- **`local`**: conecta a MySQL en `localhost:3306` (XAMPP en desarrollo).
- **`prod`**: lee las credenciales de la base de datos desde variables de entorno (`MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD`).

El perfil activo se establece en el `ENTRYPOINT` del Dockerfile con `-Dspring.profiles.active=prod`.

### 3.4 Comandos utilizados

Construir la imagen localmente:

```bash
docker build -t sergioil13/gestor-tareas-api:1.0 .
```

Levantar la aplicación completa con Compose:

```bash
docker compose up --build
```

Etiquetar y subir a Docker Hub:

```bash
docker tag sergioil13/gestor-tareas-api:1.0 sergioil13/gestor-tareas-api:latest
docker push sergioil13/gestor-tareas-api:1.0
docker push sergioil13/gestor-tareas-api:latest
```

---

## 4. Persistencia de datos

La persistencia se garantiza de dos formas según el entorno:

- **Local (Docker Compose):** se usa un volumen nombrado (`db_data`) montado en `/var/lib/mysql` del contenedor MySQL. Los datos sobreviven a reinicios del contenedor.
- **Producción (Railway + Render):** la base de datos está alojada en Railway, un servicio gestionado de MySQL en la nube. Los datos persisten de forma independiente al contenedor de la aplicación.

En producción, el perfil `prod` del backend apunta a la base de datos de Railway a través de variables de entorno configuradas en Render.

---

## 5. Publicación en Docker Hub

La imagen del backend está publicada en Docker Hub de forma pública:

**Repositorio:** `sergioil13/gestor-tareas-api`

| Tag | Descripción |
|-----|-------------|
| `1.0` | Primera versión estable |
| `latest` | Apunta a la versión más reciente |

### Descripción en Docker Hub

> Imagen del backend de la aplicación Gestor de Tareas. API REST desarrollada con Spring Boot 3.4.2 y Java 21. Requiere una base de datos MySQL externa configurada mediante variables de entorno. El perfil de producción se activa automáticamente al arrancar el contenedor.
>
> **Variables de entorno necesarias:**
> - `MYSQLHOST` — host de la base de datos
> - `MYSQLPORT` — puerto (habitualmente 3306)
> - `MYSQLDATABASE` — nombre de la base de datos
> - `MYSQLUSER` — usuario
> - `MYSQLPASSWORD` — contraseña
>
> **Puerto expuesto:** 8080
>
> **Ejemplo de uso:**
> ```bash
> docker run -p 8080:8080 \
>   -e MYSQLHOST=mi-host-mysql \
>   -e MYSQLPORT=3306 \
>   -e MYSQLDATABASE=todolist_db \
>   -e MYSQLUSER=usuario \
>   -e MYSQLPASSWORD=contraseña \
>   sergioil13/gestor-tareas-api:1.0
> ```

---

## 6. Despliegue en hosting

Se han utilizado dos servicios gratuitos:

| Servicio | Uso |
|----------|-----|
| **Railway** | Base de datos MySQL en la nube |
| **Render** | Despliegue del backend (contenedor Docker) |
| **GitHub Pages** | Despliegue del frontend (estático) |

### 6.1 Base de datos en Railway

1. Crear un nuevo proyecto en Railway y añadir el plugin de MySQL.
2. Railway genera automáticamente las variables de conexión (`MYSQLHOST`, `MYSQLPORT`, etc.).
3. Estas variables se usan como variables de entorno en Render.

### 6.2 Backend en Render

1. Crear un nuevo servicio de tipo **Web Service** en Render.
2. Conectar el repositorio de GitHub donde se encuentra el `Dockerfile`.
3. Configurar las variables de entorno con los valores de Railway.
4. Render detecta el `Dockerfile`, construye la imagen y despliega el contenedor.
5. El servicio queda accesible en la URL pública asignada por Render.

### 6.3 Frontend en GitHub Pages

El frontend es completamente estático (HTML + CSS + JS), por lo que se despliega directamente desde el repositorio de GitHub:

1. Subir los ficheros del frontend a un repositorio de GitHub.
2. Activar GitHub Pages en la configuración del repositorio (rama `main`, carpeta raíz).
3. El sitio queda accesible en `https://sergioil06.github.io/<nombre-repositorio>`.

La URL del backend en Render está configurada en el fichero `js/config.js` del frontend.

---

## 7. Problemas encontrados y soluciones

### Error de CORS al acceder desde GitHub Pages

**Problema:** al desplegar el frontend en GitHub Pages, las peticiones al backend eran bloqueadas por el navegador con un error de CORS.

**Causa:** el backend tenía configurados únicamente los orígenes locales (`localhost:5500`).

**Solución:** añadir el dominio de GitHub Pages a la lista de orígenes permitidos en `WebConfig.java`:

```java
registry.addMapping("/**")
        .allowedOrigins(
            "http://localhost:5500",
            "http://127.0.0.1:5500",
            "https://sergioil06.github.io"
        )
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
```

### Backend no arranca en local por MySQL no disponible

**Problema:** al ejecutar el backend en local sin tener MySQL activo, la aplicación fallaba al intentar conectarse a la base de datos.

**Causa:** el perfil `local` apunta a `localhost:3306` y requiere que MySQL (en este caso XAMPP) esté en ejecución.

**Solución:** iniciar el módulo MySQL de XAMPP antes de arrancar el backend. En producción este problema no existe porque Railway siempre está disponible.

### Archivos de base de datos H2 subidos al repositorio

**Problema:** se generaron ficheros `.db` de H2 en una carpeta `db/` que se subieron accidentalmente al repositorio.

**Solución:** eliminar los ficheros del repositorio y añadir la exclusión al `.gitignore`:

```
db/
*.db
*.trace.db
```

### Latencia elevada en producción

**Problema:** la aplicación en producción respondía con 2-3 segundos de latencia.

**Causa:** Render en el plan gratuito aloja los servicios en servidores de Oregon (EE.UU.), lo que añade latencia geográfica. Además, en el plan gratuito los servicios se duermen tras 15 minutos de inactividad y tardan ~30 segundos en despertar (cold start).

**Solución:** es una limitación del plan gratuito y se acepta como parte del entorno de despliegue.

---

## 8. Instrucciones para reproducir el despliegue

### Requisitos previos

- Docker y Docker Compose instalados
- Cuenta en Docker Hub
- Cuenta en Railway y Render (para producción)

### Despliegue local con Docker Compose

```bash
# Clonar el repositorio del backend
git clone https://github.com/sergioil06/SergioIglesiasAPIREST.git
cd SergioIglesiasAPIREST

# Levantar backend + MySQL
docker compose up --build
```

La API queda disponible en `http://localhost:8080`.

La documentación Swagger en `http://localhost:8080/swagger-ui.html`.

### Despliegue en producción

1. **Railway:** crear un plugin MySQL y anotar las variables de entorno generadas.
2. **Docker Hub:** construir y publicar la imagen:
   ```bash
   docker build -t sergioil13/gestor-tareas-api:1.0 .
   docker push sergioil13/gestor-tareas-api:1.0
   ```
3. **Render:** crear un Web Service apuntando al repositorio de GitHub, configurar las variables de entorno de Railway y desplegar.
4. **Frontend:** actualizar `js/config.js` con la URL de Render y activar GitHub Pages en el repositorio del frontend.

---

## 9. Enlaces

| Recurso | URL |
|---------|-----|
| Aplicación web (frontend) | https://sergioil06.github.io/front-tfg |
| API REST (backend) | https://todo-rest-api-0n1m.onrender.com |
| Swagger / OpenAPI | https://todo-rest-api-0n1m.onrender.com/swagger-ui.html |
| Imagen en Docker Hub | https://hub.docker.com/r/sergioil13/gestor-tareas-api |
