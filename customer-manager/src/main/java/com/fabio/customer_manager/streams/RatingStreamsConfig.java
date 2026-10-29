package com.fabio.customer_manager.streams;

import com.fabio.customer_manager.dtos.RatingEvent;
import com.fabio.customer_manager.dtos.RatingRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RatingStreamsConfig {

    private final ObjectMapper objectMapper;

    private static final String CREATED_RATING_EVENT_NAME = "RATING_CREATED";

    @Bean
    public Sinks.Many<Message<String>> ratingRequestSink() {
        log.info("Creating rating sink");
        //multicast() porque pueden existir varios subscriptores
        //Buffer guarda los mensaje cuando exista saturacion de msg
        return Sinks.many().multicast().onBackpressureBuffer(1_000);

    }

    /*
      //This supplier is to send msg to kafka(procedure) --> Un productor de mensajes
     */
    @Bean
    public Supplier<Flux<Message<String>>> ratingRequestSupplier(Sinks.Many<Message<String>> sink) {
        return () -> sink.asFlux()
                .doOnSubscribe(s -> log.info("Subscribing to rating sink"))
                .doOnCancel(() -> log.info("Canceling rating sink"));
    }
    /*
       *input is a json string rating request y el output is json string rating event
       *la interfaz Function permite transformar datos, nuestra msg ingresa como un request y sale como un event de KAFKA
     */
    @Bean
    public Function<Flux<Message<String>>, Flux<Message<String>>> ratingTransformer() {
        return flux -> flux.<Message<String>>handle((msg, sink) -> {

              try {
                  RatingRequest ratingRequest = objectMapper.readValue(msg.getPayload(), RatingRequest.class);

                  RatingEvent ratingEvent = RatingEvent.builder()
                          .uuidCustomer(ratingRequest.getUuidCustomer().toString())
                          .idRestaurant(ratingRequest.getIdRestaurant().toString())
                          .comment(ratingRequest.getComment())
                          .rating(ratingRequest.getRating())
                          .eventType(CREATED_RATING_EVENT_NAME)
                          .build();

                  Message<String> messageOut = MessageBuilder.withPayload(
                                  this.objectMapper.writeValueAsString(ratingEvent))
                          .setHeader(KafkaHeaders.KEY, ratingRequest.getIdRestaurant().toString().getBytes())
                          .setHeader("contentType", "text/plain")
                          .build();

                  sink.next(messageOut);
              } catch (Exception e) {
                  log.error("Error while converting rating event", e);
              }
        });
    }

}
