package co.com.bancolombia.usecase.addproducttobranch;

import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AddProductToBranchUseCase {
    private final BranchRepository branchGateway;
    private final ProductRepository productGateway;

    public Mono<Product> addProduct(Long branchId, Product product){
        return branchGateway.findById(branchId)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.B404002)))
                .flatMap(_ -> {
                    product.setBranchId(branchId);
                    return productGateway.save(product);
                });
    }
}
