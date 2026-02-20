package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.r2dbc.adapter.BranchRepositoryAdapter;
import co.com.bancolombia.r2dbc.entity.BranchEntity;
import co.com.bancolombia.r2dbc.repository.BranchReactiveRepository;
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
class BranchRepositoryAdapterTest {

    @Mock
    private BranchReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private BranchRepositoryAdapter adapter;

    private Branch branch;
    private BranchEntity branchEntity;

    @BeforeEach
    void setUp() {
        adapter = new BranchRepositoryAdapter(repository, mapper);

        branch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .build();

        branchEntity = BranchEntity.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .build();
    }

    @Test
    void save_ShouldMapAndSaveBranch() {
        when(mapper.map(any(Branch.class), eq(BranchEntity.class))).thenReturn(branchEntity);
        when(repository.save(any(BranchEntity.class))).thenReturn(Mono.just(branchEntity));
        when(mapper.mapBuilder(any(BranchEntity.class), eq(Branch.BranchBuilder.class)))
                .thenReturn(Branch.builder()
                        .id(1L)
                        .name("Test Branch")
                        .franchiseId(1L));

        StepVerifier.create(adapter.save(branch))
                .expectNextMatches(result ->
                        result.getId().equals(1L) &&
                                result.getName().equals("Test Branch") &&
                                result.getFranchiseId().equals(1L)
                )
                .verifyComplete();

        verify(mapper).map(any(Branch.class), eq(BranchEntity.class));
        verify(repository).save(any(BranchEntity.class));
        verify(mapper).mapBuilder(any(BranchEntity.class), eq(Branch.BranchBuilder.class));
    }

    @Test
    void save_WhenRepositoryFails_ShouldPropagateError() {
        RuntimeException exception = new RuntimeException("Database error");
        when(mapper.map(any(Branch.class), eq(BranchEntity.class))).thenReturn(branchEntity);
        when(repository.save(any(BranchEntity.class))).thenReturn(Mono.error(exception));

        StepVerifier.create(adapter.save(branch))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Database error")
                )
                .verify();

        verify(repository).save(any(BranchEntity.class));
    }

    @Test
    void findById_ShouldMapAndReturnBranch() {
        when(repository.findById(1L)).thenReturn(Mono.just(branchEntity));
        when(mapper.mapBuilder(any(BranchEntity.class), eq(Branch.BranchBuilder.class)))
                .thenReturn(Branch.builder()
                        .id(1L)
                        .name("Test Branch")
                        .franchiseId(1L));

        StepVerifier.create(adapter.findById(1L))
                .expectNextMatches(result ->
                        result.getId().equals(1L) &&
                                result.getName().equals("Test Branch")
                )
                .verifyComplete();

        verify(repository).findById(1L);
        verify(mapper).mapBuilder(any(BranchEntity.class), eq(Branch.BranchBuilder.class));
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
    void save_WithNullId_ShouldSaveNewBranch() {
        Branch newBranch = Branch.builder()
                .name("New Branch")
                .franchiseId(1L)
                .build();

        BranchEntity newEntity = BranchEntity.builder()
                .name("New Branch")
                .franchiseId(1L)
                .build();

        BranchEntity savedEntity = BranchEntity.builder()
                .id(2L)
                .name("New Branch")
                .franchiseId(1L)
                .build();

        when(mapper.map(any(Branch.class), eq(BranchEntity.class))).thenReturn(newEntity);
        when(repository.save(any(BranchEntity.class))).thenReturn(Mono.just(savedEntity));
        when(mapper.mapBuilder(any(BranchEntity.class), eq(Branch.BranchBuilder.class)))
                .thenReturn(Branch.builder()
                        .id(2L)
                        .name("New Branch")
                        .franchiseId(1L));

        StepVerifier.create(adapter.save(newBranch))
                .expectNextMatches(result ->
                        result.getId().equals(2L) &&
                                result.getName().equals("New Branch")
                )
                .verifyComplete();
    }
}
