package co.com.bancolombia.config;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {
        @Bean
        public FranchiseRepository franchiseRepository() {
            return new FranchiseRepository() {
                @Override
                public Mono<Franchise> save(Franchise franchise) {
                    return Mono.empty();
                }

                @Override
                public Mono<Franchise> findById(Long id) {
                    return Mono.empty();
                }
            };
        }

        @Bean
        public BranchRepository branchRepository() {
            return new BranchRepository() {
                @Override
                public Mono<Branch> save(Branch branch) {
                    return Mono.empty();
                }

                @Override
                public Mono<Branch> findById(Long id) {
                    return Mono.empty();
                }

            };
        }

        @Bean
        public ProductRepository productsRepository() {
            return new ProductRepository() {
                @Override
                public Mono<Product> save(Product products) {
                    return Mono.empty();
                }
                @Override
                public Mono<Product> findById(Long id){
                    return Mono.empty();
                }
                @Override
                public Mono<Void> deleteById(Long id){
                    return Mono.empty();
                }
                @Override
                public Flux<Product> findTopStockByBranchesInFranchise(Long franchiseId){
                    return Flux.empty();
                }
            };
        }
    }

}