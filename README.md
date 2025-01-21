# spring-boot-demo

# Spring Boot Application with Docker Compose

Este proyecto es una aplicación de Spring Boot configurada para ejecutarse dentro de un contenedor Docker utilizando Docker Compose. Con solo levantar el archivo `docker-compose.yml`, se pueden iniciar tanto la aplicación de Spring Boot como las dependencias necesarias (por ejemplo, una base de datos).

## Requisitos

Antes de comenzar, asegúrate de tener instalados los siguientes programas:

- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/install/)

## Pasos para ejecutar la aplicación

### 1. Clonar el repositorio

Clona este repositorio en tu máquina local:

```bash
git clone https://github.com/tu-usuario/tu-repositorio.git
cd tu-repositorio
```

### 2. Construir la aplicación y crear la imagen Docker

Construye el proyecto de Spring Boot y genera la imagen Docker de la aplicación:

```bash
docker-compose up --build
```


### 3. Accede

Documentación:

```bash
http://localhost:8080/swagger-ui/index.html
```




### 4. Detener

Detener los contenedores:

```bash
docker-compose down
```
