package co.com.bancolombia.usecase.updateproductstock;

import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductStockUseCase {
    private final ProductRepository productRepository;

    public Mono<Product> updateStock(Long productId, Integer newStock) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.B404003)))
                .map(product -> product.toBuilder().stock(newStock).build())
                .flatMap(productRepository::save);
    }
}
