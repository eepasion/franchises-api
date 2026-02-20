package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.r2dbc.entity.FranchiseEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.repository.FranchiseReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FranchiseRepositoryAdapter extends ReactiveAdapterOperations<
        Franchise,
        FranchiseEntity,
        Long,
        FranchiseReactiveRepository
        > implements FranchiseRepository {

    public FranchiseRepositoryAdapter(FranchiseReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.mapBuilder(d, Franchise.FranchiseBuilder.class).build());
    }
}
