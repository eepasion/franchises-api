package co.com.bancolombia.usecase.updateproductstock;

import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductStockUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductStockUseCase useCase;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(10)
                .branchId(1L)
                .build();
    }

    @Test
    void updateStock_WhenProductExists_ShouldUpdateStock() {
        Long productId = 1L;
        Integer newStock = 20;
        Product updatedProduct = product.toBuilder().stock(newStock).build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(useCase.updateStock(productId, newStock))
                .expectNextMatches(result ->
                        result.getId().equals(1L) &&
                                result.getName().equals("Test Product") &&
                                result.getStock().equals(20) &&
                                result.getBranchId().equals(1L)
                )
                .verifyComplete();

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateStock_WhenProductNotFound_ShouldThrowBusinessException() {
        Long productId = 999L;
        Integer newStock = 20;
        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateStock(productId, newStock))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                ((BusinessException) error).getErrorCode() == ErrorCode.B404003
                )
                .verify();

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateStock_WhenRepositoryFails_ShouldPropagateError() {
        Long productId = 1L;
        Integer newStock = 20;
        RuntimeException exception = new RuntimeException("Database error");

        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.error(exception));

        StepVerifier.create(useCase.updateStock(productId, newStock))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database error")
                )
                .verify();

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateStock_ShouldPreserveOtherProductFields() {
        Long productId = 1L;
        Integer newStock = 5;
        Product updatedProduct = product.toBuilder().stock(newStock).build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(useCase.updateStock(productId, newStock))
                .expectNextMatches(result ->
                        result.getId().equals(product.getId()) &&
                                result.getName().equals(product.getName()) &&
                                result.getBranchId().equals(product.getBranchId()) &&
                                result.getStock().equals(newStock)
                )
                .verifyComplete();

        verify(productRepository).save(argThat(p ->
                p.getId().equals(product.getId()) &&
                        p.getName().equals(product.getName()) &&
                        p.getBranchId().equals(product.getBranchId()) &&
                        p.getStock().equals(newStock)
        ));
    }
}
