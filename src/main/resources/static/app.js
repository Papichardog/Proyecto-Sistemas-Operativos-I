// Funciones compartidas por los 3 dashboards.
// Se incluye con <script src="app.js"></script> antes del script propio
// de cada página.

// Vacío = ruta relativa al mismo origen desde donde se cargó la página.
// Funciona tanto si abrís http://localhost:8080/login.html (Spring Boot
// directo) como http://localhost:8081/login.html (a través de Nginx).
const API_BASE = "";

// Lee la sesión guardada por login.html. Si no existe, manda de vuelta
// al login — así ninguna pantalla queda accesible sin haber iniciado sesión.
function obtenerSesion() {
    const authHeader = sessionStorage.getItem("authHeader");
    const usuarioJson = sessionStorage.getItem("usuario");
    if (!authHeader || !usuarioJson) {
        window.location.href = "login.html";
        return null;
    }
    return { authHeader, usuario: JSON.parse(usuarioJson) };
}

// Wrapper de fetch que agrega automáticamente el header Authorization.
// Si el backend responde 401 (sesión inválida o expiró), limpia todo
// y manda al login en vez de dejar la pantalla en un estado raro.
async function llamadaApi(path, opciones = {}) {
    const sesion = obtenerSesion();
    if (!sesion) return null;

    const respuesta = await fetch(`${API_BASE}${path}`, {
        ...opciones,
        headers: {
            "Authorization": sesion.authHeader,
            "Content-Type": "application/json",
            ...(opciones.headers || {})
        }
    });

    if (respuesta.status === 401) {
        sessionStorage.clear();
        window.location.href = "login.html";
        return null;
    }

    return respuesta;
}

function cerrarSesion() {
    sessionStorage.clear();
    window.location.href = "login.html";
}

// Evita que texto ingresado por el usuario rompa el HTML al insertarlo
// con innerHTML (por ejemplo, un motivo de consulta con "<" adentro).
function escaparHtml(texto) {
    const div = document.createElement("div");
    div.textContent = texto ?? "";
    return div.innerHTML;
}

function formatearFecha(fechaIso) {
    if (!fechaIso) return "";
    const [anio, mes, dia] = fechaIso.split("-");
    return `${dia}/${mes}/${anio}`;
}

function formatearHora(horaIso) {
    if (!horaIso) return "";
    return horaIso.substring(0, 5);
}

// Llena el encabezado (nombre, rol, botón de salir) si esos elementos
// existen en la página. Cada dashboard solo necesita tener los ids
// "nombre-usuario", "rol-usuario" y "boton-salir" en su HTML.
function iniciarEncabezado() {
    const sesion = obtenerSesion();
    if (!sesion) return;

    const nombreEl = document.getElementById("nombre-usuario");
    const rolEl = document.getElementById("rol-usuario");
    const botonSalir = document.getElementById("boton-salir");

    if (nombreEl) nombreEl.textContent = sesion.usuario.nombre;
    if (rolEl) rolEl.textContent = sesion.usuario.rol;
    if (botonSalir) botonSalir.addEventListener("click", cerrarSesion);
}

document.addEventListener("DOMContentLoaded", iniciarEncabezado);