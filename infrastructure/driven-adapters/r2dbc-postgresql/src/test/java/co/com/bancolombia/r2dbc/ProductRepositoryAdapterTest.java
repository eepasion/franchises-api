package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.r2dbc.adapter.ProductRepositoryAdapter;
import co.com.bancolombia.r2dbc.entity.ProductEntity;
import co.com.bancolombia.r2dbc.repository.ProductReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @Mock
    private ProductReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private ProductRepositoryAdapter adapter;

    private Product product;
    private ProductEntity productEntity;

    @BeforeEach
    void setUp() {
        adapter = new ProductRepositoryAdapter(repository, mapper);

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(10)
                .branchId(1L)
                .build();

        productEntity = ProductEntity.builder()
                .id(1L)
                .name("Test Product")
                .stock(10)
                .branchId(1L)
                .build();
    }

    @Test
    void save_ShouldMapAndSaveProduct() {
        when(mapper.map(any(Product.class), eq(ProductEntity.class))).thenReturn(productEntity);
        when(repository.save(any(ProductEntity.class))).thenReturn(Mono.just(productEntity));
        when(mapper.mapBuilder(any(ProductEntity.class), eq(Product.ProductBuilder.class)))
                .thenReturn(Product.builder()
                        .id(1L)
                        .name("Test Product")
                        .stock(10)
                        .branchId(1L));

        StepVerifier.create(adapter.save(product))
                .expectNextMatches(result ->
                        result.getId().equals(1L) &&
                                result.getName().equals("Test Product") &&
                                result.getStock().equals(10) &&
                                result.getBranchId().equals(1L)
                )
                .verifyComplete();

        verify(mapper).map(any(Product.class), eq(ProductEntity.class));
        verify(repository).save(any(ProductEntity.class));
        verify(mapper).mapBuilder(any(ProductEntity.class), eq(Product.ProductBuilder.class));
    }

    @Test
    void save_WhenRepositoryFails_ShouldPropagateError() {
        RuntimeException exception = new RuntimeException("Database error");
        when(mapper.map(any(Product.class), eq(ProductEntity.class))).thenReturn(productEntity);
        when(repository.save(any(ProductEntity.class))).thenReturn(Mono.error(exception));

        StepVerifier.create(adapter.save(product))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database error")
                )
                .verify();

        verify(repository).save(any(ProductEntity.class));
    }

    @Test
    void findById_ShouldMapAndReturnProduct() {
        when(repository.findById(1L)).thenReturn(Mono.just(productEntity));
        when(mapper.mapBuilder(any(ProductEntity.class), eq(Product.ProductBuilder.class)))
                .thenReturn(Product.builder()
                        .id(1L)
                        .name("Test Product")
                        .stock(10)
                        .branchId(1L));

        StepVerifier.create(adapter.findById(1L))
                .expectNextMatches(result ->
                        result.getId().equals(1L) &&
                                result.getName().equals("Test Product") &&
                                result.getStock().equals(10)
                )
                .verifyComplete();

        verify(repository).findById(1L);
        verify(mapper).mapBuilder(any(ProductEntity.class), eq(Product.ProductBuilder.class));
    }

    @Test
    void findById_WhenNotFound_ShouldReturnEmpty() {
        when(repository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(999L))
                .verifyComplete();

        verify(repository).findById(999L);
        verify(mapper, never()).mapBuilder(any(), any());
    }

    @Test
    void save_WithNullId_ShouldSaveNewProduct() {
        Product newProduct = Product.builder()
                .name("New Product")
                .stock(5)
                .branchId(1L)
                .build();

        ProductEntity newEntity = ProductEntity.builder()
                .name("New Product")
                .stock(5)
                .branchId(1L)
                .build();

        ProductEntity savedEntity = ProductEntity.builder()
                .id(2L)
                .name("New Product")
                .stock(5)
                .branchId(1L)
                .build();

        when(mapper.map(any(Product.class), eq(ProductEntity.class))).thenReturn(newEntity);
        when(repository.save(any(ProductEntity.class))).thenReturn(Mono.just(savedEntity));
        when(mapper.mapBuilder(any(ProductEntity.class), eq(Product.ProductBuilder.class)))
                .thenReturn(Product.builder()
                        .id(2L)
                        .name("New Product")
                        .stock(5)
                        .branchId(1L));

        StepVerifier.create(adapter.save(newProduct))
                .expectNextMatches(result ->
                        result.getId().equals(2L) &&
                                result.getName().equals("New Product") &&
                                result.getStock().equals(5)
                )
                .verifyComplete();
    }

    @Test
    void deleteById_ShouldDeleteProduct() {
        when(repository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById(1L))
                .verifyComplete();

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteById_WhenRepositoryFails_ShouldPropagateError() {
        RuntimeException exception = new RuntimeException("Database error");
        when(repository.deleteById(1L)).thenReturn(Mono.error(exception));

        StepVerifier.create(adapter.deleteById(1L))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database error")
                )
                .verify();

        verify(repository).deleteById(1L);
    }

}
