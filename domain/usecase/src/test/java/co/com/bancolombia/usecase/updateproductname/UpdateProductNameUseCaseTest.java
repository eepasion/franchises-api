package co.com.bancolombia.usecase.updateproductname;

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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductNameUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private UpdateProductNameUseCase useCase;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Old Product Name")
                .stock(10)
                .branchId(1L)
                .build();
    }

    @Test
    void updateName_WhenProductExists_ShouldUpdateName() {
        Long productId = 1L;
        String newName = "New Product Name";
        Product updatedProduct = product.toBuilder().name(newName).build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(useCase.updateName(productId, newName))
                .expectNextMatches(result ->
                        result.getId().equals(1L) &&
                                result.getName().equals("New Product Name") &&
                                result.getStock().equals(10) &&
                                result.getBranchId().equals(1L)
                )
                .verifyComplete();

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateName_WhenProductNotFound_ShouldThrowBusinessException() {
        Long productId = 999L;
        String newName = "New Product Name";
        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateName(productId, newName))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                ((BusinessException) error).getErrorCode() == ErrorCode.B404003
                )
                .verify();

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateName_WhenRepositoryFails_ShouldPropagateError() {
        Long productId = 1L;
        String newName = "New Product Name";
        RuntimeException exception = new RuntimeException("Database error");

        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.error(exception));

        StepVerifier.create(useCase.updateName(productId, newName))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database error")
                )
                .verify();

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateName_ShouldPreserveOtherProductFields() {
        Long productId = 1L;
        String newName = "Updated Name";
        Product updatedProduct = product.toBuilder().name(newName).build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(useCase.updateName(productId, newName))
                .expectNextMatches(result ->
                        result.getId().equals(product.getId()) &&
                                result.getStock().equals(product.getStock()) &&
                                result.getBranchId().equals(product.getBranchId()) &&
                                result.getName().equals(newName)
                )
                .verifyComplete();

        verify(productRepository).save(argThat(p ->
                p.getId().equals(product.getId()) &&
                        p.getStock().equals(product.getStock()) &&
                        p.getBranchId().equals(product.getBranchId()) &&
                        p.getName().equals(newName)
        ));
    }
}
