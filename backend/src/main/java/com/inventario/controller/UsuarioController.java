package com.inventario.controller;

import com.inventario.config.JwtUtil;
import com.inventario.model.Usuario;
import com.inventario.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listarTodos();
    }

    // Nota: la ruta la llamamos "registro" (no POST /api/usuarios) para que
    // coincida con la excepcion configurada en SecurityConfig como publica.
    @PostMapping("/registro")
    public Usuario crear(@RequestBody Usuario usuario) {
        return usuarioService.guardar(usuario);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody Map<String, String> body) {
        Usuario usuario = usuarioService.login(body.get("email"), body.get("password"));
        String token = jwtUtil.generarToken(usuario.getEmail());
        return LoginResponse.desde(usuario, token);
    }
}
