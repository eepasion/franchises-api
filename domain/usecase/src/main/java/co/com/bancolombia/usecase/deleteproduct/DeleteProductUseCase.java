package co.com.bancolombia.usecase.deleteproduct;

import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteProductUseCase {
    private final ProductRepository productGateway;

    public Mono<Void> deleteProduct(Long productId) {
        return productGateway.findById(productId)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.B404003)))
                .flatMap(product -> productGateway.deleteById(productId));
    }
}
