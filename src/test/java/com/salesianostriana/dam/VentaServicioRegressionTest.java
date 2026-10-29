package com.salesianostriana.dam;

import com.salesianostriana.dam.model.*;
import com.salesianostriana.dam.repos.*;
import com.salesianostriana.dam.service.VentaServicio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServicioRegressionTest {
    @Mock ProductoRepositorio products;
    @Mock VentaRepositorio sales;
    @InjectMocks VentaServicio service;

    @Test void removesEveryMatchingLineUsingIdValueAndDetachesBothSides() {
        Long storedId = Long.valueOf("1000");
        Long requestedId = Long.valueOf("1000");
        assertNotSame(storedId, requestedId);
        Producto product = Producto.builder().id(storedId).precio(2.5).build();
        LineaDeVenta first = LineaDeVenta.builder().producto(product).cantidad(1).pvp(2.5).build();
        LineaDeVenta second = LineaDeVenta.builder().producto(product).cantidad(2).pvp(2.5).build();
        Venta sale = new Venta();
        sale.addLineaVenta(first); sale.addLineaVenta(second);
        when(sales.findById(8L)).thenReturn(Optional.of(sale));
        assertSame(sale, service.removeLineaVenta(8L, requestedId));
        assertTrue(sale.getLineasDeVenta().isEmpty());
        assertNull(first.getVenta()); assertNull(second.getVenta());
        verify(sales).save(sale);
        verifyNoInteractions(products);
    }

    @Test void nonPositiveQuantityIsRejectedBeforeReadingOrWriting() {
        assertThrows(IllegalArgumentException.class, () -> service.addProductoToVenta(8L, 1L, 0));
        assertThrows(IllegalArgumentException.class, () -> service.addProductoToVenta(8L, 1L, -2));
        verifyNoInteractions(products, sales);
    }

    @Test void incompleteNewSaleIsNotSavedWhenAProductIsMissing() {
        Map<Long,Integer> requested = new LinkedHashMap<>();
        requested.put(1L, 2); requested.put(2L, 1);
        when(products.findById(1L)).thenReturn(Optional.of(Producto.builder().id(1L).precio(2.5).build()));
        when(products.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.nuevaVenta(requested, new Cliente()));
        verifyNoInteractions(sales);
    }

    @Test void invalidNewSaleQuantityDoesNotReachTheRepository() {
        assertThrows(IllegalArgumentException.class, () -> service.nuevaVenta(Map.of(1L, -1), new Cliente()));
        verifyNoInteractions(products, sales);
    }
}
