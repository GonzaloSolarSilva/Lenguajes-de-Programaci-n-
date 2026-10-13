package com.saborcriollo.pedidos.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.saborcriollo.pedidos.model.Administrador;
import com.saborcriollo.pedidos.model.Cajero;
import com.saborcriollo.pedidos.model.Categoria;
import com.saborcriollo.pedidos.model.Cocinero;
import com.saborcriollo.pedidos.model.Insumo;
import com.saborcriollo.pedidos.model.Mesa;
import com.saborcriollo.pedidos.model.Mesero;
import com.saborcriollo.pedidos.model.Producto;
import com.saborcriollo.pedidos.model.RecetaItem;
import com.saborcriollo.pedidos.model.enums.Estacion;
import com.saborcriollo.pedidos.model.enums.Turno;
import com.saborcriollo.pedidos.repository.CategoriaRepository;
import com.saborcriollo.pedidos.repository.InsumoRepository;
import com.saborcriollo.pedidos.repository.MesaRepository;
import com.saborcriollo.pedidos.repository.ProductoRepository;
import com.saborcriollo.pedidos.repository.RecetaItemRepository;
import com.saborcriollo.pedidos.repository.UsuarioRepository;

/**
 * Carga datos de demostración la primera vez que se ejecuta el sistema
 * (solo si la tabla usuario está vacía). Contraseña de todos los usuarios: 123456.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final MesaRepository mesaRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final InsumoRepository insumoRepository;
    private final RecetaItemRepository recetaRepository;

    public DataSeeder(UsuarioRepository usuarioRepository, MesaRepository mesaRepository,
                      CategoriaRepository categoriaRepository, ProductoRepository productoRepository,
                      InsumoRepository insumoRepository, RecetaItemRepository recetaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.mesaRepository = mesaRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.insumoRepository = insumoRepository;
        this.recetaRepository = recetaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        // Usuarios (uno por rol, más un segundo mesero de la tarde)
        usuarioRepository.save(new Mesero("Carlos", "Quispe", "mesero1", "123456", Turno.MANANA));
        usuarioRepository.save(new Mesero("Lucía", "Rojas", "mesero2", "123456", Turno.TARDE));
        usuarioRepository.save(new Cocinero("Pedro", "Huamán", "cocinero1", "123456", Estacion.FONDOS));
        usuarioRepository.save(new Cajero("Ana", "Torres", "cajero1", "123456"));
        usuarioRepository.save(new Administrador("Admin", "Sabor Criollo", "admin", "123456"));

        // 18 mesas del salón principal
        for (int numero = 1; numero <= 18; numero++) {
            mesaRepository.save(new Mesa(numero, numero % 3 == 0 ? 6 : 4));
        }

        // Categorías
        Categoria entradas = categoriaRepository.save(new Categoria("Entradas"));
        Categoria fondos = categoriaRepository.save(new Categoria("Platos de fondo"));
        Categoria postres = categoriaRepository.save(new Categoria("Postres"));
        Categoria bebidas = categoriaRepository.save(new Categoria("Bebidas"));

        // Insumos (nombre, unidad, stock actual, stock mínimo)
        Insumo pescado = insumo("Pescado", "kg", "20", "3");
        Insumo limon = insumo("Limón", "kg", "15", "2");
        Insumo camote = insumo("Camote", "kg", "10", "2");
        Insumo papa = insumo("Papa amarilla", "kg", "25", "4");
        Insumo lomo = insumo("Lomo de res", "kg", "18", "3");
        Insumo cebolla = insumo("Cebolla roja", "kg", "20", "3");
        Insumo arroz = insumo("Arroz", "kg", "40", "5");
        Insumo pollo = insumo("Pollo", "kg", "30", "5");
        Insumo aji = insumo("Ají amarillo", "kg", "8", "1");
        Insumo maiz = insumo("Maíz morado", "kg", "6", "1");
        Insumo leche = insumo("Leche condensada", "lata", "24", "4");

        // Carta y recetas
        Producto ceviche = producto("Ceviche clásico", "Pescado fresco en leche de tigre", "28.00", Estacion.ENTRADAS, entradas);
        receta(ceviche, pescado, "0.250");
        receta(ceviche, limon, "0.100");
        receta(ceviche, camote, "0.100");
        receta(ceviche, cebolla, "0.050");

        Producto causa = producto("Causa limeña", "Causa rellena de pollo", "18.00", Estacion.ENTRADAS, entradas);
        receta(causa, papa, "0.300");
        receta(causa, limon, "0.030");
        receta(causa, pollo, "0.100");

        Producto lomoSaltado = producto("Lomo saltado", "Lomo fino con papas fritas y arroz", "32.00", Estacion.FONDOS, fondos);
        receta(lomoSaltado, lomo, "0.250");
        receta(lomoSaltado, cebolla, "0.100");
        receta(lomoSaltado, papa, "0.200");
        receta(lomoSaltado, arroz, "0.150");

        Producto ajiGallina = producto("Ají de gallina", "Crema de ají amarillo con pollo deshilachado", "26.00", Estacion.FONDOS, fondos);
        receta(ajiGallina, pollo, "0.250");
        receta(ajiGallina, aji, "0.050");
        receta(ajiGallina, arroz, "0.150");
        receta(ajiGallina, papa, "0.100");

        Producto arrozPollo = producto("Arroz con pollo", "Arroz verde con presa de pollo", "24.00", Estacion.FONDOS, fondos);
        receta(arrozPollo, arroz, "0.200");
        receta(arrozPollo, pollo, "0.300");

        Producto suspiro = producto("Suspiro limeño", "Postre de manjar blanco y merengue", "12.00", Estacion.POSTRES_BEBIDAS, postres);
        receta(suspiro, leche, "0.500");

        Producto chicha = producto("Chicha morada", "Jarra de chicha morada", "8.00", Estacion.POSTRES_BEBIDAS, bebidas);
        receta(chicha, maiz, "0.100");

        producto("Inca Kola 500 ml", "Gaseosa personal", "6.00", Estacion.POSTRES_BEBIDAS, bebidas);
    }

    private Insumo insumo(String nombre, String unidad, String stock, String minimo) {
        return insumoRepository.save(new Insumo(nombre, unidad, new BigDecimal(stock), new BigDecimal(minimo)));
    }

    private Producto producto(String nombre, String descripcion, String precio, Estacion estacion, Categoria categoria) {
        return productoRepository.save(new Producto(nombre, descripcion, new BigDecimal(precio), estacion, categoria));
    }

    private void receta(Producto producto, Insumo insumo, String cantidad) {
        recetaRepository.save(new RecetaItem(producto, insumo, new BigDecimal(cantidad)));
    }
}
