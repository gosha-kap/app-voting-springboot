package com.restautantvote.services;

import com.restautantvote.dto.RestaurantIsUpdatedInfo;
import com.restautantvote.dto.RestaurantMenuInfo;
import com.restautantvote.model.BaseEntity;
import com.restautantvote.model.Meal;
import com.restautantvote.model.Menu;
import com.restautantvote.model.Restaurant;
import com.restautantvote.repository.MealRepository;
import com.restautantvote.repository.MenuRepository;
import com.restautantvote.repository.RestaurantRepository;
import com.restautantvote.repository.VoteRepository;
import com.restautantvote.utils.ValidationUtil;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class AdminService {
    private final RestaurantRepository restaurantRepository;
    private final VoteRepository voteRepository;
    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;



    public List<RestaurantIsUpdatedInfo> getAllUpdated() {
        List<Restaurant> restaurantList = restaurantRepository.getAllWithMaxDate();
        return restaurantList.stream().map(restaurant -> new RestaurantIsUpdatedInfo(
                        restaurant.getId(),
                        restaurant.getName(),
                        restaurant.getMenu().stream().anyMatch(menu -> menu.getCreated().equals(LocalDate.now()) &&
                                ( Objects.nonNull(menu.getMeals()) && menu.getMeals().size() != 0 ) ),
                        restaurant.getMenu().stream().findFirst().orElseThrow().getCreated())).
                collect(Collectors.toList());
    }

     public Restaurant getOne(Integer restaurantId) {
         return restaurantRepository.findById(restaurantId).orElseThrow(
                 ()-> new IllegalArgumentException("No entity with restaurantId = " + restaurantId + "is found."));
    }


    @Modifying
    @Transactional
    @CacheEvict(value = "allRestaurantsDateInfo" , keyGenerator = "DateKeyGenerator")
    public Restaurant save(Restaurant restaurant) {
        ValidationUtil.checkNew(restaurant);
        Restaurant created = restaurantRepository.save(restaurant);
        Menu menu = new Menu(null);
        menu.setRestaurant(created);
        menuRepository.save(menu);
        return created;
    }


    @Modifying
    @Transactional
    @Caching(evict= {
            @CacheEvict(value = "restaurantDateInfo", key = "#updatedRestaurant.id"),
            @CacheEvict(value = "restaurantHistoryInfo", key = "#updatedRestaurant.id"),
            @CacheEvict(value = "allRestaurantsDateInfo", allEntries = true)})
     public Restaurant updateRestaurant(Restaurant updatedRestaurant) {
        restaurantRepository.findById(updatedRestaurant.getId()).orElseThrow(
                ()-> new IllegalArgumentException("No entity with restaurantId = " + updatedRestaurant.getId() + "is found."));
        return restaurantRepository.save(updatedRestaurant);
    }

    @Modifying
    @Transactional
    @Caching(evict= {
            @CacheEvict(value = "restaurantDateInfo", key = "#restaurantId"),
            @CacheEvict(value = "restaurantHistoryInfo", key = "#restaurantId"),
            @CacheEvict(value = "allRestaurantsDateInfo", allEntries = true)})
    public void delete(Integer restaurantId) {
             restaurantRepository.findById(restaurantId).orElseThrow(
                ()-> new IllegalArgumentException("No entity with restaurantId = " + restaurantId + "is found."));
             voteRepository.deleteByRestaurantId(restaurantId);
             restaurantRepository.deleteById(restaurantId);
    }


    public RestaurantMenuInfo getOneInfo(Integer restaurantId) {
        Restaurant restaurant = restaurantRepository.getOneInfo(restaurantId,LocalDate.now()).orElseThrow(
                ()-> new IllegalArgumentException("No entity with restaurantId=" + restaurantId + " is found " +
                        "or no menu for today"));
        return new RestaurantMenuInfo(restaurant.getId(),restaurant.getName(),LocalDate.now(),
                restaurant.getMenu().stream().findFirst().orElseThrow(
                        ()-> new RuntimeException("Error retrieving menu ...")).getMeals());
    }

    @Modifying
    @Transactional
    @Caching(evict= {
            @CacheEvict(value = "restaurantDateInfo", key = "#restaurantId"),
            @CacheEvict(value = "restaurantHistoryInfo", key = "#restaurantId"),
            @CacheEvict(value = "allRestaurantsDateInfo",keyGenerator = "DateKeyGenerator")})
    public RestaurantMenuInfo addMealToMenu(Integer restaurantId, List<Meal> meals) {
        ValidationUtil.checkIfEmpty(meals);
        meals.forEach(ValidationUtil::checkNew);
        Restaurant restaurant = restaurantRepository.getOneInfo(restaurantId,LocalDate.now()).orElse(
                restaurantRepository.findById(restaurantId).orElseThrow(
                        ()-> new IllegalArgumentException("No entity with restaurantId = " + restaurantId + "is found.")));
        Menu todayMenu = restaurant.getMenu().stream().filter(menu -> menu.getCreated().equals(LocalDate.now())).findFirst().orElse(
                new Menu(new ArrayList<>()));
        todayMenu.addMealsToMenu(meals);
        todayMenu.setRestaurant(restaurant);
        menuRepository.save(todayMenu);
        return new RestaurantMenuInfo(restaurant.getId(),restaurant.getName(),LocalDate.now(),todayMenu.getMeals());
    }

    @Modifying
    @Transactional
    @Caching(evict= {
            @CacheEvict(value = "restaurantDateInfo", key = "#restaurantId"),
            @CacheEvict(value = "restaurantHistoryInfo", key = "#restaurantId"),
            @CacheEvict(value = "allRestaurantsDateInfo",keyGenerator = "DateKeyGenerator")})
    public RestaurantMenuInfo updateMeals(Integer restaurantId, List<Meal> meals) {
        ValidationUtil.checkIfEmpty(meals);
        meals.forEach(ValidationUtil::checkNotNew);
        Restaurant restaurant = restaurantRepository.getOneInfo(restaurantId,LocalDate.now()).orElseThrow(
                ()-> new IllegalArgumentException("No entity with restaurantId=" + restaurantId + " is found " +
                        "or no menu for today"));
        Menu menu = restaurant.getMenu().stream().findFirst().orElseThrow(
                ()-> new RuntimeException("Error retrieving menu ..."));
        menu.getMeals().forEach(meal ->
            meals.forEach(updatedMeal-> {
                assert updatedMeal.getId() != null;
                if(Objects.equals(updatedMeal.getId(), meal.getId())) {
                    meal.setDescription(updatedMeal.getDescription());
                    meal.setPrice(updatedMeal.getPrice());
            }
                }
        ));
       menuRepository.save(menu);
       return new RestaurantMenuInfo(restaurant.getId(),restaurant.getName(),LocalDate.now(),menu.getMeals());
    }

    @Modifying
    @Transactional
    @Caching(evict= {
            @CacheEvict(value = "restaurantDateInfo", key = "#restaurantId"),
            @CacheEvict(value = "restaurantHistoryInfo", key = "#restaurantId"),
            @CacheEvict(value = "allRestaurantsDateInfo",keyGenerator = "DateKeyGenerator")})
    public void deleteMeals(Integer restaurantId, List<Integer> deleteListId) {
        ValidationUtil.checkIfEmpty(deleteListId);
        Restaurant restaurant = restaurantRepository.getOneInfo(restaurantId,LocalDate.now()).orElseThrow(
                ()-> new IllegalArgumentException("No entity with restaurantId=" + restaurantId + " is found " +
                        "or no menu for today"));
        Menu menu = restaurant.getMenu().stream().findFirst().orElseThrow(
                ()-> new RuntimeException("Error retrieving menu ..."));
        List<Integer> existedListId = menu.getMeals().stream().mapToInt(BaseEntity::getId).boxed().toList();
        if(!existedListId.containsAll(deleteListId))
            throw new IllegalArgumentException("Can't found meals to delete. Wrong id list");
        List<Meal> updatedMealList =  menu.getMeals().stream().
                filter(meal->!deleteListId.contains(meal.getId())).
                collect(Collectors.toList());
        menu.setMeals(updatedMealList);
        menuRepository.save(menu);
        mealRepository.deleteAllById(deleteListId);
        }

}

