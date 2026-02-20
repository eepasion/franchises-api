package co.com.bancolombia.api.helper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ValidationUtil {
    private final Validator validator;

    public <T> Mono<T> validate(T target){
        Set<ConstraintViolation<T>> violations = validator.validate(target);
        if(!violations.isEmpty()){
            return Mono.error(new ConstraintViolationException(violations));
        }
        return Mono.just(target);
    }

}
