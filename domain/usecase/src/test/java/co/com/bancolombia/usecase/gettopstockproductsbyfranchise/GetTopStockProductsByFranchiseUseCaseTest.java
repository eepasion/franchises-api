package co.com.bancolombia.usecase.gettopstockproductsbyfranchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.ProductWithBranch;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class GetTopStockProductsByFranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetTopStockProductsByFranchiseUseCase useCase;

    private Franchise franchise;
    private Branch branch1;
    private Product product1;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        branch1 = Branch.builder()
                .id(1L)
                .name("Branch 1")
                .franchiseId(1L)
                .build();

        product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .stock(50)
                .branchId(1L)
                .build();
    }

    @Test
    void getTopStockProductsByFranchise_WhenFranchiseExists_ShouldReturnProductsWithBranches() {
        Long franchiseId = 1L;

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(productRepository.findTopStockByBranchesInFranchise(franchiseId))
                .thenReturn(Flux.just(product1));
        when(branchRepository.findById(1L)).thenReturn(Mono.just(branch1));

        StepVerifier.create(useCase.getTopStockProductsByFranchise(franchiseId))
                .expectNext(ProductWithBranch.builder()
                        .product(product1)
                        .branch(branch1)
                        .build())
                .verifyComplete();

        verify(franchiseRepository).findById(franchiseId);
        verify(productRepository).findTopStockByBranchesInFranchise(franchiseId);
        verify(branchRepository).findById(1L);
    }

    @Test
    void getTopStockProductsByFranchise_WhenFranchiseNotFound_ShouldThrowBusinessException() {
        Long franchiseId = 999L;
        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getTopStockProductsByFranchise(franchiseId))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                ((BusinessException) error).getErrorCode() == ErrorCode.B404001
                )
                .verify();

        verify(franchiseRepository).findById(franchiseId);
        verify(productRepository, never()).findTopStockByBranchesInFranchise(any());
        verify(branchRepository, never()).findById(any());
    }

    @Test
    void getTopStockProductsByFranchise_WhenNoProducts_ShouldReturnEmpty() {
        Long franchiseId = 1L;

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(productRepository.findTopStockByBranchesInFranchise(franchiseId))
                .thenReturn(Flux.empty());

        StepVerifier.create(useCase.getTopStockProductsByFranchise(franchiseId))
                .verifyComplete();

        verify(franchiseRepository).findById(franchiseId);
        verify(productRepository).findTopStockByBranchesInFranchise(franchiseId);
        verify(branchRepository, never()).findById(any());
    }

    @Test
    void getTopStockProductsByFranchise_ShouldCreateCorrectProductWithBranchMapping() {
        Long franchiseId = 1L;

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(productRepository.findTopStockByBranchesInFranchise(franchiseId))
                .thenReturn(Flux.just(product1));
        when(branchRepository.findById(1L)).thenReturn(Mono.just(branch1));

        StepVerifier.create(useCase.getTopStockProductsByFranchise(franchiseId))
                .assertNext(result -> {
                    assert result.getProduct().equals(product1);
                    assert result.getBranch().equals(branch1);
                })
                .verifyComplete();
    }
}
