package co.com.bancolombia.api.config;

import co.com.bancolombia.api.Handler;
import co.com.bancolombia.api.RouterRest;
import co.com.bancolombia.api.helper.ValidationUtil;
import co.com.bancolombia.usecase.addbranchtofranchise.AddBranchToFranchiseUseCase;
import co.com.bancolombia.usecase.addproducttobranch.AddProductToBranchUseCase;
import co.com.bancolombia.usecase.createfranchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.deleteproduct.DeleteProductUseCase;
import co.com.bancolombia.usecase.gettopstockproductsbyfranchise.GetTopStockProductsByFranchiseUseCase;
import co.com.bancolombia.usecase.updateproductstock.UpdateProductStockUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ValidationUtil validationUtil;

    @MockitoBean
    private CreateFranchiseUseCase createFranchiseUseCase;

    @MockitoBean
    private AddBranchToFranchiseUseCase addBranchToFranchiseUseCase;

    @MockitoBean
    private AddProductToBranchUseCase addProductToFranchiseUseCase;

    @MockitoBean
    private DeleteProductUseCase deleteProductUseCase;

    @MockitoBean
    private UpdateProductStockUseCase updateProductStockUseCase;

    @MockitoBean
    private GetTopStockProductsByFranchiseUseCase getTopStockProductsByFranchiseUseCase;

    @Test
    void corsConfigurationShouldAllowOrigins() {
        when(getTopStockProductsByFranchiseUseCase.getTopStockProductsByFranchise(1L))
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/franchises/1/top-products")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}