package com.salesianostriana.dam.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ProductoCantidadDto {

    private Long idProducto;
    private int cantidad;

}
