package com.restautantvote.web;

import com.restautantvote.dto.RestaurantMenuInfo;
import com.restautantvote.model.Meal;
import com.restautantvote.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Menu controller.", description = "Create and edit menu information for today. (Required admin role).")
public class AdminMealsController {

    private final AdminService adminService;
    private final  Class<UserRestaurantController> userRestaurantController = UserRestaurantController.class;
    private final Class<AdminMealsController> adminMealsControllerClass = AdminMealsController.class;

    @Operation(
            summary = "Menu information.",
            description = "Show all created meals in menu."
    )
    @GetMapping(value = "/{restaurantId}/meals")
    public ResponseEntity<EntityModel<RestaurantMenuInfo>> getMenu(
            @Parameter(description = "Identifier of restaurant") @PathVariable @Min(1) Integer restaurantId){
        log.info("Get menu info for restaurant id =  {} by admin", restaurantId );
        EntityModel<RestaurantMenuInfo> restaurantEntityModel = EntityModel.of(adminService.getOneInfo(restaurantId),
                linkTo(methodOn(adminMealsControllerClass).getMenu(restaurantId)).withSelfRel(),
                linkTo(methodOn(userRestaurantController).allRestaurantsMenuForToday()).withRel("root"));
        return ResponseEntity.ok(restaurantEntityModel);
      }

    @Operation(
            summary = "Add meal to menu.",
            description = "Adding meal is automatically create menu if it not created yet. Meal item must have no id."
    )
    @PostMapping(value = "/{restaurantId}/meals",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<EntityModel<RestaurantMenuInfo>> addMealsForTodayManu(
            @Valid @RequestBody List<Meal> meals,
            @Parameter(description = "Identifier of restaurant") @PathVariable @Min(1) Integer restaurantId)  {
        log.info("Add meals for today menu to  restaurant id =  {} by admin", restaurantId );
        EntityModel<RestaurantMenuInfo> restaurantEntityModel = EntityModel.of(adminService.addMealToMenu(restaurantId,meals),
                linkTo(methodOn(adminMealsControllerClass).getMenu(restaurantId)).withSelfRel(),
                linkTo(methodOn(userRestaurantController).allRestaurantsMenuForToday()).withRel("root"));
        return ResponseEntity.accepted().body(restaurantEntityModel);
    }


    @Operation(
            summary = "Update meals in menu.",
            description = "You have to produce list of existing meal items with updated information in them."
    )
    @PutMapping(value="/{restaurantId}/meals",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<RestaurantMenuInfo>>  updateMeals(
            @Valid @RequestBody List<Meal> meals,
            @Parameter(description = "Identifier of restaurant") @PathVariable @Min(1) Integer restaurantId){
        log.info("Update meals for today menu to  restaurant id =  {} by admin", restaurantId );
        EntityModel<RestaurantMenuInfo> restaurantEntityModel = EntityModel.of(adminService.updateMeals(restaurantId,meals),
                linkTo(methodOn(adminMealsControllerClass).getMenu(restaurantId)).withSelfRel(),
                linkTo(methodOn(userRestaurantController).allRestaurantsMenuForToday()).withRel("root"));
        return ResponseEntity.accepted().body(restaurantEntityModel);
    }
    @Operation(
            summary = "Delete meals in menu.",
            description = " You have to  produce list of identifiers of items to exclude them from menu."
    )
    @DeleteMapping(value = "/{restaurantId}/meals")
    public ResponseEntity<?>  deleteMeals(
            @Parameter(description = "Identifier of restaurant")  @PathVariable @Min(1) Integer restaurantId,
            @Valid @RequestBody List<Integer> deleteList){
        log.info("Delete meals for today menu to restaurant id =  {} by admin", restaurantId );
        adminService.deleteMeals(restaurantId,deleteList);
        return ResponseEntity.noContent().build();
    }
}
