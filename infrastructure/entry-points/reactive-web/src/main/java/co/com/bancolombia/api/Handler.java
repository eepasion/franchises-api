package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.CreateFranchiseRequest;
import co.com.bancolombia.api.helper.ValidationUtil;
import co.com.bancolombia.api.mapper.FranchiseMapper;
import co.com.bancolombia.usecase.createfranchise.CreateFranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final ValidationUtil validationUtil;
    private final CreateFranchiseUseCase createFranchiseUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateFranchiseRequest.class)
                .flatMap(validationUtil::validate)
                .map(FranchiseMapper::toDomain)
                .flatMap(createFranchiseUseCase::save)
                .map(FranchiseMapper::toDto)
                .flatMap(franchise ->
                        ServerResponse.status(HttpStatus.CREATED).bodyValue(franchise));
    }
}
