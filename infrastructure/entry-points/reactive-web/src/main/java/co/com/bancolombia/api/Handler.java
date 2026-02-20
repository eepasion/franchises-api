package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.AddBranchRequest;
import co.com.bancolombia.api.dto.request.AddProductRequest;
import co.com.bancolombia.api.dto.request.CreateFranchiseRequest;
import co.com.bancolombia.api.dto.request.UpdateProductStockRequest;
import co.com.bancolombia.api.helper.ValidationUtil;
import co.com.bancolombia.api.mapper.BranchMapper;
import co.com.bancolombia.api.mapper.FranchiseMapper;
import co.com.bancolombia.api.mapper.ProductMapper;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.usecase.addbranchtofranchise.AddBranchToFranchiseUseCase;
import co.com.bancolombia.usecase.addproducttobranch.AddProductToBranchUseCase;
import co.com.bancolombia.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.deleteproduct.DeleteProductUseCase;
import co.com.bancolombia.usecase.updateproductstock.UpdateProductStockUseCase;
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
    private final AddProductToBranchUseCase addProductToBranchUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;


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

    public Mono<ServerResponse> addProductToBranch(ServerRequest serverRequest) {
        String branchIdStr = serverRequest.pathVariable("branchId");
        return Mono.fromCallable(() -> Long.parseLong(branchIdStr))
                .onErrorMap(NumberFormatException.class,
                        e -> new BusinessException(ErrorCode.B400001, "Invalid branch ID"))
                .flatMap(branchId ->
                        serverRequest.bodyToMono(AddProductRequest.class)
                                .flatMap(validationUtil::validate)
                                .map(ProductMapper::toDomain)
                                .flatMap(product -> addProductToBranchUseCase.addProduct(branchId, product))
                                .map(ProductMapper::toDto)
                                .flatMap(product ->
                                        ServerResponse.status(HttpStatus.CREATED).bodyValue(product))
                );
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest serverRequest) {
        String productIdStr = serverRequest.pathVariable("productId");
        return Mono.fromCallable(() -> Long.parseLong(productIdStr))
                .onErrorMap(NumberFormatException.class,
                        e -> new BusinessException(ErrorCode.B400001, "Invalid product ID"))
                .flatMap(deleteProductUseCase::deleteProduct)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest serverRequest) {
        String productIdStr = serverRequest.pathVariable("productId");
        return Mono.fromCallable(() -> Long.parseLong(productIdStr))
                .onErrorMap(NumberFormatException.class,
                        e -> new BusinessException(ErrorCode.B400001, "Invalid product ID"))
                .flatMap(productId ->
                        serverRequest.bodyToMono(UpdateProductStockRequest.class)
                                .flatMap(validationUtil::validate)
                                .flatMap(request -> updateProductStockUseCase.updateStock(productId, request.getStock()))
                                .map(ProductMapper::toDto)
                                .flatMap(product ->
                                        ServerResponse.ok().bodyValue(product))
                );
    }
}
