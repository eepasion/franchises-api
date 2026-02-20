package co.com.bancolombia.api.helper;

import co.com.bancolombia.model.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Order(-2)
@RequiredArgsConstructor
@Slf4j
public class GlobalErrorHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public @NotNull Mono<Void> handle(@NotNull ServerWebExchange exchange, @NotNull Throwable ex) {

        var response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        if (ex instanceof BusinessException businessEx) {
            log.warn("Business error [{}]: {}", businessEx.getCode(), ex.getMessage());
            return writeResponse(response,
                    HttpStatus.valueOf(businessEx.getHttpStatus()),
                    businessEx.getCode(),
                    businessEx.getMessage());
        }

        if (ex instanceof ConstraintViolationException validationEx) {
            log.warn("Validation error: {}", ex.getMessage());
            return toListErrors(validationEx.getConstraintViolations())
                    .flatMap(errors -> writeResponse(response,
                            HttpStatus.BAD_REQUEST,
                            "B400-001",
                            String.join(", ", errors)));
        }

        log.error("An error has occurred: {}", ex.getMessage(), ex);
        return writeResponse(response,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "E500-001",
                "Internal Server Error");
    }

    private Mono<Void> writeResponse(org.springframework.http.server.reactive.ServerHttpResponse response,
                                     HttpStatus status, String code, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", code);
        errorResponse.put("status", status.value());
        errorResponse.put("message", message);

        return Mono.fromCallable(() -> objectMapper.writeValueAsBytes(errorResponse))
                .map(response.bufferFactory()::wrap)
                .flatMap(buffer -> {
                    response.setStatusCode(status);
                    return response.writeWith(Mono.just(buffer));
                })
                .onErrorResume(e -> {
                    log.error("Error writing response", e);
                    response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return response.setComplete();
                });
    }

    private Mono<List<String>> toListErrors(Set<ConstraintViolation<?>> violations) {
        return Flux.fromIterable(violations)
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collectList();
    }
}
