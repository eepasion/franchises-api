package co.com.bancolombia.r2dbc.repository;

import co.com.bancolombia.r2dbc.entity.ProductEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ProductReactiveRepository extends ReactiveCrudRepository<ProductEntity, Long>, ReactiveQueryByExampleExecutor<ProductEntity> {

    @Query("SELECT p.* FROM products p " +
            "INNER JOIN branches b ON p.branch_id = b.id " +
            "WHERE b.franchise_id = :franchiseId " +
            "AND p.id = (SELECT id FROM products p2 WHERE p2.branch_id = p.branch_id AND p2.stock = " +
            "(SELECT MAX(p3.stock) FROM products p3 WHERE p3.branch_id = p.branch_id) ORDER BY p2.id ASC LIMIT 1)")
    Flux<ProductEntity> findTopStockProductsByFranchise(Long franchiseId);
}
