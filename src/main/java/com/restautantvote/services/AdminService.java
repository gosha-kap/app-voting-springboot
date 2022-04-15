package com.restautantvote.services;

import com.restautantvote.dto.RestaurantIsUpdatedInfo;
import com.restautantvote.dto.RestaurantMenuInfo;
import com.restautantvote.model.Meal;
import com.restautantvote.model.Menu;
import com.restautantvote.model.Restaurant;
import com.restautantvote.repository.MealRepository;
import com.restautantvote.repository.MenuRepository;
import com.restautantvote.repository.RestaurantRepository;
import com.restautantvote.repository.VoteRepository;
import com.restautantvote.utils.ValidationUtil;
import lombok.AllArgsConstructor;
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
        List<Restaurant> restaurantList = restaurantRepository.getAll();
        return restaurantList.stream().map(restaurant -> new RestaurantIsUpdatedInfo(
                        restaurant.getId(),
                        restaurant.getName(),
                        restaurant.getMenu().stream().anyMatch(menu -> menu.getCreated().equals(LocalDate.now()) &&
                                ( Objects.nonNull(menu.getMeals()) && menu.getMeals().size() != 0 ) ),
                        restaurant.getMenu().stream().findFirst().orElseThrow().getCreated())).
                collect(Collectors.toList());
    }

     public Restaurant getOne(Integer restaurantId) {
         Restaurant restaurant = restaurantRepository.getLastMenu(restaurantId).orElse(null);
         ValidationUtil.checkNotFound(restaurant,"restraurantId="+restaurantId);
            return restaurant;
    }


    @Modifying
    @Transactional
    public Restaurant save(Restaurant restaurant) {
        Restaurant created = restaurantRepository.save(restaurant);
        Menu menu = new Menu(null);
        menu.setRestaurant(created);
        menuRepository.save(menu);
        return created;
    }

    @Modifying
    @Transactional
    public void updateRestaurant(Restaurant updatedRestaurant) {
        Restaurant restaurant = restaurantRepository.getLastMenu(updatedRestaurant.getId()).orElse(null);
        ValidationUtil.checkNotFound(restaurant,"restraurantId="+updatedRestaurant.getId());
            restaurantRepository.save(restaurant);

    }

    @Modifying
    @Transactional
    public void delete(Integer restaurantId) {
        Restaurant restaurant = restaurantRepository.getLastMenu(restaurantId).orElse(null);
        ValidationUtil.checkNotFound(restaurant,"restraurantId="+restaurantId);
             voteRepository.deleteByRestaurantId(restaurantId);
             restaurantRepository.deleteById(restaurantId);

    }

    public RestaurantMenuInfo getOneInfo(Integer restaurantId) {
        Restaurant restaurant = restaurantRepository.getLastMenu(restaurantId).orElse(null);
        ValidationUtil.checkNotFound(restaurant,"restraurantId="+restaurantId);


            List<Meal> meals = restaurant.getMenu().stream().findFirst().orElse(new Menu(-1,new ArrayList<>()))
                    .getMeals();
            return new RestaurantMenuInfo(restaurant.getId(),restaurant.getName(),meals);

    }

    @Modifying
    @Transactional
    public RestaurantMenuInfo addMealToMenu(Integer restaurantId, List<Meal> meals) {
        ValidationUtil.checkIfEmpty(meals);
        Restaurant restaurant = restaurantRepository.getLastMenu(restaurantId).orElse(null);
        ValidationUtil.checkNotFound(restaurant,"restraurantId="+restaurantId);
        Menu menu = restaurant.getMenu().stream().filter(m->m.getCreated().isEqual(LocalDate.now())).findAny().orElse(new Menu(null));
        menu.setRestaurant(restaurant);
        menu.addMealsToMenu(meals);
        menu =  menuRepository.save(menu);
        return new RestaurantMenuInfo(restaurant.getId(),restaurant.getName(),menu.getMeals());

    }

    @Modifying
    @Transactional
    public void updateMeals(Integer restaurantId, List<Meal> meals) {
        ValidationUtil.checkIfEmpty(meals);
        meals.forEach(ValidationUtil::checkNotNew);
        Restaurant restaurant = restaurantRepository.getLastMenu(restaurantId).orElse(null);
        ValidationUtil.checkNotFound(restaurant,"restraurantId="+restaurantId);
        /*Check if menu is exist*/
        Menu menu = restaurant.getMenu().stream().filter(m->m.getCreated().isEqual(LocalDate.now())).findFirst().orElse(null);
        ValidationUtil.checkNotFound(menu,"Menu is not updated. Nothing to update");
        /*Update meals in menu*/
        menu.getMeals().forEach(meal -> meals.forEach(updatedMeal-> {
            if(updatedMeal.getId().equals(meal.getId())) {
                meal.setDescription(updatedMeal.getDescription());
                meal.setPrice(updatedMeal.getPrice());
            }
                }
        ));
        menuRepository.save(menu);
    }

    @Modifying
    @Transactional
    public void deleteMeals(Integer restaurantId, List<Integer> deleteListId) {
        ValidationUtil.checkIfEmpty(deleteListId);
        Restaurant restaurant = restaurantRepository.getLastMenu(restaurantId).orElse(null);
        ValidationUtil.checkNotFound(restaurant,"restraurantId="+restaurantId);
        Menu menu = restaurant.getMenu().stream().filter(m->m.getCreated().isEqual(LocalDate.now())).findFirst().orElse(null);
        ValidationUtil.checkNotFound(menu,"Menu is not updated. Nothing to delete");
        /*Check if  delete list is exist in menu*/
        List<Integer> existedListId = menu.getMeals().stream().mapToInt(meal -> meal.getId()).boxed().toList();
        if(!existedListId.containsAll(deleteListId))
            throw new IllegalArgumentException("Can't found meal to delete");
        /*Create new menu*/
        List<Meal> updatedMealList =  menu.getMeals().stream().
                filter(meal->!deleteListId.contains(meal.getId())).
                collect(Collectors.toList());
        menu.setMeals(updatedMealList);
        menuRepository.save(menu);
        mealRepository.deleteAllById(deleteListId);
        }

}

