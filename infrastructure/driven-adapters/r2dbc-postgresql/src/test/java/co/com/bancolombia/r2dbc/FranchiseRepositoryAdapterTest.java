package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.r2dbc.adapter.FranchiseRepositoryAdapter;
import co.com.bancolombia.r2dbc.entity.FranchiseEntity;
import co.com.bancolombia.r2dbc.repository.FranchiseReactiveRepository;
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
class FranchiseRepositoryAdapterTest {

    @Mock
    private FranchiseReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private FranchiseRepositoryAdapter adapter;
    private Franchise franchise;
    private FranchiseEntity franchiseEntity;

    @BeforeEach
    void setUp() {
        adapter = new FranchiseRepositoryAdapter(repository, mapper);

        franchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        franchiseEntity = FranchiseEntity.builder()
                .id(1L)
                .name("Test Franchise")
                .build();
    }

    @Test
    void save_ShouldMapAndSaveFranchise() {
        when(mapper.map(any(Franchise.class), eq(FranchiseEntity.class))).thenReturn(franchiseEntity);
        when(repository.save(any(FranchiseEntity.class))).thenReturn(Mono.just(franchiseEntity));
        when(mapper.mapBuilder(any(FranchiseEntity.class), eq(Franchise.FranchiseBuilder.class)))
                .thenReturn(Franchise.builder().id(1L).name("Test Franchise"));

        StepVerifier.create(adapter.save(franchise))
                .expectNextMatches(result ->
                        result.getId().equals(1L) &&
                                result.getName().equals("Test Franchise")
                )
                .verifyComplete();

        verify(mapper).map(any(Franchise.class), eq(FranchiseEntity.class));
        verify(repository).save(any(FranchiseEntity.class));
        verify(mapper).mapBuilder(any(FranchiseEntity.class), eq(Franchise.FranchiseBuilder.class));
    }

    @Test
    void save_WhenRepositoryFails_ShouldPropagateError() {
        RuntimeException exception = new RuntimeException("Database error");
        when(mapper.map(any(Franchise.class), eq(FranchiseEntity.class))).thenReturn(franchiseEntity);
        when(repository.save(any(FranchiseEntity.class))).thenReturn(Mono.error(exception));

        StepVerifier.create(adapter.save(franchise))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database error")
                )
                .verify();

        verify(repository).save(any(FranchiseEntity.class));
    }

    @Test
    void findById_ShouldMapAndReturnFranchise() {
        when(repository.findById(1L)).thenReturn(Mono.just(franchiseEntity));
        when(mapper.mapBuilder(any(FranchiseEntity.class), eq(Franchise.FranchiseBuilder.class)))
                .thenReturn(Franchise.builder().id(1L).name("Test Franchise"));

        StepVerifier.create(adapter.findById(1L))
                .expectNextMatches(result ->
                        result.getId().equals(1L) &&
                                result.getName().equals("Test Franchise")
                )
                .verifyComplete();

        verify(repository).findById(1L);
        verify(mapper).mapBuilder(any(FranchiseEntity.class), eq(Franchise.FranchiseBuilder.class));
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
    void save_WithNullId_ShouldSaveNewFranchise() {
        Franchise newFranchise = Franchise.builder().name("New Franchise").build();
        FranchiseEntity newEntity = FranchiseEntity.builder().name("New Franchise").build();
        FranchiseEntity savedEntity = FranchiseEntity.builder().id(2L).name("New Franchise").build();

        when(mapper.map(any(Franchise.class), eq(FranchiseEntity.class))).thenReturn(newEntity);
        when(repository.save(any(FranchiseEntity.class))).thenReturn(Mono.just(savedEntity));
        when(mapper.mapBuilder(any(FranchiseEntity.class), eq(Franchise.FranchiseBuilder.class)))
                .thenReturn(Franchise.builder().id(2L).name("New Franchise"));

        StepVerifier.create(adapter.save(newFranchise))
                .expectNextMatches(result ->
                        result.getId().equals(2L) &&
                                result.getName().equals("New Franchise")
                )
                .verifyComplete();
    }
}
