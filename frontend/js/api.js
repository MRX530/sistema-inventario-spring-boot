// Centraliza las llamadas a la API para no repetir fetch() en cada pagina
const API_BASE = 'http://localhost:8080/api';

async function apiGet(path) {
    const res = await fetch(API_BASE + path);
    if (!res.ok) throw new Error('Error en la peticion');
    return res.json();
}

async function apiPost(path, body) {
    const res = await fetch(API_BASE + path, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
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
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (!res.ok) throw new Error('Error en la peticion');
    return res.json();
}

async function apiDelete(path) {
    const res = await fetch(API_BASE + path, { method: 'DELETE' });
    if (!res.ok) throw new Error('Error en la peticion');
}
