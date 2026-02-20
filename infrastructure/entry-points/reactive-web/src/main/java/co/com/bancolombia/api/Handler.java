package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.AddBranchRequest;
import co.com.bancolombia.api.dto.request.AddProductRequest;
import co.com.bancolombia.api.dto.request.CreateFranchiseRequest;
import co.com.bancolombia.api.dto.request.UpdateBranchNameRequest;
import co.com.bancolombia.api.dto.request.UpdateFranchiseNameRequest;
import co.com.bancolombia.api.dto.request.UpdateProductNameRequest;
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
import co.com.bancolombia.usecase.gettopstockproductsbyfranchise.GetTopStockProductsByFranchiseUseCase;
import co.com.bancolombia.usecase.updatebranchname.UpdateBranchNameUseCase;
import co.com.bancolombia.usecase.updatefranchisename.UpdateFranchiseNameUseCase;
import co.com.bancolombia.usecase.updateproductname.UpdateProductNameUseCase;
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
    private final GetTopStockProductsByFranchiseUseCase topStockProductsByFranchiseUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private static final String FRANCHISE_PATH_VARIABLE = "franchiseId";
    private static final String BRANCH_PATH_VARIABLE = "branchId";
    private static final String PRODUCT_PATH_VARIABLE = "productId";
    private static final String INVALID_FRANCHISE_ID = "Invalid franchise ID";
    private static final String INVALID_BRANCH_ID = "Invalid branch ID";
    private static final String INVALID_PRODUCT_ID = "Invalid product ID";

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
        String franchiseIdStr = serverRequest.pathVariable(FRANCHISE_PATH_VARIABLE);
        return Mono.fromCallable(() -> Long.parseLong(franchiseIdStr))
                .onErrorMap(NumberFormatException.class,
                        e -> new BusinessException(ErrorCode.B400001, INVALID_FRANCHISE_ID))
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
        String branchIdStr = serverRequest.pathVariable(BRANCH_PATH_VARIABLE);
        return Mono.fromCallable(() -> Long.parseLong(branchIdStr))
                .onErrorMap(NumberFormatException.class,
                        e -> new BusinessException(ErrorCode.B400001, INVALID_BRANCH_ID))
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
        String productIdStr = serverRequest.pathVariable(PRODUCT_PATH_VARIABLE);
        return Mono.fromCallable(() -> Long.parseLong(productIdStr))
                .onErrorMap(NumberFormatException.class,
                        e -> new BusinessException(ErrorCode.B400001, INVALID_PRODUCT_ID))
                .flatMap(deleteProductUseCase::deleteProduct)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateProductStock(ServerRequest serverRequest) {
        String productIdStr = serverRequest.pathVariable(PRODUCT_PATH_VARIABLE);
        return Mono.fromCallable(() -> Long.parseLong(productIdStr))
                .onErrorMap(NumberFormatException.class,
                        e -> new BusinessException(ErrorCode.B400001, INVALID_PRODUCT_ID))
                .flatMap(productId ->
                        serverRequest.bodyToMono(UpdateProductStockRequest.class)
                                .flatMap(validationUtil::validate)
                                .flatMap(request -> updateProductStockUseCase.updateStock(productId, request.getStock()))
                                .map(ProductMapper::toDto)
                                .flatMap(product ->
                                        ServerResponse.ok().bodyValue(product))
                );
    }

    public Mono<ServerResponse> getTopStockProductsByFranchise(ServerRequest serverRequest) {
        String franchiseIdStr = serverRequest.pathVariable(FRANCHISE_PATH_VARIABLE);
        return Mono.fromCallable(() -> Long.parseLong(franchiseIdStr))
                .onErrorMap(NumberFormatException.class,
                        e -> new BusinessException(ErrorCode.B400001, INVALID_FRANCHISE_ID))
                .flatMapMany(topStockProductsByFranchiseUseCase::getTopStockProductsByFranchise)
                .map(ProductMapper::toDtoWithBranch)
                .collectList()
                .flatMap(products ->
                        ServerResponse.ok().bodyValue(products));
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest serverRequest) {
        String franchiseIdStr = serverRequest.pathVariable(FRANCHISE_PATH_VARIABLE);
        return Mono.fromCallable(() -> Long.parseLong(franchiseIdStr))
                .onErrorMap(NumberFormatException.class,
                        e -> new BusinessException(ErrorCode.B400001, INVALID_PRODUCT_ID))
                .flatMap(franchiseId ->
                        serverRequest.bodyToMono(UpdateFranchiseNameRequest.class)
                                .flatMap(validationUtil::validate)
                                .flatMap(request -> updateFranchiseNameUseCase.updateName(franchiseId, request.getName()))
                                .map(FranchiseMapper::toDto)
                                .flatMap(franchise ->
                                        ServerResponse.ok().bodyValue(franchise))
                );
    }

    public Mono<ServerResponse> updateBranchName(ServerRequest serverRequest) {
        String branchIdStr = serverRequest.pathVariable(BRANCH_PATH_VARIABLE);
        return Mono.fromCallable(() -> Long.parseLong(branchIdStr))
                .onErrorMap(NumberFormatException.class,
                        e -> new BusinessException(ErrorCode.B400001, INVALID_PRODUCT_ID))
                .flatMap(branchId ->
                        serverRequest.bodyToMono(UpdateBranchNameRequest.class)
                                .flatMap(validationUtil::validate)
                                .flatMap(request -> updateBranchNameUseCase.updateName(branchId, request.getName()))
                                .map(BranchMapper::toDto)
                                .flatMap(branch ->
                                        ServerResponse.ok().bodyValue(branch))
                );
    }

    public Mono<ServerResponse> updateProductName(ServerRequest serverRequest) {
        String productIdStr = serverRequest.pathVariable(PRODUCT_PATH_VARIABLE);
        return Mono.fromCallable(() -> Long.parseLong(productIdStr))
                .onErrorMap(NumberFormatException.class,
                        e -> new BusinessException(ErrorCode.B400001, INVALID_PRODUCT_ID))
                .flatMap(productId ->
                        serverRequest.bodyToMono(UpdateProductNameRequest.class)
                                .flatMap(validationUtil::validate)
                                .flatMap(request -> updateProductNameUseCase.updateName(productId, request.getName()))
                                .map(ProductMapper::toDto)
                                .flatMap(branch ->
                                        ServerResponse.ok().bodyValue(branch))
                );
    }
}
