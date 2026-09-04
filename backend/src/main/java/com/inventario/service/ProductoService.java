package com.inventario.service;

import com.inventario.model.Producto;
import com.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    // Pageable ya trae encapsulado: numero de pagina, tamaño de pagina y orden.
    // JpaRepository.findAll(Pageable) traduce esto a "LIMIT x OFFSET y" en SQL,
    // en vez de traer TODOS los productos y cortar la lista en Java (ineficiente
    // con miles de registros).
    public Page<Producto> listarPaginado(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
    }

    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    // Regla de negocio: productos por debajo de su stock minimo
    public List<Producto> listarStockBajo() {
        return productoRepository.findAll().stream()
                .filter(p -> p.getStockActual() < p.getStockMinimo())
                .toList();
    }
}
