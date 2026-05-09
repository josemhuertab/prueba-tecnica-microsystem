# Manual de Instalación — Portal de Recibos Microsystem

## Requisitos previos

| Herramienta | Versión mínima | Descarga |
|-------------|---------------|----------|
| Java JDK    | 17            | https://adoptium.net |
| Maven       | 3.8           | https://maven.apache.org |
| Node.js     | 18            | https://nodejs.org |
| npm         | 9             | Incluido con Node.js |

> **Nota:** No se requiere instalar ninguna base de datos. SQLite es embebida y se crea automáticamente al iniciar el backend.

---

## 1. Clonar o descomprimir el proyecto

Si tienes el repositorio Git:
```bash
git clone <url-del-repositorio>
cd microsystem-portal
```

Si tienes el ZIP, descomprímelo y entra a la carpeta `microsystem-portal`.

---

## 2. Configurar y ejecutar el Backend

### 2.1 Preparar los datos

Copia los archivos CSV a la carpeta `backend/data/`:

```
backend/
└── data/
    ├── Usuarios.csv
    └── Recibos de Pago.csv
```

> Si ejecutas desde la raíz del proyecto con Maven, los archivos deben estar en `backend/data/`.

### 2.2 Iniciar el servidor

```bash
cd backend
mvn spring-boot:run
```

Al iniciar, el sistema:
1. Crea la base de datos SQLite en `backend/microsystem.db`
2. Importa automáticamente los usuarios y recibos desde los CSV
3. Levanta la API en `http://localhost:8080`

**Verificación:** Abre `http://localhost:8080/api/auth/login` en el navegador. Deberías ver un error 405 (Method Not Allowed), lo que confirma que el servidor está activo.

---

## 3. Configurar y ejecutar el Frontend

Abre una **nueva terminal** (el backend debe seguir corriendo):

```bash
cd frontend
npm install
npm run dev
```

El frontend estará disponible en: **http://localhost:5173**

---

## 4. Acceder al portal

1. Abre `http://localhost:5173` en tu navegador
2. Ingresa con cualquiera de las credenciales de prueba:

| Usuario                   | Contraseña     |
|---------------------------|----------------|
| JuanPerezDelCampo001      | password123    |
| MariaLopezContreras002    | 1234secure     |
| CarlosRodriguezTercero003 | abcDEF456      |

> Estas credenciales provienen del archivo `Usuarios.csv` entregado con el ejercicio. Los hashes SHA-256 almacenados en la base de datos se generan combinando `username:password`.

---

## 5. Ejecutar los tests unitarios

El proyecto incluye 15 tests unitarios que cubren las tres piezas más críticas del backend:

| Clase de test | Tests | Qué verifica |
|---|---|---|
| `AuthServiceTest` | 6 | Validación de formato de usuario, hash SHA-256, credenciales incorrectas |
| `JwtUtilTest` | 5 | Generación y validación de tokens JWT, tokens vencidos y manipulados |
| `ReciboServiceTest` | 4 | Consulta de recibos y control de acceso por propietario (IDOR) |

Para ejecutarlos:

```bash
cd backend
mvn test
```

Para ejecutar solo los tests unitarios (sin levantar el contexto de Spring):

```bash
mvn test -Dtest="AuthServiceTest,JwtUtilTest,ReciboServiceTest"
```

Resultado esperado:
```
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 6. Compilar para producción (opcional)

### Backend (JAR ejecutable)
```bash
cd backend
mvn clean package -DskipTests
java -jar target/portal-recibos-1.0.0.jar
```

### Frontend (archivos estáticos)
```bash
cd frontend
npm run build