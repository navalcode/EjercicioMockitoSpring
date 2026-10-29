package com.salesianostriana.dam;

import com.salesianostriana.dam.model.*;
import com.salesianostriana.dam.repos.*;
import com.salesianostriana.dam.service.VentaServicio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Original eight coursework cases, with explicit state and persistence assertions. */
@ExtendWith(MockitoExtension.class)
class VentaServicioTest {
    @Mock ProductoRepositorio productoRepositorio;
    @Mock VentaRepositorio ventaRepositorio;
    @InjectMocks VentaServicio ventaServicio;

    private Producto product() {
        return Producto.builder().id(1L).nombre("Sample notebook").codigoProducto("DEMO-1").precio(2.5).build();
    }
    @Test void nuevaVentaSucces() {
        Producto product = product(); Cliente customer = new Cliente();
        when(productoRepositorio.findById(1L)).thenReturn(Optional.of(product));
        Venta sale = ventaServicio.nuevaVenta(Map.of(1L, 2), customer);
        assertSame(customer, sale.getCliente());
        assertEquals(1, sale.getLineasDeVenta().size());
        LineaDeVenta line = sale.getLineasDeVenta().get(0);
        assertSame(product, line.getProducto()); assertSame(sale, line.getVenta());
        assertEquals(2, line.getCantidad()); assertEquals(2.5, line.getPvp());
        verify(ventaRepositorio).save(sale);
    }
    @Test void ventaNuevaFailsWithNegativeProductId() {
        when(productoRepositorio.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> ventaServicio.nuevaVenta(Map.of(1L, 1), new Cliente()));
        verifyNoInteractions(ventaRepositorio);
    }
    @Test void addProductoToEmptyVentaReturnsNull() {
        when(ventaRepositorio.findById(3L)).thenReturn(Optional.empty());
        assertNull(ventaServicio.addProductoToVenta(3L, 1L, 1));
        verifyNoInteractions(productoRepositorio); verify(ventaRepositorio, never()).save(any());
    }
    @Test void addProductoToVentaWithEmptyProductReturnsNull() {
        Venta sale = new Venta();
        when(ventaRepositorio.findById(1L)).thenReturn(Optional.of(sale));
        when(productoRepositorio.findById(1L)).thenReturn(Optional.empty());
        assertNull(ventaServicio.addProductoToVenta(1L, 1L, 1));
        assertTrue(sale.getLineasDeVenta().isEmpty()); verify(ventaRepositorio, never()).save(any());
    }
    @Test void addProductoToVentaSucces() {
        Venta sale = new Venta(); Producto product = product();
        when(ventaRepositorio.findById(1L)).thenReturn(Optional.of(sale));
        when(productoRepositorio.findById(1L)).thenReturn(Optional.of(product));
        assertSame(sale, ventaServicio.addProductoToVenta(1L, 1L, 1));
        assertEquals(1, sale.getLineasDeVenta().size());
        LineaDeVenta line = sale.getLineasDeVenta().get(0);
        assertSame(product, line.getProducto()); assertSame(sale, line.getVenta());
        assertEquals(1, line.getCantidad()); assertEquals(2.5, line.getPvp());
        verify(ventaRepositorio).save(sale);
    }
    @Test void removeLineaVentaFromEmptyVentaReturnsNull() {
        when(ventaRepositorio.findById(3L)).thenReturn(Optional.empty());
        assertNull(ventaServicio.removeLineaVenta(3L, 1L));
        verify(ventaRepositorio, never()).save(any()); verifyNoInteractions(productoRepositorio);
    }
    @Test void removeLineaVentaWithEmptyProductReturnsNull() {
        Venta sale = new Venta();
        when(ventaRepositorio.findById(1L)).thenReturn(Optional.of(sale));
        assertNull(ventaServicio.removeLineaVenta(1L, 1L));
        assertTrue(sale.getLineasDeVenta().isEmpty()); verify(ventaRepositorio, never()).save(any());
    }
    @Test void removeLineaVentaSucces() {
        Venta sale = new Venta(); Producto product = product();
        LineaDeVenta first = LineaDeVenta.builder().producto(product).cantidad(1).pvp(2.5).build();
        LineaDeVenta second = LineaDeVenta.builder().producto(product).cantidad(1).pvp(2.5).build();
        sale.addLineaVenta(first); sale.addLineaVenta(second);
        when(ventaRepositorio.findById(1L)).thenReturn(Optional.of(sale));
        assertSame(sale, ventaServicio.removeLineaVenta(1L, 1L));
        assertTrue(sale.getLineasDeVenta().isEmpty());
        assertNull(first.getVenta()); assertNull(second.getVenta()); verify(ventaRepositorio).save(sale);
    }
}
