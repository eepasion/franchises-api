package co.com.bancolombia.usecase.createfranchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateFranchiseUseCase {
    private final FranchiseRepository gateway;

    public Mono<Franchise> save(Franchise franchise){
        return gateway.save(franchise);
    }
}
