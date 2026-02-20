package co.com.bancolombia.usecase.updatebranchname;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateBranchNameUseCase {
    private final BranchRepository branchRepository;

    public Mono<Branch> updateName(Long productId, String newName) {
        return branchRepository.findById(productId)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.B404002)))
                .map(franchise -> franchise.toBuilder().name(newName).build())
                .flatMap(branchRepository::save);
    }
}
