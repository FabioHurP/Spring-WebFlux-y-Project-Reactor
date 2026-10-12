package com.fabio.eats_hub_catalog.repositories;

import com.fabio.eats_hub_catalog.collections.RestaurantCollection;
import com.fabio.eats_hub_catalog.enums.PriceEnum;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface RestaurantRepository extends ReactiveMongoRepository<RestaurantCollection, UUID> {

    Flux<RestaurantCollection> findByCuisineType(String cuisineType);

    //@Query("{'name':  {$regex: '^?0', $options: 'i'   } }") La i es de init, de q inicie
    Mono<RestaurantCollection> findByNameStartingWithIgnoreCase(String name);

    Flux<RestaurantCollection> findByPriceRangeIn(List<PriceEnum> priceRanges);

    //AddressCity = address.city
    Flux<RestaurantCollection> findByAddressCity(String city);
}
