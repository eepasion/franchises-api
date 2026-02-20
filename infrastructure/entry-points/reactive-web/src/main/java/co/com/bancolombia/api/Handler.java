package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.AddBranchRequest;
import co.com.bancolombia.api.dto.request.CreateFranchiseRequest;
import co.com.bancolombia.api.helper.ValidationUtil;
import co.com.bancolombia.api.mapper.BranchMapper;
import co.com.bancolombia.api.mapper.FranchiseMapper;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.usecase.addbranchtofranchise.AddBranchToFranchiseUseCase;
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
    private final AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;


    public Mono<ServerResponse> createFranchise(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateFranchiseRequest.class)
                .flatMap(validationUtil::validate)
                .map(FranchiseMapper::toDomain)
                .flatMap(createFranchiseUseCase::save)
                .map(FranchiseMapper::toDto)
                .flatMap(franchise ->
                        ServerResponse.status(HttpStatus.CREATED).bodyValue(franchise));
    }

    public Mono<ServerResponse> addBranchToFranchise(ServerRequest serverRequest) {
        String franchiseIdStr = serverRequest.pathVariable("franchiseId");
        return Mono.fromCallable(() -> Long.parseLong(franchiseIdStr))
                .onErrorMap(NumberFormatException.class,
                        e -> new BusinessException(ErrorCode.B400001, "Invalid franchise ID"))
                .flatMap(franchiseId ->
                        serverRequest.bodyToMono(AddBranchRequest.class)
                                .flatMap(validationUtil::validate)
                                .map(BranchMapper::toDomain)
                                .flatMap(branch -> addBranchToFranchiseUseCase.addBranch(franchiseId, branch))
                                .map(BranchMapper::toDto)
                                .flatMap(branch ->
                                        ServerResponse.status(HttpStatus.CREATED).bodyValue(branch))
                );
    }
}
