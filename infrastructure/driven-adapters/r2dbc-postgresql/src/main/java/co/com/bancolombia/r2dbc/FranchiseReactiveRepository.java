package co.com.bancolombia.r2dbc;

import co.com.bancolombia.r2dbc.entity.FranchiseEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface FranchiseReactiveRepository extends ReactiveCrudRepository<FranchiseEntity, String>, ReactiveQueryByExampleExecutor<FranchiseEntity> {

}
