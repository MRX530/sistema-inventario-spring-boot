package com.inventario.service;

import com.inventario.model.*;
import com.inventario.repository.MovimientoRepository;
import com.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoService {

    @Autowired
    private MovimientoRepository movimientoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Registra un movimiento de ENTRADA o SALIDA y actualiza el stock del producto.
     * Esta es la regla de negocio mas importante del sistema:
     * - ENTRADA: suma al stock actual
     * - SALIDA: resta del stock, pero primero valida que haya suficiente
     * Se marca @Transactional porque se hacen 2 escrituras (movimiento + producto)
     * y deben ocurrir juntas: si una falla, la otra se revierte.
     */
    @Transactional
    public Movimiento registrarMovimiento(Long productoId, Long usuarioId, TipoMovimiento tipo, Integer cantidad, Usuario usuario) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (tipo == TipoMovimiento.SALIDA) {
            if (producto.getStockActual() < cantidad) {
                throw new StockInsuficienteException(
                        "Stock insuficiente. Disponible: " + producto.getStockActual() + ", solicitado: " + cantidad);
            }
            producto.setStockActual(producto.getStockActual() - cantidad);
        } else {
            producto.setStockActual(producto.getStockActual() + cantidad);
        }

        productoRepository.save(producto);

        Movimiento movimiento = new Movimiento();
        movimiento.setProducto(producto);
        movimiento.setUsuario(usuario);
        movimiento.setTipo(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setFecha(LocalDateTime.now());

        return movimientoRepository.save(movimiento);
    }

    public List<Movimiento> historialPorProducto(Long productoId) {
        return movimientoRepository.findByProductoId(productoId);
    }

    // Para el reporte: movimientos entre dos fechas (ej: "este mes")
    public List<Movimiento> reportePorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return movimientoRepository.findByFechaBetween(inicio, fin);
    }
}
