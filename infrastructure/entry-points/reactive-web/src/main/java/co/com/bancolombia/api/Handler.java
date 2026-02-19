package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.CreateFranchiseRequest;
import co.com.bancolombia.api.dto.response.CreateFranchiseResponse;
import co.com.bancolombia.api.helper.ValidationUtil;
import co.com.bancolombia.api.mapper.FranchiseMapper;
import co.com.bancolombia.usecase.createfranchise.CreateFranchiseUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final ValidationUtil validationUtil;
    private final CreateFranchiseUseCase createFranchiseUseCase;


    @RouterOperations({
            @RouterOperation(
                    path = "/api/franchises",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Create a new franchise",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = CreateFranchiseRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Franchise created successfully",
                                            content = @Content(schema = @Schema(implementation = CreateFranchiseResponse.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Invalid request")
                            }
                    )
            )
    })
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
