package co.com.bancolombia.usecase.updatefranchisename;

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
class UpdateFranchiseNameUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private UpdateFranchiseNameUseCase useCase;

    private Franchise franchise;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder()
                .id(1L)
                .name("Old Franchise Name")
                .build();
    }

    @Test
    void updateName_WhenFranchiseExists_ShouldUpdateName() {
        Long franchiseId = 1L;
        String newName = "New Franchise Name";
        Franchise updatedFranchise = franchise.toBuilder().name(newName).build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));

        StepVerifier.create(useCase.updateName(franchiseId, newName))
                .expectNextMatches(result ->
                        result.getId().equals(1L) &&
                                result.getName().equals("New Franchise Name")
                )
                .verifyComplete();

        verify(franchiseRepository).findById(franchiseId);
        verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    void updateName_WhenFranchiseNotFound_ShouldThrowBusinessException() {
        Long franchiseId = 999L;
        String newName = "New Franchise Name";
        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateName(franchiseId, newName))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                ((BusinessException) error).getErrorCode() == ErrorCode.B404001
                )
                .verify();

        verify(franchiseRepository).findById(franchiseId);
        verify(franchiseRepository, never()).save(any(Franchise.class));
    }

    @Test
    void updateName_WhenRepositoryFails_ShouldPropagateError() {
        Long franchiseId = 1L;
        String newName = "New Franchise Name";
        RuntimeException exception = new RuntimeException("Database error");

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.error(exception));

        StepVerifier.create(useCase.updateName(franchiseId, newName))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database error")
                )
                .verify();

        verify(franchiseRepository).findById(franchiseId);
        verify(franchiseRepository).save(any(Franchise.class));
    }

    @Test
    void updateName_ShouldPreserveOtherFranchiseFields() {
        Long franchiseId = 1L;
        String newName = "Updated Name";
        Franchise updatedFranchise = franchise.toBuilder().name(newName).build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));

        StepVerifier.create(useCase.updateName(franchiseId, newName))
                .expectNextMatches(result ->
                        result.getId().equals(franchise.getId()) &&
                                result.getName().equals(newName)
                )
                .verifyComplete();

        verify(franchiseRepository).save(argThat(f ->
                f.getId().equals(franchise.getId()) &&
                        f.getName().equals(newName)
        ));
    }
}
