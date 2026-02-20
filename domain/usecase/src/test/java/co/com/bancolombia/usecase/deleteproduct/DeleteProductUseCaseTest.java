package co.com.bancolombia.usecase.deleteproduct;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseTest {

    @Mock
    private ProductRepository productGateway;

    @InjectMocks
    private DeleteProductUseCase useCase;

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
    void deleteProduct_WhenProductExists_ShouldDeleteProduct() {
        Long productId = 1L;

        when(productGateway.findById(productId)).thenReturn(Mono.just(product));
        when(productGateway.deleteById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteProduct(productId))
                .verifyComplete();

        verify(productGateway).findById(productId);
        verify(productGateway).deleteById(productId);
    }

    @Test
    void deleteProduct_WhenProductNotFound_ShouldThrowBusinessException() {
        Long productId = 999L;
        when(productGateway.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteProduct(productId))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                ((BusinessException) error).getErrorCode() == ErrorCode.B404003
                )
                .verify();

        verify(productGateway).findById(productId);
        verify(productGateway, never()).deleteById(any());
    }

    @Test
    void deleteProduct_WhenDeleteFails_ShouldPropagateError() {
        Long productId = 1L;
        RuntimeException exception = new RuntimeException("Database error");

        when(productGateway.findById(productId)).thenReturn(Mono.just(product));
        when(productGateway.deleteById(productId)).thenReturn(Mono.error(exception));

        StepVerifier.create(useCase.deleteProduct(productId))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database error")
                )
                .verify();

        verify(productGateway).findById(productId);
        verify(productGateway).deleteById(productId);
    }
}
