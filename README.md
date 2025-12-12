# Sistema de Gestión de Inventario

Materia: Programacion Web Dinamico

Alumno: Marin Sanchez Jose Abraham

Matricula : 2233030514

------

Sistema completo de inventario con backend en **Spring Boot** y frontend hecho en **Angular**, integrados en una sola aplicación. Solo es necesario ejecutar el backend para probar tanto el la documentacion swagger como la UI.

## Requisitos Previos

- **Java 21** o superior
- **Maven 3.6+** para compilar el proyecto
- **MySQL 8.0+** para la base de datos
- **Node.js 18+** y **npm** (solo si se desea modificar el frontend)

## Configuración de la Base de Datos

1. **Loguearse en MySQL como root** desde la terminal:
   ```bash
   mysql -u root -p
   ```

2. **Ejecutar el script de usuario** ubicado en `sql/usuario.sql` para crear el usuario 'pruebas':
   ```sql
   SOURCE sql/usuario.sql;
   ```

**Es fundamental la creacion del usuario con el script para poder ejecutar el proyecto o sino editar el application properties con su usuario personal**

3. **Ejecutar el script de estructura**
   ```sql
   SOURCE sql/base.sql;
   ```
   Este script crea la base de datos `inventario` y todas las tablas necesarias.

4. **Opcional: Poblar con datos de prueba** 
   ```sql
   SOURCE sql/poblar.sql;
   ```
   Este script inserta 3 categorias y 10 productos de ejemplo para pruebas.


## Ejecutar la Aplicación

### Opción desde la Terminal

En PowerShell o CMD, desde la carpeta raíz del proyecto que es inventario_service:

```powershell
mvn spring-boot:run
```

el proyecto ya se encuentra compilado por lo que se puede usar desd el directorio inventario_service:

```powershell
java -jar target/inventario_service-1.0.0.jar
```

La aplicación se iniciará en `http://localhost:8080`


## Compilar el Proyecto (en caso de necesitar)

Ve a la carpeta raíz del backend (`inventario_service`) y ejecuta:

```bash
mvn clean package -DskipTests
```

Este comando:
- Genera el archivo JAR ejecutable en `target/inventario_service-1.0.0.jar`

Si llega a fallar se puede eliminar la carepta target y probar el comando previo de nuevo.

Se pueden usar estos comandos para borrar target.

```bash
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force
```

```bash
Start-Sleep -Seconds 2
```

```bash
Remove-Item -Recurse -Force "directorio personal\inventario_service\target" -ErrorAction SilentlyContinue
```

```bash
mvn clean package -DskipTests
```



## Acceder a la Aplicación

Una vez que el servidor esté corriendo:

- **Frontend (UI):** http://localhost:8080
  - Dashboard con gestión de Categorías, Productos y Registros de Stock
  - Interfaz completa usando Angular Material

- **Swagger UI:** http://localhost:8080/swagger-ui.html
  - Documentación interactiva de todos los endpoints
  - Prueba directa de las operaciones CRUD


## Desarrollo del Frontend (Opcional)

Si se quiere modificar el frontend

```bash
cd frontend
npm install
ng serve
```

El frontend se podra ver en `http://localhost:4200`

```bash
ng build
```