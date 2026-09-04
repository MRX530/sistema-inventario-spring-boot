package com.inventario.controller;

import com.inventario.model.Movimiento;
import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import com.inventario.service.MovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin(origins = "*")
public class MovimientoController {

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public Movimiento registrar(@RequestBody MovimientoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return movimientoService.registrarMovimiento(
                request.getProductoId(),
                request.getUsuarioId(),
                request.getTipo(),
                request.getCantidad(),
                usuario
        );
    }

    @GetMapping("/producto/{productoId}")
    public List<Movimiento> historialDeProducto(@PathVariable Long productoId) {
        return movimientoService.historialPorProducto(productoId);
    }

    @GetMapping("/reporte")
    public List<Movimiento> reporte(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return movimientoService.reportePorRangoFechas(inicio, fin);
    }
}
