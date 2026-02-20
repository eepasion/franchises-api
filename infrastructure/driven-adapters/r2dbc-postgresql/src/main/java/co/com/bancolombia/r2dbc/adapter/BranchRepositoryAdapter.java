package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.r2dbc.entity.BranchEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.repository.BranchReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class BranchRepositoryAdapter extends ReactiveAdapterOperations<
        Branch,
        BranchEntity,
        Long,
        BranchReactiveRepository
> implements BranchRepository {
    public BranchRepositoryAdapter(BranchReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.mapBuilder(d, Branch.BranchBuilder.class).build());
    }

}
