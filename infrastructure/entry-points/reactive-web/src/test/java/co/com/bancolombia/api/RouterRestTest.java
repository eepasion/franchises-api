package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.CreateFranchiseRequest;
import co.com.bancolombia.api.dto.response.CreateFranchiseResponse;
import co.com.bancolombia.api.helper.GlobalErrorHandler;
import co.com.bancolombia.api.helper.ValidationUtil;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.usecase.addbranchtofranchise.AddBranchToFranchiseUseCase;
import co.com.bancolombia.usecase.addproducttobranch.AddProductToBranchUseCase;
import co.com.bancolombia.usecase.createfranchise.CreateFranchiseUseCase;
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
import reactor.core.publisher.Mono;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
}
