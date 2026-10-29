package com.salesianostriana.dam.controller;

import com.salesianostriana.dam.dto.*;
import com.salesianostriana.dam.model.Cliente;
import com.salesianostriana.dam.repos.ClienteRepositorio;
import com.salesianostriana.dam.service.VentaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class VentaController {
    private final VentaServicio ventaServicio;
    private final ClienteRepositorio clienteRepositorio;
    private final VentaConverter ventaConverter;

    @PostMapping({"/", "/ventas"})
    public ResponseEntity<GetVentaDto> nuevaVenta(@RequestBody CreateVentaDto request) {
        if (request.getClienteId() == null || request.getProductos() == null) {
            throw new IllegalArgumentException("invalid_sale");
        }
        Map<Long,Integer> products = new LinkedHashMap<>();
        for (ProductoCantidadDto product : request.getProductos()) {
            if (product == null || product.getIdProducto() == null
                    || products.putIfAbsent(product.getIdProducto(), product.getCantidad()) != null) {
                throw new IllegalArgumentException("invalid_product");
            }
        }
        Cliente customer = clienteRepositorio.findById(request.getClienteId())
                .orElseThrow(() -> new NoSuchElementException("customer_not_found"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventaConverter.ventaToGetVentaDto(ventaServicio.nuevaVenta(products, customer)));
    }
}
