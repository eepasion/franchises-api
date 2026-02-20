package co.com.bancolombia.usecase.updatebranchname;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
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
class UpdateBranchNameUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private UpdateBranchNameUseCase useCase;

    private Branch branch;

    @BeforeEach
    void setUp() {
        branch = Branch.builder()
                .id(1L)
                .name("Old Branch Name")
                .franchiseId(1L)
                .build();
    }

    @Test
    void updateName_WhenBranchExists_ShouldUpdateName() {
        Long branchId = 1L;
        String newName = "New Branch Name";
        Branch updatedBranch = branch.toBuilder().name(newName).build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(updatedBranch));

        StepVerifier.create(useCase.updateName(branchId, newName))
                .expectNextMatches(result ->
                        result.getId().equals(1L) &&
                                result.getName().equals("New Branch Name") &&
                                result.getFranchiseId().equals(1L)
                )
                .verifyComplete();

        verify(branchRepository).findById(branchId);
        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void updateName_WhenBranchNotFound_ShouldThrowBusinessException() {
        Long branchId = 999L;
        String newName = "New Branch Name";
        when(branchRepository.findById(branchId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateName(branchId, newName))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                ((BusinessException) error).getErrorCode() == ErrorCode.B404002
                )
                .verify();

        verify(branchRepository).findById(branchId);
        verify(branchRepository, never()).save(any(Branch.class));
    }

    @Test
    void updateName_WhenRepositoryFails_ShouldPropagateError() {
        Long branchId = 1L;
        String newName = "New Branch Name";
        RuntimeException exception = new RuntimeException("Database error");

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.error(exception));

        StepVerifier.create(useCase.updateName(branchId, newName))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database error")
                )
                .verify();

        verify(branchRepository).findById(branchId);
        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void updateName_ShouldPreserveOtherBranchFields() {
        Long branchId = 1L;
        String newName = "Updated Name";
        Branch updatedBranch = branch.toBuilder().name(newName).build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(updatedBranch));

        StepVerifier.create(useCase.updateName(branchId, newName))
                .expectNextMatches(result ->
                        result.getId().equals(branch.getId()) &&
                                result.getFranchiseId().equals(branch.getFranchiseId()) &&
                                result.getName().equals(newName)
                )
                .verifyComplete();

        verify(branchRepository).save(argThat(b ->
                b.getId().equals(branch.getId()) &&
                        b.getFranchiseId().equals(branch.getFranchiseId()) &&
                        b.getName().equals(newName)
        ));
    }
}
