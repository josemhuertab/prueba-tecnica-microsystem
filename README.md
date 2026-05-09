# Portal de Recibos de Pago — Microsystem

Portal web para que los colaboradores de la empresa XYZ visualicen sus últimos 20 recibos de pago.

## Stack tecnológico

| Capa       | Tecnología                        |
|------------|-----------------------------------|
| Frontend   | Vue.js 3 + Vite                   |
| Backend    | Java 17 + Spring Boot 3.2         |
| Base de datos | SQLite (embebida, sin servidor) |
| Autenticación | JWT (JSON Web Token)           |

## Estructura del proyecto

```
microsystem-portal/
├── backend/          # API REST en Spring Boot
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
│   │   ├── assets/   # CSS global
│   │   ├── components/  # NavBar
│   │   ├── router/   # Rutas y guards
│   │   ├── services/ # Llamadas a la API
│   │   └── views/    # Login, Recibos, Detalle
│   ├── index.html
│   └── package.json
├── data/             # Archivos CSV originales
└── docs/             # Manuales de instalación y usuario
```

## Inicio rápido

Ver [docs/manual-instalacion.md](docs/manual-instalacion.md) para instrucciones detalladas.

```bash
# Backend
cd backend
mvn spring-boot:run

# Frontend (en otra terminal)
cd frontend
npm install
npm run dev
```

Accede en: http://localhost:5173

## Credenciales de prueba

Provienen del archivo `Usuarios.csv` entregado con el ejercicio. El hash se genera como `SHA-256(username:password)`.

| Usuario                  | Contraseña    |
|--------------------------|---------------|
| JuanPerezDelCampo001     | password123   |
| MariaLopezContreras002   | 1234secure    |
| CarlosRodriguezTercero003| abcDEF456     |
