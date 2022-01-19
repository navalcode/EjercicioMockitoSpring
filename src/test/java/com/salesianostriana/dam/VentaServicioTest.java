package com.salesianostriana.dam;

import com.salesianostriana.dam.model.Cliente;
import com.salesianostriana.dam.model.LineaDeVenta;
import com.salesianostriana.dam.model.Producto;
import com.salesianostriana.dam.model.Venta;
import com.salesianostriana.dam.repos.ProductoRepositorio;
import com.salesianostriana.dam.repos.VentaRepositorio;
import com.salesianostriana.dam.service.VentaServicio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class VentaServicioTest {

	@Mock
	ProductoRepositorio productoRepositorio;

	@Mock
	VentaRepositorio ventaRepositorio;

	@InjectMocks
	VentaServicio ventaServicio;

	//En primer lugar ejecutaremos un test para el caso en el que se cree una nueva venta
	//correctamente

	@Test
	void nuevaVentaSucces() {

		Cliente c = Cliente.builder()
				.dni("00000000F")
				.nombre("Cliente 1")
				.email("cliente@cliente.cli")
				.build();

		Optional<Producto> p = Optional.of(Producto.builder()
				.nombre("Desodorante")
				.id(1L)
				.codigoProducto("1")
				.precio(2.5)
				.build());

		lenient().when(productoRepositorio.findById(1L)).thenReturn(p);

		Venta venta = ventaServicio.nuevaVenta(Map.of(1L,2),c);

		venta.getLineasDeVenta().forEach(l -> assertTrue(l.getProducto().equals(p.get())));
	}

	//Vamos a probar ahora que cuando se proporcione un id inexistente se devuelva un
	//null en vez de salvarse la venta. Este test fallará, dado que el método nuevaVenta
	// no contempla los valores nulos o vacios, a pesar de que productoRepositorio.findById
	//devuelve un optional

	//Si quisieramos que el test se realizara con exito deberiamos comprobar que se devuelve una
	//excepción del tipo NoSuchElementException

	@Test
	void ventaNuevaFailsWithNegativeProductId(){

		Cliente c = Cliente.builder()
				.dni("00000000F")
				.nombre("Cliente 1")
				.email("cliente@cliente.cli")
				.build();

		Optional<Producto> empty= Optional.empty();

		lenient().when(productoRepositorio.findById(1L)).thenReturn(empty);

		assertThrows(NoSuchElementException.class,()->ventaServicio.nuevaVenta(Map.of(1L,1),c));

	}

	//Pasamos ahora a testear el método addProductoToVenta

	//Con una aproximación de caja negra primero comprobaremos que devuelve null al tratar
	//de agregar un producto a una venta inexistente.

	@Test
	void addProductoToEmptyVentaReturnsNull(){
		Optional<Venta> empty = Optional.empty();

		lenient().when(ventaRepositorio.findById(3L)).thenReturn(empty);

		Venta venta = this.ventaServicio.addProductoToVenta(3L,1L,1);

		assertNull(venta);
	}

	//Ahora procederemos al test en el que la venta existe pero el producto no,
	//podriamos esperar un comportamiento similar siguiendo una aproximación de caja negra.

	@Test
	void addProductoToVentaWithEmptyProductReturnsNull(){
		Optional<Venta> v = Optional.of(new Venta());
		v.get().setId(1L);

		Optional<Producto> empty= Optional.empty();

		lenient().when(ventaRepositorio.findById(1L)).thenReturn(v);
		lenient().when(productoRepositorio.findById(1L)).thenReturn(empty);

		Venta venta = ventaServicio.addProductoToVenta(1L,1L,1);

		//En este caso el test debería fallar ya que el código realmente no contempla que el producto esté vacio
		assertNull(venta);
	}

	//Ahora comprobaremos que el método funciona correctamente al pasarle los datos correctos.
	//Este test tendrá un código muy similar al nuevaVentaSucces()

	@Test
	void addProductoToVentaSucces(){
		Cliente c = Cliente.builder()
				.dni("00000000F")
				.nombre("Cliente 1")
				.email("cliente@cliente.cli")
				.build();

		Optional<Producto> p = Optional.of(Producto.builder()
				.nombre("Desodorante")
				.id(1L)
				.codigoProducto("1")
				.precio(2.5)
				.build());

		Optional<Venta> v = Optional.of(new Venta());
		v.get().setId(1L);

		lenient().when(ventaRepositorio.findById(1L)).thenReturn(v);
		lenient().when(productoRepositorio.findById(1L)).thenReturn(p);

		Venta venta = ventaServicio.addProductoToVenta(1L,1L,1);

		venta.getLineasDeVenta().forEach(l -> assertTrue(l.getProducto().equals(p.get())));

	}

	//Para el tercer método, eliminar lineas de venta, las pruebas serán muy similares
	//a las de addProductoToVenta, ya que lo que se hace es eliminar lineas de venta de una venta
	//Tendremos por tanto los mismos casos de prueba.

	@Test
	void removeLineaVentaFromEmptyVentaReturnsNull(){
		Optional<Venta> empty = Optional.empty();

		lenient().when(ventaRepositorio.findById(3L)).thenReturn(empty);

		Venta venta = this.ventaServicio.removeLineaVenta(3L,1L);

		assertNull(venta);
	}

	//Comoprobamos que al borrar una linea de venta con producto null devuelva null
	@Test
	void removeLineaVentaWithEmptyProductReturnsNull(){
		Optional<Venta> v = Optional.of(new Venta());
		v.get().setId(1L);

		Optional<Producto> empty= Optional.empty();

		lenient().when(ventaRepositorio.findById(1L)).thenReturn(v);
		lenient().when(productoRepositorio.findById(1L)).thenReturn(empty);

		Venta venta = ventaServicio.removeLineaVenta(1L,1L);

		//Del mismo modo que sucedia con su homologo addProductoToVenta, con contempla
		//que un producto pueda venir vacio, de tal forma que el test falla.
		assertNull(venta);
	}

	//Comprobamos que eliminar una linea de venta sea exitosa

	@Test
	void removeLineaVentaSucces(){
		Cliente c = Cliente.builder()
				.dni("00000000F")
				.nombre("Cliente 1")
				.email("cliente@cliente.cli")
				.build();

		Optional<Producto> p = Optional.of(Producto.builder()
				.nombre("Desodorante")
				.id(1L)
				.codigoProducto("1")
				.precio(2.5)
				.build());

		Optional<Venta> v = Optional.of(new Venta());
		v.get().setId(1L);

		LineaDeVenta lv = LineaDeVenta.builder()
						.producto(p.get())
				        .cantidad(1)
				        .pvp(2.50)
						.build();

		LineaDeVenta lv2 = LineaDeVenta.builder()
				.producto(p.get())
				.cantidad(1)
				.pvp(2.50)
				.build();

		v.get().addLineaVenta(lv);
		v.get().addLineaVenta(lv2);

		lenient().when(ventaRepositorio.findById(1L)).thenReturn(v);
		lenient().when(productoRepositorio.findById(1L)).thenReturn(p);

		Venta venta = ventaServicio.removeLineaVenta(1L,1L);

		venta.getLineasDeVenta().forEach(l -> assertFalse(l.getProducto().equals(p.get())));

	}

}
