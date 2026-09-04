package com.inventario.repository;

import com.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByStockActualLessThan(Integer cantidad);
    List<Producto> findByCategoriaId(Long categoriaId);
}
