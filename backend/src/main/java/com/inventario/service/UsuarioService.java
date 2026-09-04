package com.inventario.service;

import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario guardar(Usuario usuario) {
        // encode() aplica BCrypt: la misma contraseña "1234" genera un hash
        // distinto cada vez (usa "sal" aleatoria), pero matches() igual la reconoce.
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    // Devuelve el usuario si las credenciales son correctas.
    // matches() compara el password en texto plano contra el hash guardado
    // (nunca se compara hash contra hash, ni se "desencripta" nada).
    public Usuario login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmail(email);
        if (usuario == null || !passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Credenciales invalidas");
        }
        return usuario;
    }
}
