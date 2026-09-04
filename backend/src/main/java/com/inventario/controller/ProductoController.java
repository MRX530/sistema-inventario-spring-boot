package com.inventario.controller;

import com.inventario.model.Producto;
import com.inventario.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // permite que el frontend (otro puerto/dominio) consuma la API
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // Se mantiene por compatibilidad con lo que ya usa el frontend actual
    @GetMapping
    public List<Producto> listar() {
        return productoService.listarTodos();
    }

    // Version paginada: /api/productos/pagina?page=0&size=10&sort=nombre
    // page: numero de pagina (empieza en 0)
    // size: cuantos productos por pagina
    // sort: por que campo ordenar
    @GetMapping("/pagina")
    public Page<Producto> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        return productoService.listarPaginado(pageable);
    }

    @GetMapping("/{id}")
    public Producto buscarPorId(@PathVariable Long id) {
        return productoService.buscarPorId(id);
    }

    @GetMapping("/stock-bajo")
    public List<Producto> stockBajo() {
        return productoService.listarStockBajo();
    }

    @PostMapping
    public Producto crear(@RequestBody Producto producto) {
        return productoService.guardar(producto);
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        producto.setId(id);
        return productoService.guardar(producto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}
