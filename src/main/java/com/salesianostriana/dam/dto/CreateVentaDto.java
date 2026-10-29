package com.salesianostriana.dam.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Value
@Builder
@Jacksonized
public class CreateVentaDto {

    private List<ProductoCantidadDto> productos;
    private Long clienteId;


}
