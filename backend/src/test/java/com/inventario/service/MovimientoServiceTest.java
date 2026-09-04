package com.inventario.service;

import com.inventario.model.*;
import com.inventario.repository.MovimientoRepository;
import com.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class): activa Mockito para esta clase de test.
// No se levanta Spring completo ni se conecta a MySQL: se prueban las clases
// Java "puras", con los repositorios simulados (@Mock).
@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private ProductoRepository productoRepository;

    // @InjectMocks crea un MovimientoService real, pero le "inyecta" los
    // repositorios falsos de arriba en vez de los reales.
    @InjectMocks
    private MovimientoService movimientoService;

    private Producto producto;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Mouse inalambrico");
        producto.setStockActual(10);
        producto.setStockMinimo(5);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Admin");
    }

    @Test
    void salidaConStockSuficiente_descuentaElStock() {
        // Arrange: cuando el service pida el producto 1, Mockito le devuelve nuestro producto simulado
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Movimiento resultado = movimientoService.registrarMovimiento(
                1L, 1L, TipoMovimiento.SALIDA, 4, usuario);

        // Assert: el stock bajo de 10 a 6, y el movimiento quedo bien registrado
        assertEquals(6, producto.getStockActual());
        assertEquals(TipoMovimiento.SALIDA, resultado.getTipo());
        assertEquals(4, resultado.getCantidad());
        verify(productoRepository).save(producto);
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    void salidaConStockInsuficiente_lanzaExcepcionYNoModificaNada() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        // El producto tiene 10 de stock, pedimos sacar 50 -> debe fallar
        StockInsuficienteException ex = assertThrows(StockInsuficienteException.class, () ->
                movimientoService.registrarMovimiento(1L, 1L, TipoMovimiento.SALIDA, 50, usuario)
        );

        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        // Clave: si la validacion fallo, NUNCA debe llegar a guardar nada
        verify(productoRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void entrada_sumaElStock() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(inv -> inv.getArgument(0));

        Movimiento resultado = movimientoService.registrarMovimiento(
                1L, 1L, TipoMovimiento.ENTRADA, 20, usuario);

        assertEquals(30, producto.getStockActual()); // 10 + 20
        assertEquals(TipoMovimiento.ENTRADA, resultado.getTipo());
    }

    @Test
    void productoInexistente_lanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                movimientoService.registrarMovimiento(99L, 1L, TipoMovimiento.ENTRADA, 5, usuario)
        );
    }
}
