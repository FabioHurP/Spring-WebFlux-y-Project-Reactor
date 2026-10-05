package com.fabio.eats_hub_catalog.validators;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReactiveValidator {

    //Hace referencia al validator de la carpeta configs/ValidatorConfig
    private final Validator validator;

    public <T> Mono<T> validate(T object) {
        final var setViolations = validator.validate(object);

        if (setViolations.isEmpty()) {
            return Mono.just(object);
        }

        final var errors = setViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        return Mono.error(new ValidationException(errors));
    }
}