package com.restautantvote.utils;

import com.restautantvote.dto.RestaurantMenuInfo;
import com.restautantvote.model.Restaurant;
import com.restautantvote.model.Vote;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Util {
    private Util(){}

    public static RestaurantMenuInfo toMenuFromRestaurantList (Restaurant restaurant, List<Vote> votes, LocalDate date){

        return new RestaurantMenuInfo(
                restaurant.getId(),
                restaurant.getName(),
                (int) votes.stream().filter(res->(res.getRestaurant().getId().equals(restaurant.getId()))).count(),
                votes.stream().filter(res->res.getRestaurant().getId().equals(restaurant.getId())).mapToDouble(res->res.getRate()).average().orElse(0.0),
                date,
                restaurant.getMenu().stream().findFirst().get().getMeals());



    }

    public static List<RestaurantMenuInfo> toMenuHistoryListFromRestaurant(Restaurant restaurant, List<Vote> votes) {
       return restaurant.getMenu().stream().
                map(restaurantMenu->new RestaurantMenuInfo(
                        restaurant.getId(),
                        restaurant.getName(),
                        (int) votes.stream().filter(rest->rest.getDate().isEqual(restaurantMenu.getCreated())).count(),
                        votes.stream().filter(res-> res.getDate().isEqual(restaurantMenu.getCreated())).mapToDouble(res->res.getRate()).average().orElse(0.0),
                        restaurantMenu.getCreated(),restaurantMenu.getMeals())

                ).sorted(Comparator.comparing(RestaurantMenuInfo::getDate).reversed()).collect(Collectors.toList());
    }
}
