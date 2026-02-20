package co.com.bancolombia.usecase.addproducttobranch;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
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
class AddProductToBranchUseCaseTest {

    @Mock
    private BranchRepository branchGateway;

    @Mock
    private ProductRepository productGateway;

    @InjectMocks
    private AddProductToBranchUseCase useCase;

    private Branch branch;
    private Product product;

    @BeforeEach
    void setUp() {
        branch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .build();

        product = Product.builder()
                .name("Test Product")
                .stock(10)
                .build();
    }

    @Test
    void addProduct_WhenBranchExists_ShouldSaveProduct() {
        Long branchId = 1L;
        Product savedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(10)
                .branchId(branchId)
                .build();

        when(branchGateway.findById(branchId)).thenReturn(Mono.just(branch));
        when(productGateway.save(any(Product.class))).thenReturn(Mono.just(savedProduct));

        StepVerifier.create(useCase.addProduct(branchId, product))
                .expectNextMatches(result ->
                        result.getId().equals(1L) &&
                                result.getName().equals("Test Product") &&
                                result.getBranchId().equals(branchId)
                )
                .verifyComplete();

        verify(branchGateway).findById(branchId);
        verify(productGateway).save(any(Product.class));
    }

    @Test
    void addProduct_WhenBranchNotFound_ShouldThrowBusinessException() {
        Long branchId = 999L;
        when(branchGateway.findById(branchId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.addProduct(branchId, product))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                ((BusinessException) error).getErrorCode() == ErrorCode.B404002
                )
                .verify();

        verify(branchGateway).findById(branchId);
        verify(productGateway, never()).save(any(Product.class));
    }

    @Test
    void addProduct_ShouldSetBranchIdInProduct() {
        Long branchId = 1L;
        Product savedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(10)
                .branchId(branchId)
                .build();

        when(branchGateway.findById(branchId)).thenReturn(Mono.just(branch));
        when(productGateway.save(any(Product.class))).thenReturn(Mono.just(savedProduct));

        StepVerifier.create(useCase.addProduct(branchId, product))
                .expectNextCount(1)
                .verifyComplete();

        verify(productGateway).save(argThat(p -> p.getBranchId().equals(branchId)));
    }

    @Test
    void addProduct_WhenRepositoryFails_ShouldPropagateError() {
        Long branchId = 1L;
        RuntimeException exception = new RuntimeException("Database error");

        when(branchGateway.findById(branchId)).thenReturn(Mono.just(branch));
        when(productGateway.save(any(Product.class))).thenReturn(Mono.error(exception));

        StepVerifier.create(useCase.addProduct(branchId, product))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database error")
                )
                .verify();
    }
}
