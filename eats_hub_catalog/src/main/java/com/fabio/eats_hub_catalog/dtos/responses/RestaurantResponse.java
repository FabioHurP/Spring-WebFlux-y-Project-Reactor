package com.fabio.eats_hub_catalog.dtos.responses;


import com.fabio.eats_hub_catalog.enums.PriceEnum;
import com.fabio.eats_hub_catalog.records.Address;
import com.fabio.eats_hub_catalog.records.ContactInfo;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RestaurantResponse {

    private String name;
    private Address address;
    private String cuisineType;
    private PriceEnum priceRange;
    private String openHours;
    private String logoUrl;
    private String closeAt;
    private ContactInfo contactInfo;
    private Double globalRating; //Con este campo se va a hacer uso del MapStruct
}

