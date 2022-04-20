package com.restautantvote.repository;

import com.restautantvote.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface RestaurantRepository extends JpaRepository<Restaurant,Integer> {


    @Query("SELECT restaurants from Restaurant restaurants"+
            "  JOIN FETCH  restaurants.menu menus " +
            "where menus.created=:date "  )
        List<Restaurant> getAllForDate(LocalDate date);


    @Query("SELECT restaurant from Restaurant restaurant " +
            " Join fetch restaurant.menu menus  where menus.created = " +
            "(select MAX(menu.created)  from Menu menu where menu.restaurant.id = restaurant.id )" )
        List<Restaurant> getAllWithMaxDate();

    @Query("SELECT restaurant from Restaurant restaurant"+
            "  JOIN FETCH  restaurant.menu menus " +
            "where restaurant.id =:id and menus.created=:date"  )
    Optional<Restaurant> getOneInfo(Integer id,LocalDate date);


    @Query("SELECT restaurant from Restaurant  restaurant " +
            "Join fetch restaurant.menu menu  " +
            "where restaurant.id=:restaurantId order by menu.created desc ")
    Optional<Restaurant> getMenuHistory(Integer restaurantId);


}
