package com.salesianostriana.dam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesianostriana.dam.model.Cliente;
import com.salesianostriana.dam.model.Producto;
import com.salesianostriana.dam.repos.ClienteRepositorio;
import com.salesianostriana.dam.repos.ProductoRepositorio;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.Map;

@Configuration
public class DemoData {
    @Bean
    ApplicationRunner fictionalData(ClienteRepositorio customers, ProductoRepositorio products, ObjectMapper json) {
        return args -> {
            Cliente customer = customers.save(Cliente.builder().nombre("Demo customer")
                    .dni("DEMO").email("demo@example.invalid").build());
            Producto product = products.save(Producto.builder().nombre("Sample notebook")
                    .codigoProducto("DEMO-1").precio(2.5).build());
            System.out.println("DEMO_REQUEST=" + json.writeValueAsString(Map.of(
                    "clienteId", customer.getId(), "productos", List.of(Map.of("idProducto", product.getId(), "cantidad", 2)))));
        };
    }
}
