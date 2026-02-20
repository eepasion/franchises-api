package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.AddBranchRequest;
import co.com.bancolombia.api.dto.request.AddProductRequest;
import co.com.bancolombia.api.dto.request.CreateFranchiseRequest;
import co.com.bancolombia.api.dto.request.UpdateProductStockRequest;
import co.com.bancolombia.api.dto.response.BranchResponse;
import co.com.bancolombia.api.dto.response.CreateFranchiseResponse;
import co.com.bancolombia.api.dto.response.ProductResponse;
import co.com.bancolombia.api.dto.response.ProductWithBranchResponse;
import co.com.bancolombia.api.helper.GlobalErrorHandler;
import co.com.bancolombia.api.helper.ValidationUtil;
import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.exception.ErrorCode;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.ProductWithBranch;
import co.com.bancolombia.usecase.addbranchtofranchise.AddBranchToFranchiseUseCase;
import co.com.bancolombia.usecase.addproducttobranch.AddProductToBranchUseCase;
import co.com.bancolombia.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.deleteproduct.DeleteProductUseCase;
import co.com.bancolombia.usecase.gettopstockproductsbyfranchise.GetTopStockProductsByFranchiseUseCase;
import co.com.bancolombia.usecase.updateproductstock.UpdateProductStockUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, ValidationUtil.class, GlobalErrorHandler.class, RouterRestTest.Config.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateFranchiseUseCase createFranchiseUseCase;

    @MockitoBean
    private AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;

    @MockitoBean
    private AddProductToBranchUseCase addProductToBranchUseCase;

    @MockitoBean
    private DeleteProductUseCase deleteProductUseCase;

    @MockitoBean
    private UpdateProductStockUseCase updateProductStockUseCase;

    @MockitoBean
    private GetTopStockProductsByFranchiseUseCase getTopStockProductsByFranchiseUseCase;

    @TestConfiguration
    static class Config {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Test
    void testCreateFranchise() {
        Franchise savedFranchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        when(createFranchiseUseCase.save(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchise));

        CreateFranchiseRequest request = CreateFranchiseRequest.builder()
                .name("Test Franchise")
                .build();

        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CreateFranchiseResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull()
                            .extracting(
                                    CreateFranchiseResponse::getId,
                                    CreateFranchiseResponse::getName
                            ).containsExactly(1L, "Test Franchise");
                });
    }

    @Test
    void testCreateFranchiseWithInvalidName() {
        CreateFranchiseRequest request = CreateFranchiseRequest.builder()
                .name("")
                .build();

        webTestClient.post()
                .uri("/api/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testAddBranchToFranchise() {
        Branch savedBranch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .build();

        when(addBranchToFranchiseUseCase.addBranch(eq(1L), any(Branch.class)))
                .thenReturn(Mono.just(savedBranch));

        AddBranchRequest request = AddBranchRequest.builder()
                .name("Test Branch")
                .build();

        webTestClient.post()
                .uri("/api/franchises/1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BranchResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull()
                            .extracting(
                                    BranchResponse::getId,
                                    BranchResponse::getName,
                                    BranchResponse::getFranchiseId
                            ).containsExactly(1L, "Test Branch", 1L);
                });
    }

    @Test
    void testAddBranchToFranchiseWithInvalidId() {
        AddBranchRequest request = AddBranchRequest.builder()
                .name("Test Branch")
                .build();

        webTestClient.post()
                .uri("/api/franchises/invalid/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testAddBranchToFranchiseWithInvalidName() {
        AddBranchRequest request = AddBranchRequest.builder()
                .name("")
                .build();

        webTestClient.post()
                .uri("/api/franchises/1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testAddProductToBranch() {
        Product savedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(10)
                .branchId(1L)
                .build();

        when(addProductToBranchUseCase.addProduct(eq(1L), any(Product.class)))
                .thenReturn(Mono.just(savedProduct));

        AddProductRequest request = AddProductRequest.builder()
                .name("Test Product")
                .stock(10)
                .build();

        webTestClient.post()
                .uri("/api/branches/1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ProductResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull()
                            .extracting(
                                    ProductResponse::getId,
                                    ProductResponse::getName,
                                    ProductResponse::getStock,
                                    ProductResponse::getBranchId
                            ).containsExactly(1L, "Test Product", 10, 1L);
                });
    }

    @Test
    void testAddProductToBranchWithInvalidId() {
        AddProductRequest request = AddProductRequest.builder()
                .name("Test Product")
                .stock(10)
                .build();

        webTestClient.post()
                .uri("/api/branches/invalid/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testAddProductToBranchWithInvalidData() {
        AddProductRequest request = AddProductRequest.builder()
                .name("")
                .stock(-1)
                .build();

        webTestClient.post()
                .uri("/api/branches/1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Test
    void testDeleteProduct() {
        when(deleteProductUseCase.deleteProduct(1L))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/products/1")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    void testDeleteProductWithInvalidId() {
        webTestClient.delete()
                .uri("/api/products/invalid")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testDeleteProductNotFound() {
        when(deleteProductUseCase.deleteProduct(999L))
                .thenReturn(Mono.error(new BusinessException(ErrorCode.B404003)));

        webTestClient.delete()
                .uri("/api/products/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateProductStock() {
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(25)
                .branchId(1L)
                .build();

        when(updateProductStockUseCase.updateStock(1L, 25))
                .thenReturn(Mono.just(updatedProduct));

        UpdateProductStockRequest request = UpdateProductStockRequest.builder()
                .stock(25)
                .build();

        webTestClient.patch()
                .uri("/api/products/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull()
                            .extracting(
                                    ProductResponse::getId,
                                    ProductResponse::getName,
                                    ProductResponse::getStock,
                                    ProductResponse::getBranchId
                            ).containsExactly(1L, "Test Product", 25, 1L);
                });
    }

    @Test
    void testUpdateProductStockWithInvalidId() {
        UpdateProductStockRequest request = UpdateProductStockRequest.builder()
                .stock(25)
                .build();

        webTestClient.patch()
                .uri("/api/products/invalid/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdateProductStockWithInvalidStock() {
        UpdateProductStockRequest request = UpdateProductStockRequest.builder()
                .stock(-5)
                .build();

        webTestClient.patch()
                .uri("/api/products/1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdateProductStockNotFound() {
        when(updateProductStockUseCase.updateStock(999L, 25))
                .thenReturn(Mono.error(new BusinessException(ErrorCode.B404003)));

        UpdateProductStockRequest request = UpdateProductStockRequest.builder()
                .stock(25)
                .build();

        webTestClient.patch()
                .uri("/api/products/999/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testGetTopStockProductsByFranchise() {
        Branch branch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(50)
                .branchId(1L)
                .build();

        ProductWithBranch productWithBranch = ProductWithBranch.builder()
                .product(product)
                .branch(branch)
                .build();

        when(getTopStockProductsByFranchiseUseCase.getTopStockProductsByFranchise(1L))
                .thenReturn(Flux.just(productWithBranch));

        webTestClient.get()
                .uri("/api/franchises/1/top-products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductWithBranchResponse.class)
                .value(responses -> {
                    assertThat(responses).isNotNull();
                    ProductWithBranchResponse response = responses.getFirst();
                    assertThat(response.getProductId()).isEqualTo(1L);
                    assertThat(response.getProductName()).isEqualTo("Test Product");
                    assertThat(response.getStock()).isEqualTo(50);
                    assertThat(response.getBranchId()).isEqualTo(1L);
                    assertThat(response.getBranchName()).isEqualTo("Test Branch");
                });
    }

    @Test
    void testGetTopStockProductsByFranchiseWithInvalidId() {
        webTestClient.get()
                .uri("/api/franchises/invalid/top-products")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testGetTopStockProductsByFranchiseNotFound() {
        when(getTopStockProductsByFranchiseUseCase.getTopStockProductsByFranchise(999L))
                .thenReturn(Flux.error(new BusinessException(ErrorCode.B404001)));

        webTestClient.get()
                .uri("/api/franchises/999/top-products")
                .exchange()
                .expectStatus().isNotFound();
    }
}
