package com.restautantvote.web;

import com.restautantvote.dto.RestaurantMenuInfo;
import com.restautantvote.model.Meal;
import com.restautantvote.services.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/admin/restaurants")
public class AdminMealsController {

    private final AdminService adminService;

    @GetMapping(value = "/{restaurantId}/meals", produces = MediaType.APPLICATION_JSON_VALUE)
    public RestaurantMenuInfo getMenu(@PathVariable @Min(1) Integer restaurantId){
        return adminService.getOneInfo(restaurantId);
    }
    @PostMapping(value = "/{restaurantId}/meals",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public RestaurantMenuInfo createOne(@Valid @RequestBody List<Meal> meals, @PathVariable @Min(1) Integer restaurantId)  {
        return adminService.addMealToMenu(restaurantId,meals);
    }
    @PutMapping(value="/{restaurantId}/meals",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void  updateMeals(@Valid @RequestBody List<Meal> meals,@PathVariable @Min(1) Integer restaurantId){
        adminService.updateMeals(restaurantId,meals);
    }
    @DeleteMapping(value = "/{restaurantId}/meals")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void  deleteMeals(@PathVariable @Min(1) Integer restaurantId, @Valid @RequestBody List<Integer> deleteList){
        adminService.deleteMeals(restaurantId,deleteList);
    }
}
