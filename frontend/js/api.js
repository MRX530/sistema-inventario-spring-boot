// Centraliza las llamadas a la API para no repetir fetch() en cada pagina
const API_BASE = 'http://localhost:8080/api';

// Arma los headers comunes, agregando el token JWT si existe
// (login no lo necesita todavia, por eso se guarda recien despues de loguearse)
function headers() {
    const base = { 'Content-Type': 'application/json' };
    const token = sessionStorage.getItem('token');
    if (token) base['Authorization'] = 'Bearer ' + token;
    return base;
}

async function apiGet(path) {
    const res = await fetch(API_BASE + path, { headers: headers() });
    if (!res.ok) throw new Error('Error en la peticion');
    return res.json();
}

async function apiPost(path, body) {
    const res = await fetch(API_BASE + path, {
        method: 'POST',
        headers: headers(),
        body: JSON.stringify(body)
    });
    if (!res.ok) {
        const err = await res.json();
        throw new Error(err.error || 'Error en la peticion');
    }
    return res.json();
}

async function apiPut(path, body) {
    const res = await fetch(API_BASE + path, {
        method: 'PUT',
        headers: headers(),
        body: JSON.stringify(body)
    });
    if (!res.ok) throw new Error('Error en la peticion');
    return res.json();
}

async function apiDelete(path) {
    const res = await fetch(API_BASE + path, { method: 'DELETE', headers: headers() });
    if (!res.ok) throw new Error('Error en la peticion');
}
