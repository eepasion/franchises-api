package co.com.bancolombia.usecase.gettopstockproductsbyfranchise;

import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.ProductWithBranch;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetTopStockProductsByFranchiseUseCase {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public Flux<ProductWithBranch> getTopStockProductsByFranchise(Long franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.B404001)))
                .flatMapMany(franchise ->
                        productRepository.findTopStockByBranchesInFranchise(franchiseId)
                ).flatMap(product -> branchRepository.findById(product.getBranchId())
                        .map(branch -> ProductWithBranch.builder()
                                .product(product)
                                .branch(branch)
                                .build())
                );
    }

}
