package co.com.bancolombia.model.product.gateways;

import co.com.bancolombia.model.product.Product;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> save(Product product);
    Mono<Product> findById(Long id);
    Mono<Void> deleteById(Long id);

}
