package co.com.bancolombia.usecase.addbranchtofranchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
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
class AddBranchToFranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseGateway;

    @Mock
    private BranchRepository branchGateway;

    @InjectMocks
    private AddBranchToFranchiseUseCase useCase;

    private Franchise franchise;
    private Branch branch;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        branch = Branch.builder()
                .name("Test Branch")
                .build();
    }

    @Test
    void addBranch_WhenFranchiseExists_ShouldSaveBranch() {
        Long franchiseId = 1L;
        Branch savedBranch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(franchiseId)
                .build();

        when(franchiseGateway.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchGateway.save(any(Branch.class))).thenReturn(Mono.just(savedBranch));

        StepVerifier.create(useCase.addBranch(franchiseId, branch))
                .expectNextMatches(result ->
                        result.getId().equals(1L) &&
                                result.getName().equals("Test Branch") &&
                                result.getFranchiseId().equals(franchiseId)
                )
                .verifyComplete();

        verify(franchiseGateway).findById(franchiseId);
        verify(branchGateway).save(any(Branch.class));
    }

    @Test
    void addBranch_WhenFranchiseNotFound_ShouldThrowBusinessException() {
        Long franchiseId = 999L;
        when(franchiseGateway.findById(franchiseId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.addBranch(franchiseId, branch))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                ((BusinessException) error).getErrorCode() == ErrorCode.B404001
                )
                .verify();

        verify(franchiseGateway).findById(franchiseId);
        verify(branchGateway, never()).save(any(Branch.class));
    }

    @Test
    void addBranch_ShouldSetFranchiseIdInBranch() {
        Long franchiseId = 1L;
        Branch savedBranch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(franchiseId)
                .build();

        when(franchiseGateway.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchGateway.save(any(Branch.class))).thenReturn(Mono.just(savedBranch));

        StepVerifier.create(useCase.addBranch(franchiseId, branch))
                .expectNextCount(1)
                .verifyComplete();

        verify(branchGateway).save(argThat(b -> b.getFranchiseId().equals(franchiseId)));
    }

    @Test
    void addBranch_WhenRepositoryFails_ShouldPropagateError() {
        Long franchiseId = 1L;
        RuntimeException exception = new RuntimeException("Database error");

        when(franchiseGateway.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchGateway.save(any(Branch.class))).thenReturn(Mono.error(exception));

        StepVerifier.create(useCase.addBranch(franchiseId, branch))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database error")
                )
                .verify();
    }
}
