package co.com.bancolombia.r2dbc;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.r2dbc.entity.FranchiseEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class FranchiseRepositoryAdapter extends ReactiveAdapterOperations<
        Franchise,
        FranchiseEntity,
        String,
        FranchiseReactiveRepository
> implements FranchiseRepository {
    public FranchiseRepositoryAdapter(FranchiseReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.mapBuilder(d, Franchise.FranchiseBuilder.class).build());
    }

}
