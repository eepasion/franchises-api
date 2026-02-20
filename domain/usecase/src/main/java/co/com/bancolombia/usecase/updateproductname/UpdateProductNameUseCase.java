package co.com.bancolombia.usecase.updateproductname;

import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductNameUseCase {
    private final ProductRepository productRepository;

    public Mono<Product> updateName(Long productId, String newName) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.B404003)))
                .map(product -> product.toBuilder().name(newName).build())
                .flatMap(productRepository::save);
    }
}
