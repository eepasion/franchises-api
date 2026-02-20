package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.request.AddBranchRequest;
import co.com.bancolombia.api.dto.request.CreateFranchiseRequest;
import co.com.bancolombia.api.dto.response.BranchResponse;
import co.com.bancolombia.api.dto.response.CreateFranchiseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/franchises",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Create a new franchise",
                            tags = {"Franchises"},
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
            ),
            @RouterOperation(
                    path = "/api/franchises/{franchiseId}/branches",
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "addBranchToFranchise",
                    operation = @Operation(
                            operationId = "addBranchToFranchise",
                            summary = "Add a new branch to an existing franchise",
                            tags = {"Branches"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = AddBranchRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Branch added successfully",
                                            content = @Content(schema = @Schema(implementation = BranchResponse.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/franchises"), handler::createFranchise)
                .andRoute(POST("/api/franchises/{franchiseId}/branches"), handler::addBranchToFranchise);
    }
}
