# Guía de Presentación — Portal de Recibos Microsystem

> **Cómo leer este documento:**
> Cada bloque tiene dos partes:
> - **🗣️ Qué decir** — el texto que puedes decir en voz alta, con tus palabras
> - **🖥️ Qué mostrar** — exactamente qué abrir, dónde hacer clic o qué archivo señalar

---

## PARTE 1 — Introducción (1 minuto)

**🗣️ Qué decir:**
> "El proyecto es un Portal de Recibos de Pago para los colaboradores de Microsystem.
> La idea es simple: cada empleado puede iniciar sesión y consultar sus últimos recibos de sueldo.
> Lo construí como una aplicación fullstack separada en dos partes independientes:
> un backend en Java con Spring Boot que expone una API REST,
> y un frontend en Vue 3 que consume esa API.
> No usé ninguna base de datos externa — todo corre con SQLite embebido,
> así que basta con clonar el proyecto y ejecutarlo."

**🖥️ Qué mostrar:**
Muestra la estructura de carpetas en el explorador de archivos o en el editor:
```
prueba-tecnica-microsystem/
├── backend/     ← Java + Spring Boot
├── frontend/    ← Vue 3 + Vite
├── data/        ← CSVs originales
└── docs/        ← Manuales
```

---

## PARTE 2 — Stack tecnológico (1 minuto)

**🗣️ Qué decir:**
> "Para el backend elegí Spring Boot 3 con Java 17 porque es el estándar en el mundo empresarial.
> La seguridad la implementé con Spring Security en modo stateless usando JWT —
> sin cookies, sin sesiones del servidor, el token viaja en cada petición.
> La base de datos es SQLite, que es un archivo embebido, lo que hace que el proyecto
> sea muy fácil de instalar sin depender de un servidor de base de datos externo.
>
> Para el frontend usé Vue 3 con la Composition API y Vite como bundler.
> No usé ningún framework CSS externo como Bootstrap o Tailwind —
> todo el diseño lo hice con variables CSS propias, lo que me dio control total sobre el tema oscuro."

**🖥️ Qué mostrar:**
Abre `backend/pom.xml` y señala estas dependencias mientras las mencionas:
- `spring-boot-starter-security`
- `jjwt-api` (versión 0.12.5)
- `sqlite-jdbc`
- `opencsv`

Luego abre `frontend/package.json` y señala:
- `vue: ^3.4.21`
- `vue-router`
- `axios`

---

## PARTE 3 — Demo en vivo: el flujo completo (3-4 minutos)

> Antes de empezar, asegúrate de tener el backend y el frontend corriendo.
> Backend: `mvn spring-boot:run` en la carpeta `backend/`
> Frontend: `npm run dev` en la carpeta `frontend/`

### 3.1 — Pantalla de Login

**🗣️ Qué decir:**
> "La primera pantalla es el login. Noten que hay un hint abajo que explica el formato del usuario —
> esto lo pedía la especificación: el username debe empezar con mayúscula,
> tener al menos 15 caracteres y terminar con exactamente 3 dígitos.
> Implementé validación tanto en el frontend como en el backend,
> así que si alguien intenta saltarse el frontend, el backend igual rechaza el request."

**🖥️ Qué mostrar:**
1. Abre `http://localhost:5173` en el navegador
2. Intenta enviar el formulario vacío → muestra los errores de campo
3. Escribe un username inválido como `juan001` → muestra el mensaje de formato
4. Muestra el botón 🌙 en la esquina y activa el modo oscuro

**🗣️ Qué decir (modo oscuro):**
> "Agregué modo oscuro como detalle extra. El estado se guarda en localStorage
> y se aplica con un atributo `data-theme` en el HTML raíz,
> así persiste entre recargas de página."

### 3.2 — Login exitoso

**🗣️ Qué decir:**
> "Ahora ingreso con credenciales válidas. El backend valida el formato del username,
> busca al usuario en la base de datos, y compara el hash SHA-256 de la contraseña.
> Si todo coincide, devuelve un JWT con 24 horas de vigencia."

**🖥️ Qué mostrar:**
1. Ingresa con `JuanPerezDelCampo001` / `password123`
2. Haz clic en "Ingresar al portal"
3. Abre las DevTools del navegador (F12) → pestaña Network → busca la llamada a `/api/auth/login`
4. Muestra el response: `{ "token": "eyJ...", "username": "JuanPerezDelCampo001" }`
5. Ve a Application → Local Storage → muestra que el token quedó guardado

### 3.3 — Lista de recibos

**🗣️ Qué decir:**
> "Después del login, el usuario ve sus últimos 20 recibos ordenados del más reciente al más antiguo.
> Implementé tres filtros: por período, por año y por sueldo mínimo.
> También se puede ordenar por cualquier columna haciendo clic en el encabezado."

**🖥️ Qué mostrar:**
1. Muestra la tabla con los recibos cargados
2. Haz clic en el encabezado "Sueldo Líquido" → muestra el ordenamiento ascendente/descendente
3. Escribe `2024` en el filtro de período → muestra cómo se filtra en tiempo real
4. Usa el filtro de año → selecciona un año del dropdown
5. Haz clic en "Limpiar filtros"

**🗣️ Qué decir (sobre los filtros):**
> "Los filtros son una `computed` de Vue que se recalcula automáticamente cada vez que cambia
> cualquier filtro. No hago ninguna llamada adicional al backend — todo el filtrado
> es del lado del cliente sobre los 20 recibos que ya tengo en memoria."

### 3.4 — Detalle del recibo

**🗣️ Qué decir:**
> "Al hacer clic en cualquier fila, voy al detalle del recibo.
> Aquí se muestra el desglose completo: haberes, descuentos y el sueldo líquido final destacado."

**🖥️ Qué mostrar:**
1. Haz clic en cualquier fila de la tabla
2. Señala la tarjeta de Haberes (verde) y la de Descuentos (rojo)
3. Señala el sueldo líquido destacado al fondo
4. Haz clic en "← Volver a mis recibos"

---

## PARTE 4 — Arquitectura y seguridad (2-3 minutos)

**🗣️ Qué decir:**
> "Quiero mostrarles cómo funciona la seguridad por dentro, porque es la parte más importante del proyecto."

### 4.1 — El hash de contraseñas

**🗣️ Qué decir:**
> "Las contraseñas nunca se guardan en texto plano.
> La especificación venía con un archivo Python que mostraba cómo generar el hash.
> El algoritmo es SHA-256 aplicado sobre la cadena 'username:password'.
> Repliqué exactamente esa lógica en Java."

**🖥️ Qué mostrar:**
Abre `data/login_hash_example.py` y señala:
```python
combined = f"{username}:{pwd}"
hash_value = hashlib.sha256(combined.encode('utf-8')).hexdigest()
```
Luego abre `backend/src/main/java/.../service/AuthService.java` y señala el método `generarHash()`:
```java
String combined = username + ":" + password;
MessageDigest digest = MessageDigest.getInstance("SHA-256");
byte[] hashBytes = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
return HexFormat.of().formatHex(hashBytes);
```
> "Son exactamente lo mismo, solo que en Java."

### 4.2 — El filtro JWT

**🗣️ Qué decir:**
> "Cada petición al backend pasa por mi JwtFilter antes de llegar al controlador.
> El filtro lee el header Authorization, valida la firma del token,
> y si es válido registra al usuario en el contexto de seguridad de Spring.
> Los controladores simplemente reciben el parámetro `Authentication auth`
> y llaman a `auth.getName()` para saber quién está haciendo la petición."

**🖥️ Qué mostrar:**
Abre `backend/src/main/java/.../security/JwtFilter.java` y señala:
```java
String token = authHeader.substring(7);
if (jwtUtil.isTokenValid(token)) {
    String username = jwtUtil.extractUsername(token);
    UsernamePasswordAuthenticationToken auth = ...
    SecurityContextHolder.getContext().setAuthentication(auth);
}
```
Luego abre `ReciboController.java` y señala:
```java
public ResponseEntity<List<ReciboPago>> listarRecibos(Authentication auth) {
    String username = auth.getName();
```
> "El controlador no sabe nada de JWT — solo pregunta quién es el usuario autenticado."

### 4.3 — El ownership check (punto clave de seguridad)

**🗣️ Qué decir:**
> "Este es el punto de seguridad más importante del proyecto.
> Cuando alguien pide el detalle de un recibo por ID,
> no basta con que el ID exista — también verifico que ese recibo pertenezca al usuario del token.
> Si Juan conoce el ID de un recibo de María e intenta acceder, recibe un 403.
> Esto se llama IDOR — Insecure Direct Object Reference — y es uno de los errores
> más comunes en APIs REST."

**🖥️ Qué mostrar:**
Abre `backend/src/main/java/.../repository/ReciboPagoRepository.java` y señala:
```java
Optional<ReciboPago> findByIdAndUsername(Long id, String username);
```
Luego abre `ReciboService.java` y señala:
```java
return reciboPagoRepository.findByIdAndUsername(id, username)
    .orElseThrow(() -> new SecurityException("Recibo no encontrado o no autorizado."));
```
> "Con una sola consulta verifico tanto que el recibo exista como que sea del usuario correcto."

---

## PARTE 5 — Carga de datos desde CSV (1 minuto)

**🗣️ Qué decir:**
> "Los datos vienen de dos archivos CSV que se importan automáticamente al arrancar la aplicación.
> Usé la anotación `@PostConstruct` para que el método se ejecute una vez que Spring
> termina de inicializar el componente.
> Antes de insertar, verifico si ya hay datos en la tabla para no duplicar en reinicios."

**🖥️ Qué mostrar:**
Abre `backend/src/main/java/.../service/DataLoaderService.java` y señala:
```java
@PostConstruct
public void cargarDatos() {
    cargarUsuarios();
    cargarRecibos();
}

private void cargarUsuarios() {
    if (usuarioRepository.count() > 0) return; // Ya están cargados, no inserto de nuevo
```

---

## PARTE 6 — Tests unitarios (2 minutos)

**🗣️ Qué decir:**
> "Como plus, agregué 15 tests unitarios organizados en 3 clases.
> Quiero mostrarles los más interesantes."

### 6.1 — Ejecutar los tests

**🖥️ Qué mostrar:**
Abre una terminal en la carpeta `backend/` y ejecuta:
```bash
mvn test -Dtest="AuthServiceTest,JwtUtilTest,ReciboServiceTest"
```
Espera el resultado y señala:
```
Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 6.2 — AuthServiceTest

**🗣️ Qué decir:**
> "En AuthServiceTest pruebo la lógica de autenticación de forma aislada.
> Uso Mockito para reemplazar el repositorio y JwtUtil con objetos falsos que yo controlo.
> Así el test no necesita base de datos ni Spring — corre en milisegundos."

**🖥️ Qué mostrar:**
Abre `AuthServiceTest.java` y señala:
- La anotación `@Mock` sobre el repositorio y JwtUtil
- El `@BeforeEach` donde generas el hash SHA-256 igual que el servicio real
- El test `login_passwordIncorrecta_lanzaSecurityException` y explica:

> "Aquí verifico que si la contraseña es incorrecta, el servicio lanza SecurityException
> y además confirmo que `jwtUtil` nunca fue llamado — porque si la contraseña falla,
> no debería generarse ningún token."

### 6.3 — JwtUtilTest

**🗣️ Qué decir:**
> "JwtUtilTest no usa Mockito porque JwtUtil no tiene dependencias.
> El truco interesante aquí es el test del token vencido:
> creo una instancia con expiración de -1 milisegundo,
> así el token nace ya vencido y puedo verificar que sea rechazado."

**🖥️ Qué mostrar:**
Abre `JwtUtilTest.java` y señala el test `isTokenValid_tokenVencido_retornaFalse`:
```java
ReflectionTestUtils.setField(jwtUtilVencido, "expiration", -1L);
String tokenVencido = jwtUtilVencido.generateToken("JuanPerezDelCampo001");
assertThat(jwtUtil.isTokenValid(tokenVencido)).isFalse();
```

### 6.4 — ReciboServiceTest

**🗣️ Qué decir:**
> "El test más importante de los tres es el de IDOR en ReciboServiceTest.
> Simulo que María intenta acceder al recibo de Juan pasando el ID correcto
> pero con su propio token. El repositorio devuelve vacío porque la combinación
> id + username no coincide, y el servicio lanza SecurityException.
> Esto confirma que el ownership check funciona correctamente."

**🖥️ Qué mostrar:**
Abre `ReciboServiceTest.java` y señala el test `obtenerDetalle_usuarioNoPropietario_lanzaSecurityException`:
```java
when(reciboPagoRepository.findByIdAndUsername(1L, USUARIO_MARIA))
    .thenReturn(Optional.empty());

assertThatThrownBy(() -> reciboService.obtenerDetalle(1L, USUARIO_MARIA))
    .isInstanceOf(SecurityException.class)
    .hasMessageContaining("no autorizado");
```

---

## PARTE 7 — Cierre (30 segundos)

**🗣️ Qué decir:**
> "Para resumir: implementé autenticación JWT stateless, hash SHA-256 de contraseñas,
> control de acceso por propietario en cada recibo, carga automática desde CSV,
> filtros y ordenamiento del lado del cliente, modo oscuro persistente,
> y 15 tests unitarios que cubren los flujos críticos del backend.
> Todo corre sin instalar ninguna base de datos externa."

**🖥️ Qué mostrar:**
Vuelve al navegador con la aplicación corriendo — deja visible la pantalla de recibos
como imagen final.

---

## Preguntas frecuentes que te pueden hacer

**¿Por qué SQLite y no PostgreSQL o MySQL?**
> "Para una prueba técnica, SQLite es ideal porque no requiere instalación.
> En producción cambiaría el driver y el dialecto en `application.properties`
> y el resto del código no cambiaría nada — eso es la ventaja de JPA."

**¿Por qué no usaste BCrypt para el hash?**
> "La especificación de la prueba venía con un archivo Python que definía exactamente
> el algoritmo: SHA-256 de 'username:password'. Lo repliqué tal cual para que
> los datos del CSV sean compatibles. En un sistema real usaría BCrypt con salt."

**¿Qué pasa si el token expira mientras el usuario está navegando?**
> "El interceptor de Axios en el frontend detecta el 401 que devuelve el backend,
> limpia el localStorage y redirige automáticamente al login.
> El usuario no ve un error raro — simplemente vuelve a la pantalla de inicio de sesión."

**¿Por qué los filtros son del lado del cliente y no del backend?**
> "Porque el límite ya está en 20 recibos. Filtrar 20 elementos en memoria
> es instantáneo y evita llamadas innecesarias al servidor.
> Si el requisito fuera manejar cientos de recibos, lo movería al backend con parámetros de query."

**¿Qué es IDOR?**
> "Insecure Direct Object Reference — cuando una API permite acceder a un recurso
> solo con conocer su ID, sin verificar que pertenezca al usuario que lo pide.
> Lo previne usando `findByIdAndUsername` en el repositorio,
> que verifica ambas condiciones en una sola consulta."
