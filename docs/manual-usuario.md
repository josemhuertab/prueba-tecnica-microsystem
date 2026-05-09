# Manual de Usuario — Portal de Recibos Microsystem

## ¿Qué es este portal?

El Portal de Recibos de Pago permite a los colaboradores de la empresa XYZ consultar sus últimos 20 recibos de pago de forma segura desde cualquier navegador web.

El portal incluye **modo oscuro** activable con un botón en la barra superior, y un panel de filtros para buscar recibos por período, año o sueldo mínimo.

---

## Pantalla de Inicio de Sesión

Al ingresar a `http://localhost:5173`, verás el formulario de login.

### Cómo ingresar

1. **Nombre de usuario:** Ingresa tu usuario en formato UpperCamelCase.
   - Ejemplo correcto: `JuanPerezDelCampo001`
   - El usuario debe tener al menos 15 caracteres y terminar con 3 dígitos.

2. **Contraseña:** Ingresa tu contraseña. Puedes usar el ícono 👁️ para mostrarla u ocultarla.

3. Haz clic en **Ingresar**.

## Listado de Recibos

Tras iniciar sesión, verás una tabla con tus **últimos 20 recibos de pago**, ordenados del más reciente al más antiguo.

### Panel de filtros

Encima de la tabla encontrarás un panel con tres filtros:

| Filtro | Descripción |
|--------|-------------|
| Buscar período | Filtra por texto parcial, ej: `04-2025` |
| Año | Desplegable con los años disponibles |
| Sueldo líquido mínimo | Muestra solo recibos con sueldo líquido mayor al valor ingresado |

El botón **"✕ Limpiar filtros"** restablece todos los filtros a la vez.

También puedes hacer clic en los encabezados de columna para **ordenar** la tabla de forma ascendente o descendente.

### Columnas de la tabla

| Columna | Descripción |
|---------|-------------|
| N° Recibo | Número identificador del recibo |
| Período | Mes y año al que corresponde el pago |
| Fecha de Pago | Fecha en que se realizó el depósito |
| Sueldo Base | Remuneración base del período |
| Bono Producción | Bono adicional por desempeño |
| Sueldo Líquido | Monto final depositado en cuenta |

> Todos los montos se muestran en pesos chilenos (CLP) con separador de miles.

### Ver el detalle de un recibo

Haz clic en cualquier fila de la tabla o en el botón **"Ver detalle"** para acceder al desglose completo del recibo.

---

## Detalle del Recibo

Esta pantalla muestra el desglose completo del recibo seleccionado:

- **Haberes:** Sueldo base y bono de producción
- **Descuentos:** Salud, AFP y otros descuentos
- **Sueldo Líquido:** Monto final a pagar (destacado en la parte inferior)

Para volver al listado, haz clic en **"← Volver a mis recibos"**.

---

## Modo oscuro

El portal incluye un modo oscuro. Para activarlo o desactivarlo, haz clic en el botón 🌙 / ☀️ ubicado en la barra de navegación superior (cuando estás dentro del portal) o en la esquina superior derecha de la pantalla de login.

La preferencia se guarda automáticamente en el navegador.

---

## Cerrar Sesión

Haz clic en el botón **"Cerrar sesión"** en la barra superior derecha.

El sistema invalidará tu sesión y te redirigirá automáticamente al login. Nadie más podrá acceder a tus recibos sin tus credenciales.

---

## Seguridad

- Tu sesión está protegida mediante un token JWT que expira en 24 horas.
- Si cierras el navegador sin cerrar sesión, deberás volver a ingresar tus credenciales.
- Nunca compartas tu contraseña con otras personas.
