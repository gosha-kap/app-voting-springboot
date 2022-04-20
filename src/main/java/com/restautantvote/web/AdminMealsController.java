package com.restautantvote.web;

import com.restautantvote.dto.RestaurantMenuInfo;
import com.restautantvote.model.Meal;
import com.restautantvote.services.AdminService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/admin/restaurants")
@Slf4j
public class AdminMealsController {

    private final AdminService adminService;
    private final  Class<UserRestaurantController> userRestaurantController = UserRestaurantController.class;
    private final Class<AdminMealsController> adminMealsControllerClass = AdminMealsController.class;

    @GetMapping(value = "/{restaurantId}/meals")
    public ResponseEntity<EntityModel<RestaurantMenuInfo>> getMenu(@PathVariable @Min(1) Integer restaurantId){
        log.info("Get menu info for restaurant id =  {} by admin", restaurantId );
        EntityModel<RestaurantMenuInfo> restaurantEntityModel = EntityModel.of(adminService.getOneInfo(restaurantId),
                linkTo(methodOn(adminMealsControllerClass).getMenu(restaurantId)).withSelfRel(),
                linkTo(methodOn(userRestaurantController).allRestaurantsMenuForToday()).withRel("root"));
        return ResponseEntity.ok(restaurantEntityModel);
      }

    @PostMapping(value = "/{restaurantId}/meals",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<EntityModel<RestaurantMenuInfo>> addMealsForTodayManu(
            @Valid @RequestBody List<Meal> meals, @PathVariable @Min(1) Integer restaurantId)  {
        log.info("Add meals for today menu to  restaurant id =  {} by admin", restaurantId );
        EntityModel<RestaurantMenuInfo> restaurantEntityModel = EntityModel.of(adminService.addMealToMenu(restaurantId,meals),
                linkTo(methodOn(adminMealsControllerClass).getMenu(restaurantId)).withSelfRel(),
                linkTo(methodOn(userRestaurantController).allRestaurantsMenuForToday()).withRel("root"));
        return ResponseEntity.accepted().body(restaurantEntityModel);
    }

    @PutMapping(value="/{restaurantId}/meals",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<RestaurantMenuInfo>>  updateMeals(
            @Valid @RequestBody List<Meal> meals,@PathVariable @Min(1) Integer restaurantId){
        log.info("Update meals for today menu to  restaurant id =  {} by admin", restaurantId );
        EntityModel<RestaurantMenuInfo> restaurantEntityModel = EntityModel.of(adminService.updateMeals(restaurantId,meals),
                linkTo(methodOn(adminMealsControllerClass).getMenu(restaurantId)).withSelfRel(),
                linkTo(methodOn(userRestaurantController).allRestaurantsMenuForToday()).withRel("root"));
        return ResponseEntity.accepted().body(restaurantEntityModel);
    }
    @DeleteMapping(value = "/{restaurantId}/meals")
    public ResponseEntity<?>  deleteMeals(
            @PathVariable @Min(1) Integer restaurantId, @Valid @RequestBody List<Integer> deleteList){
        log.info("Delete meals for today menu to restaurant id =  {} by admin", restaurantId );
        adminService.deleteMeals(restaurantId,deleteList);
        return ResponseEntity.noContent().build();
    }
}
