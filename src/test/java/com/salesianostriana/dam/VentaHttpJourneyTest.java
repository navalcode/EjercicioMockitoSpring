package com.salesianostriana.dam;

import com.salesianostriana.dam.model.*;
import com.salesianostriana.dam.repos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:sales_journey;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class VentaHttpJourneyTest {
    @Autowired MockMvc http;
    @Autowired ClienteRepositorio customers;
    @Autowired ProductoRepositorio products;
    @Autowired VentaRepositorio sales;
    Cliente customer;
    Producto product;

    @BeforeEach void prepareFictionalData() {
        sales.deleteAll(); products.deleteAll(); customers.deleteAll();
        customer = customers.save(Cliente.builder().nombre("Demo customer").dni("DEMO").email("demo@example.invalid").build());
        product = products.save(Producto.builder().nombre("Sample notebook").codigoProducto("DEMO-1").precio(2.5).build());
    }
    private String request(long customerId, long productId, int quantity) {
        return "{\"clienteId\":" + customerId + ",\"productos\":[{\"idProducto\":" + productId + ",\"cantidad\":" + quantity + "}]}";
    }
    @Test void jsonRequestCreatesAPersistedSaleWithCalculatedTotals() throws Exception {
        http.perform(post("/ventas").contentType(MediaType.APPLICATION_JSON).content(request(customer.getId(), product.getId(), 2)))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.nombreCliente").value("Demo customer"))
            .andExpect(jsonPath("$.productos[0].cantidad").value(2)).andExpect(jsonPath("$.productos[0].subtotal").value(5.0))
            .andExpect(jsonPath("$.total").value(5.0));
        assertEquals(1, sales.count());
        Venta sale = sales.findAll().get(0);
        assertEquals(1, sale.getLineasDeVenta().size());
        assertEquals(sale.getId(), sale.getLineasDeVenta().get(0).getVenta().getId());
    }
    @Test void originalRootRouteStillAcceptsTheSameJsonContract() throws Exception {
        http.perform(post("/").contentType(MediaType.APPLICATION_JSON).content(request(customer.getId(), product.getId(), 1)))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.total").value(2.5));
    }
    @Test void unknownCustomerIs404WithoutSavingASale() throws Exception {
        http.perform(post("/ventas").contentType(MediaType.APPLICATION_JSON).content(request(Long.MAX_VALUE, product.getId(), 1)))
            .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("not_found"));
        assertEquals(0, sales.count());
    }
    @Test void unknownProductIs404WithoutSavingAPartialSale() throws Exception {
        http.perform(post("/ventas").contentType(MediaType.APPLICATION_JSON).content(request(customer.getId(), Long.MAX_VALUE, 1)))
            .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("not_found"));
        assertEquals(0, sales.count());
    }
    @Test void nonPositiveQuantityIs400WithoutSavingASale() throws Exception {
        http.perform(post("/ventas").contentType(MediaType.APPLICATION_JSON).content(request(customer.getId(), product.getId(), 0)))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("invalid_request"));
        assertEquals(0, sales.count());
    }
    @Test void duplicateProductIdsAre400WithoutSavingASale() throws Exception {
        String body = "{\"clienteId\":" + customer.getId() + ",\"productos\":[{\"idProducto\":" + product.getId() + ",\"cantidad\":1},{\"idProducto\":" + product.getId() + ",\"cantidad\":2}]}";
        http.perform(post("/ventas").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("invalid_request"));
        assertEquals(0, sales.count());
    }
}
