package com.restautantvote.services;

import com.restautantvote.dto.RestaurantIsUpdatedInfo;
import com.restautantvote.dto.RestaurantMenuInfo;
import com.restautantvote.model.Meal;
import com.restautantvote.model.Restaurant;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AdminServiceTest extends AbstractService {


    @Autowired
    private AdminService adminService;

    @Test
    void delete() {
        adminService.delete(3);
        Assert.assertThrows(IllegalArgumentException.class,()-> adminService.getOne(3)) ;
    }

    @Test
    void delete_IdIsNotExist() {
         Assert.assertThrows(IllegalArgumentException.class,()-> adminService.delete(300)) ;
    }

    @Test
    void getOne() {
        Restaurant restaurant =  adminService.getOne(3);
        Assertions.assertEquals("new restaurant", restaurant.getName());
    }

    @Test
    void save() {
        Restaurant restaurant = new Restaurant("test");
        adminService.save(restaurant);
        Assertions.assertEquals("test", adminService.getOne(4).getName());
    }

    @Test
    void save_NotNewEntity() {
        Restaurant restaurant = new Restaurant(5,"test");
        Assert.assertThrows(IllegalArgumentException.class,()-> adminService.save(restaurant)) ;
    }

    @Test
    void updateRestaurant() {
        Restaurant restaurant = new Restaurant(3,"updatedName");
        adminService.updateRestaurant(restaurant);
        Assertions.assertEquals("updatedName", adminService.getOne(3).getName());
    }

    @Test
    void getAllUpdated() {
        List<RestaurantIsUpdatedInfo> updatedInfoList = adminService.getAllUpdated();
        Assertions.assertEquals(3, updatedInfoList.size());
        RestaurantIsUpdatedInfo ispdatedRestaurant = updatedInfoList.stream().filter(RestaurantIsUpdatedInfo::getUpdated).findAny().get();
        Assertions.assertEquals(1, ispdatedRestaurant.getId().intValue());
        RestaurantIsUpdatedInfo yestUpdateRestaurant = updatedInfoList.stream().
                filter(rest->rest.getLastMenu().equals(LocalDate.now().minusDays(1))).findAny().get();
        Assertions.assertEquals(2, yestUpdateRestaurant.getId().intValue());
        RestaurantIsUpdatedInfo emptyMenuRestaurant = updatedInfoList.stream().filter(rest->(rest.getLastMenu().isEqual(LocalDate.now()) && !rest.getUpdated())).findAny().get();
        Assertions.assertEquals(3, emptyMenuRestaurant.getId().intValue());
    }


    @Test
    void addMealToMenu() {
        Meal meal1 = new Meal("testmeal1",new BigDecimal("10"));
        Meal meal2 = new Meal("testmeal2",new BigDecimal("20.5"));
        List<Meal> meals = Arrays.asList(meal1,meal2);
        adminService.addMealToMenu(3,meals);
        RestaurantMenuInfo restaurantMenuInfo = adminService.getOneInfo(3);
        List<Meal> savedMeals =  restaurantMenuInfo.getMealList();
        savedMeals.forEach(savedMeal-> {
                        assertThat(savedMeal.getDescription()).startsWith("test");
                        Assertions.assertNotNull(savedMeal.getId());}
                );
    }


    @Test
    void addMealToMenu_NotNewMeals() {
        Meal meal1 = new Meal(10,"testmeal1",new BigDecimal("10"));
        Meal meal2 = new Meal(11,"testmeal2",new BigDecimal("20.5"));
        List<Meal> meals = Arrays.asList(meal1,meal2);
        Assert.assertThrows(IllegalArgumentException.class,()-> adminService.addMealToMenu(3,meals));

    }

    @Test
    void deleteMeals_NotExist() {
        Assert.assertThrows(IllegalArgumentException.class,()-> adminService.deleteMeals(3,Arrays.asList(101,102,103)));
    }
}