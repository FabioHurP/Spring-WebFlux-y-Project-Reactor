package com.fabio.customer_manager.services;

import com.fabio.customer_manager.dtos.RatingRequest;
import com.fabio.customer_manager.exceptions.RetryableStreamException;
import com.fabio.customer_manager.exceptions.StreamTerminateException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamService {

    //Message es una abstraccion de Spring para manejar mensajes Kafka, RabbitMq y Spring Messages
    //Emitir de manera Asincrona elementos de q vayan llegando por nuestro controlador
    private final Sinks.Many<Message<String>> ratingRequestSink;
    //Se maneja un objectMapper porque Spring puede tener problemas con la serializacion, el support object serializer para kafka
    private final ObjectMapper objectMapper;

    public Mono<Object> sendRatingToStream(RatingRequest request) {

        return Mono.fromCallable(() -> this.objectMapper.writeValueAsString(request))//Convertir el request en String con formato JSON
                .flatMap(json -> {

                    //Message es una abstraccion de Spring para manejar mensajes Kafka, RabbitMq y Spring Messages
                    Message<String> message = MessageBuilder.withPayload(json)
                            .setHeader(KafkaHeaders.KEY, request.getIdRestaurant().toString().getBytes())
                            .setHeader("contentType", "text/plain")
                            .build();

                    Sinks.EmitResult result = this.ratingRequestSink.tryEmitNext(message);

                    if (result.isSuccess()) {
                        log.info("Rating request sent successfully");
                        return Mono.empty();
                    }

                    return Mono.error(this.mapEmitResult(result));
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                        .filter(ex -> ex instanceof RetryableStreamException)
                        .onRetryExhaustedThrow((spec, sig) -> sig.failure())
                )
                .doOnError(err -> log.error(err.getMessage(), err));
    }

    private RuntimeException mapEmitResult(Sinks.EmitResult result) {
        return switch (result) {
            case FAIL_OVERFLOW, FAIL_NON_SERIALIZED ->
                new RetryableStreamException("Transient sink");

            case FAIL_CANCELLED, FAIL_TERMINATED ->
               new StreamTerminateException("Sink terminated");

            default -> new  RuntimeException("Unexpected sink result");
        };
    }
}
