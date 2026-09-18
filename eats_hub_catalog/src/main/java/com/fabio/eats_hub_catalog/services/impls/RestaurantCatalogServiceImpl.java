package com.fabio.eats_hub_catalog.services.impls;

import com.fabio.eats_hub_catalog.collections.RestaurantCollection;
import com.fabio.eats_hub_catalog.enums.PriceEnum;
import com.fabio.eats_hub_catalog.records.Address;
import com.fabio.eats_hub_catalog.repositories.RestaurantRepository;
import com.fabio.eats_hub_catalog.services.definitions.RestaurantCatalogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantCatalogServiceImpl implements RestaurantCatalogService {

    private final RestaurantRepository restaurantRepository;


    @Override
    public Flux<RestaurantCollection> readAll() {
        return this.restaurantRepository.findAll();
    }

    @Override
    public Flux<RestaurantCollection> readByCuisineType(String cuisineType) {
        return this.restaurantRepository.findByCuisineType(cuisineType)
                .doOnSubscribe(subscription -> log.info("Init search with param: {}", cuisineType))
                .doOnNext(restaurant -> log.info("Found with param: {}", restaurant.getName()))
                .onErrorResume(throwable -> {
                    log.error(throwable.getMessage(), throwable);
                    return Flux.empty();
                });
    }

    @Override
    public Mono<RestaurantCollection> readByName(String name) {
     return this.restaurantRepository.findByNameStartingWithIgnoreCase(name)
             .doOnSubscribe(subscription -> log.info("Init search start with param: {}", name))
             //.doOnComplete(() -> log.info("Search finish with param: {}", name))
             .onErrorResume(throwable -> {
                 log.error(throwable.getMessage(), throwable);
                 return Mono.empty();
             });
            //  .doOnSubscribe(subscription -> log.info("Init search start with param: {}", name))
            //  .doOnComplete(() -> log.info("Search finish with param: {}", name))
            //  .onErrorResume(throwable -> {
            //      log.error(throwable.getMessage(), throwable);
            //      return Mono.empty();
            //  });
    }

    @Override
    public Flux<RestaurantCollection> readByPriceRangeIn(List<PriceEnum> priceRanges) {
        return this.restaurantRepository.findByPriceRangeIn(priceRanges)
                .switchIfEmpty(Flux.empty().cast(RestaurantCollection.class)
                        .doOnSubscribe(s -> log.info("Restaurants is empty")));

    }

    @Override
    public Flux<RestaurantCollection> readByCity(String city) {
        return this.restaurantRepository.findAll()
                .map(RestaurantCollection::getAddress) //restaurant -> restaurant.getAddress()
                .filter(Objects::nonNull) //address -> address != null
                .map(Address::city)
                .filter(Objects::nonNull) // cityName -> cityName != null
                .distinct() //Se quitan los repetidos en caso de haber
                .collectList() //LO metemos en un flux de listas
                .flatMapMany(cities -> {

                    if (cities.isEmpty()) {
                        log.info("No restaurants found in city: {}", city);
                        return Flux.empty();
                    }

                    log.info("Init search in city: {}", city);

                    return this.restaurantRepository.findByAddressCity(city)
                            .doOnNext(restaurant -> log.info("Found restaurant in city: {}, with param: {}", city, restaurant.getName()));
                })
                .onErrorResume(throwable -> {
                    log.error(throwable.getMessage(), throwable);
                    return Flux.empty();
                });
    }

    // @Override
    // public Flux<RestaurantCollection> readByCity2(String city) {
    //     return this.restaurantRepository.findByAddressCity(city)
    //     .doOnNext(restautant -> log.info("Found restaurant in city: {} with name: {}", city, restautant))
    //     .onErrorResume(throwable -> {
    //         log.error(throwable.getMessage(), throwable);
    //         return Flux.empty();
    //     });

    // }
}
