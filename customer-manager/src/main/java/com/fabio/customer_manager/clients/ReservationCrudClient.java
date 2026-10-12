package com.fabio.customer_manager.clients;

import com.fabio.customer_manager.dtos.ReservationRequest;
import com.fabio.customer_manager.dtos.ReservationResponse;
import reactor.core.publisher.Mono;

public interface ReservationCrudClient {

    Mono<String> create(ReservationRequest reservationRequest);
    Mono<ReservationResponse> read(String uuid);
    Mono<ReservationResponse> update(String uuid, ReservationRequest reservationRequest);
    Mono<Void> delete(String uuid);
}
