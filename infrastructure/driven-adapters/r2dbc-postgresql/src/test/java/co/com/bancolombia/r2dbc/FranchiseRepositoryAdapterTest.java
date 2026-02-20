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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseRepositoryAdapterTest {

    @Mock
    private FranchiseReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    private FranchiseRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new FranchiseRepositoryAdapter(repository, mapper);
    }

    @Test
    void shouldSaveFranchise() {
        Franchise franchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        FranchiseEntity entity = FranchiseEntity.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        when(mapper.map(any(), eq(FranchiseEntity.class))).thenReturn(entity);
        when(repository.save(any(FranchiseEntity.class))).thenReturn(Mono.just(entity));
        when(mapper.mapBuilder(any(), eq(Franchise.FranchiseBuilder.class)))
                .thenReturn(franchise.toBuilder());

        StepVerifier.create(adapter.save(franchise))
                .assertNext(result -> {
                    assertEquals(1L, result.getId());
                    assertEquals("Test Franchise", result.getName());
                })
                .verifyComplete();

        verify(repository).save(any(FranchiseEntity.class));
    }

}
