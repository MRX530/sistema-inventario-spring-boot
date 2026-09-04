package com.inventario.repository;

import com.inventario.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByProductoId(Long productoId);
    List<Movimiento> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
}
