package com.restautantvote.repository;

import com.restautantvote.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu,Integer>
{
        @Query("Select menu from Menu menu where menu.restaurant.id =:restaurantId " +
                "order by menu.created desc")
        List<Menu> getMenuHistory(Integer restaurantId);


}
