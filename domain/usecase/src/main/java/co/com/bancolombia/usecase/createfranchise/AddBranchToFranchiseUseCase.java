package co.com.bancolombia.usecase.createfranchise;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AddBranchToFranchiseUseCase {
    private final FranchiseRepository franchiseGateway;
    private final BranchRepository branchGateway;

    public Mono<Branch> addBranch(Long franchiseId, Branch branch){
        return franchiseGateway.findById(franchiseId)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.B404001)))
                .flatMap(_ -> {
                    branch.setFranchiseId(franchiseId);
                    return branchGateway.save(branch);
                });
    }
}
