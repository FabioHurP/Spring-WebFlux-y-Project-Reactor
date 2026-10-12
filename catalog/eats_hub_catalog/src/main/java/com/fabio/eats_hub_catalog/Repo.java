package com.fabio.eats_hub_catalog;

import java.util.UUID;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.fabio.eats_hub_catalog.collections.RestaurantCollection;

public interface Repo extends ReactiveMongoRepository<RestaurantCollection, UUID> {
    

}
