package com.salesianostriana.dam.service;

import com.salesianostriana.dam.model.*;
import com.salesianostriana.dam.repos.ProductoRepositorio;
import com.salesianostriana.dam.repos.VentaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class VentaServicio {
    private final ProductoRepositorio productoRepositorio;
    private final VentaRepositorio ventaRepositorio;

    public Venta nuevaVenta(Map<Long, Integer> productos, Cliente cliente) {
        if (cliente == null || productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("invalid_sale");
        }
        productos.forEach((id, quantity) -> {
            if (id == null || quantity == null) throw new IllegalArgumentException("invalid_product");
            requirePositiveQuantity(quantity);
        });
        Venta venta = new Venta();
        venta.setCliente(cliente);
        productos.forEach((id, quantity) -> {
            Producto product = productoRepositorio.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("product_not_found"));
            venta.addLineaVenta(LineaDeVenta.builder().producto(product)
                    .pvp(product.getPrecio()).cantidad(quantity).build());
        });
        ventaRepositorio.save(venta);
        return venta;
    }

    public Venta addProductoToVenta(Long idVenta, Long codigoProducto, int cantidad) {
        requirePositiveQuantity(cantidad);
        Venta venta = ventaRepositorio.findById(idVenta).orElse(null);
        if (venta == null) return null;
        Producto product = productoRepositorio.findById(codigoProducto).orElse(null);
        if (product == null) return null;
        venta.addLineaVenta(LineaDeVenta.builder().producto(product)
                .cantidad(cantidad).pvp(product.getPrecio()).build());
        ventaRepositorio.save(venta);
        return venta;
    }

    public Venta removeLineaVenta(Long idVenta, Long idProducto) {
        Venta venta = ventaRepositorio.findById(idVenta).orElse(null);
        if (venta == null) return null;
        List<LineaDeVenta> matching = venta.getLineasDeVenta().stream()
                .filter(line -> Objects.equals(line.getProducto().getId(), idProducto)).toList();
        if (matching.isEmpty()) return null;
        matching.forEach(venta::removeLineaVenta);
        ventaRepositorio.save(venta);
        return venta;
    }

    private void requirePositiveQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("invalid_quantity");
    }
}
