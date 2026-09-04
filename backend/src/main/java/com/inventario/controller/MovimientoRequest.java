package com.inventario.controller;

import com.inventario.model.TipoMovimiento;
import lombok.Data;

// DTO (Data Transfer Object): representa exactamente lo que el frontend
// envia al crear un movimiento, sin exponer las entidades completas.
@Data
public class MovimientoRequest {
    private Long productoId;
    private Long usuarioId;
    private TipoMovimiento tipo;
    private Integer cantidad;
}
