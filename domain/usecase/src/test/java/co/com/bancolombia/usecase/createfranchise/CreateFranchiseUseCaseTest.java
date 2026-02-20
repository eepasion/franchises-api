package co.com.bancolombia.usecase.createfranchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateFranchiseUseCaseTest {

    @Mock
    private FranchiseRepository repository;

    private CreateFranchiseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateFranchiseUseCase(repository);
    }

    @Test
    void shouldSaveFranchiseWithGeneratedId() {
        Franchise franchise = Franchise.builder()
                .name("Test Franchise")
                .build();

        Franchise savedFranchise = franchise.toBuilder()
                .id("generated-id")
                .build();

        when(repository.save(any(Franchise.class))).thenReturn(Mono.just(savedFranchise));

        StepVerifier.create(useCase.save(franchise))
                .assertNext(result -> {
                    assertNotNull(result.getId());
                    assertEquals("Test Franchise", result.getName());
                })
                .verifyComplete();

        verify(repository).save(any(Franchise.class));
    }
}
