package co.com.bancolombia.r2dbc.adapter;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import co.com.bancolombia.r2dbc.entity.ProductEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.repository.ProductReactiveRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class ProductRepositoryAdapter extends ReactiveAdapterOperations<
        Product,
        ProductEntity,
        Long,
        ProductReactiveRepository
> implements ProductRepository {
    public ProductRepositoryAdapter(ProductReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.mapBuilder(d, Product.ProductBuilder.class).build());
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<Product> findTopStockByBranchesInFranchise(Long franchiseId) {
        return repository.findTopStockProductsByFranchise(franchiseId)
                .map(this::toEntity);
    }
}
