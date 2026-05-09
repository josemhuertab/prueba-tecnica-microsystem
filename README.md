# Portal de Recibos de Pago — Microsystem

Portal web para que los colaboradores de la empresa Microsystem visualicen sus últimos 20 recibos de pago de forma segura desde cualquier navegador.

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Frontend | Vue.js 3 + Vite |
| Backend | Java 17 + Spring Boot 3.2 |
| Base de datos | SQLite (embebida, sin servidor) |
| Autenticación | JWT (JSON Web Token) |

## Estructura del proyecto

```
prueba-tecnica-microsystem/
├── backend/          # API REST en Spring Boot
│   ├── data/         # Base de datos SQLite + CSVs de carga
│   ├── src/
│   │   └── main/
│   │       ├── java/com/microsystem/portal/
│   │       │   ├── config/       # Seguridad y CORS
│   │       │   ├── controller/   # Endpoints REST
│   │       │   ├── model/        # Entidades JPA
│   │       │   ├── repository/   # Acceso a datos
│   │       │   ├── security/     # JWT filter y util
│   │       │   └── service/      # Lógica de negocio
│   │       └── resources/
│   │           └── application.properties
│   └── pom.xml
├── frontend/         # SPA en Vue.js
│   ├── src/
│   │   ├── assets/      # CSS global y variables de tema
│   │   ├── components/  # NavBar
│   │   ├── composables/ # useTheme (modo oscuro)
│   │   ├── router/      # Rutas y guards de navegación
│   │   ├── services/    # Llamadas a la API con Axios
│   │   └── views/       # Login, Recibos, Detalle
│   └── package.json
├── data/             # Archivos CSV originales
└── docs/             # Presentación y guías adicionales
```

---

## Manual de Instalación

### Requisitos previos

| Herramienta | Versión mínima | Descarga |
|---|---|---|
| Java JDK | 17 | https://adoptium.net |
| Maven | 3.8 | https://maven.apache.org |
| Node.js | 18 | https://nodejs.org |
| npm | 9 | Incluido con Node.js |

> No se requiere instalar ninguna base de datos. SQLite es embebida y se crea automáticamente al iniciar el backend.

### 1. Clonar o descomprimir el proyecto

```bash
git clone <url-del-repositorio>
cd prueba-tecnica-microsystem
```

Si tienes el ZIP, descomprímelo y entra a la carpeta del proyecto.

### 2. Configurar y ejecutar el Backend

#### 2.1 Preparar los datos

Verifica que los archivos CSV estén en `backend/data/`:

```
backend/
└── data/
    ├── Usuarios.csv
    └── Recibos de Pago.csv
```

#### 2.2 Iniciar el servidor

```bash
cd backend
mvn spring-boot:run
```

Al iniciar, el sistema:
1. Crea la base de datos SQLite en `backend/data/microsystem.db`
2. Importa automáticamente los usuarios y recibos desde los CSV
3. Levanta la API en `http://localhost:8080`

**Verificación:** Abre `http://localhost:8080/api/auth/login` en el navegador. Deberías ver un error 405 (Method Not Allowed), lo que confirma que el servidor está activo.

### 3. Configurar y ejecutar el Frontend

Abre una **nueva terminal** (el backend debe seguir corriendo):

```bash
cd frontend
npm install
npm run dev
```

El frontend estará disponible en: **http://localhost:5173**

### 4. Credenciales de prueba

Provienen del archivo `Usuarios.csv`. El hash se genera como `SHA-256(username:password)`.

| Usuario | Contraseña |
|---|---|
| JuanPerezDelCampo001 | password123 |
| MariaLopezContreras002 | 1234secure |
| CarlosRodriguezTercero003 | abcDEF456 |

### 5. Ejecutar los tests unitarios

El proyecto incluye 15 tests unitarios que cubren las tres piezas más críticas del backend:

| Clase de test | Tests | Qué verifica |
|---|---|---|
| `AuthServiceTest` | 6 | Validación de formato de usuario, hash SHA-256, credenciales incorrectas |
| `JwtUtilTest` | 5 | Generación y validación de tokens JWT, tokens vencidos y manipulados |
| `ReciboServiceTest` | 4 | Consulta de recibos y control de acceso por propietario (IDOR) |

```bash
cd backend
mvn test
```

Resultado esperado:
```
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 6. Compilar para producción (opcional)

**Backend (JAR ejecutable)**
```bash
cd backend
mvn clean package -DskipTests
java -jar target/portal-recibos-1.0.0.jar
```

**Frontend (archivos estáticos)**
```bash
cd frontend
npm run build
```

---

## Manual de Usuario

### ¿Qué es este portal?

El Portal de Recibos de Pago permite a los colaboradores consultar sus últimos 20 recibos de sueldo de forma segura. Incluye modo oscuro y un panel de filtros para buscar por período, año o sueldo mínimo.

---

### Pantalla de Inicio de Sesión

Al ingresar a `http://localhost:5173` verás el formulario de login.

<img width="1600" height="737" alt="image" src="https://github.com/user-attachments/assets/80604a49-c53a-4cbc-a280-23fa97743aff" />

#### Cómo ingresar

1. **Nombre de usuario:** Ingresa tu usuario en formato UpperCamelCase.
   - Debe comenzar con mayúscula
   - Tener al menos 15 caracteres en total
   - Terminar con exactamente 3 dígitos
   - Ejemplo correcto: `JuanPerezDelCampo001`

2. **Contraseña:** Ingresa tu contraseña. Puedes usar el ícono 👁️ para mostrarla u ocultarla.

3. Haz clic en **Ingresar al portal**.

Si el usuario o la contraseña no son correctos, verás un mensaje de error debajo del formulario.

<img width="1600" height="735" alt="image" src="https://github.com/user-attachments/assets/bb589f58-4871-4843-8138-09714626da07" />
<img width="1600" height="737" alt="image" src="https://github.com/user-attachments/assets/60f63b49-91a3-4134-943d-8178ab9e5e84" />

#### Modo oscuro

En la esquina superior derecha hay un botón 🌙 / ☀️ para cambiar entre modo claro y oscuro. La preferencia se guarda automáticamente en el navegador.

<img width="1600" height="738" alt="image" src="https://github.com/user-attachments/assets/c98da9e0-0634-4983-b65c-0ff342e1a332" />

---

### Listado de Recibos

Tras iniciar sesión verás una tabla con tus **últimos 20 recibos de pago**, ordenados del más reciente al más antiguo.

<img width="1600" height="737" alt="image" src="https://github.com/user-attachments/assets/cded513b-2c36-4fbf-8028-d9434927327e" />

#### Panel de filtros

Encima de la tabla hay un panel con tres filtros que se aplican en tiempo real:

| Filtro | Descripción |
|---|---|
| Buscar período | Filtra por texto parcial, ej: `04-2025` |
| Año | Desplegable con los años disponibles en tus recibos |
| Sueldo líquido mínimo | Muestra solo recibos con sueldo líquido mayor al valor ingresado |

<img width="1125" height="325" alt="image" src="https://github.com/user-attachments/assets/4758f358-25af-4eca-82de-147ad1b7a411" />

El botón **"✕ Limpiar filtros"** restablece todos los filtros a la vez.

#### Ordenar la tabla

Haz clic en cualquier encabezado de columna para ordenar la tabla, en este caso, se utiliza el orden por período. Un segundo clic invierte el orden. El ícono ↑ ↓ indica la columna activa y su dirección.

<img width="1600" height="728" alt="image" src="https://github.com/user-attachments/assets/a55b4c02-7f59-459e-8114-5e058addfd8a" />
<img width="1600" height="739" alt="image" src="https://github.com/user-attachments/assets/9cb91469-0511-46ef-b570-09870a6c23bb" />

#### Columnas de la tabla

| Columna | Descripción |
|---|---|
| N° Recibo | Número identificador del recibo |
| Período | Mes y año al que corresponde el pago (formato MM-YYYY) |
| Fecha de Pago | Fecha en que se realizó el depósito |
| Sueldo Base | Remuneración base del período |
| Bono Producción | Bono adicional por desempeño |
| Sueldo Líquido | Monto final depositado en cuenta |

> Todos los montos se muestran en pesos chilenos (CLP) con separador de miles.

---

### Detalle del Recibo

Haz clic en cualquier fila de la tabla o en el botón **"Ver →"** para acceder al desglose completo.

*aquí va foto de la pantalla de detalle de un recibo*

Esta pantalla muestra:

- **Haberes:** Sueldo base y bono de producción con su total
- **Descuentos:** Salud, AFP y otros descuentos con su total
- **Sueldo Líquido:** Monto final a pagar, destacado en la parte inferior

*aquí va foto del bloque de sueldo líquido destacado al fondo de la pantalla*

Para volver al listado, haz clic en **"← Volver a mis recibos"**.

---

### Cerrar Sesión

Haz clic en el botón **"Cerrar sesión"** en la barra de navegación superior.

*aquí va foto de la barra de navegación con el botón de cerrar sesión*

El sistema cerrará tu sesión y te redirigirá automáticamente al login. Tu token JWT expira en 24 horas — si cierras el navegador sin cerrar sesión, deberás volver a ingresar tus credenciales al día siguiente.

---

### Seguridad

- Tu sesión está protegida mediante un token JWT que expira en 24 horas.
- Las contraseñas nunca se almacenan en texto plano — solo se guarda un hash SHA-256.
- Cada recibo está vinculado a tu usuario: aunque alguien conozca el ID de un recibo ajeno, el sistema se lo denegará con un error 403.
- Si el token expira mientras navegas, el sistema te redirige automáticamente al login.
- Nunca compartas tu contraseña con otras personas.
