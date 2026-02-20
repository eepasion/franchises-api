package co.com.bancolombia.usecase.updatefranchisename;

import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateFranchiseNameUseCase {
    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> updateName(Long productId, String newName) {
        return franchiseRepository.findById(productId)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.B404001)))
                .map(franchise -> franchise.toBuilder().name(newName).build())
                .flatMap(franchiseRepository::save);
    }
}
