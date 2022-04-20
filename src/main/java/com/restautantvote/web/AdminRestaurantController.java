package com.restautantvote.web;


import com.restautantvote.dto.RestaurantIsUpdatedInfo;
import com.restautantvote.model.Restaurant;
import com.restautantvote.services.AdminService;
import com.restautantvote.utils.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/admin/restaurants")
@Slf4j
public class AdminRestaurantController {

    private final AdminService adminService;
    private final  Class<UserRestaurantController> userRestaurantController = UserRestaurantController.class;
    private final Class<AdminRestaurantController> adminRestaurantControllerClass  = AdminRestaurantController.class;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<RestaurantIsUpdatedInfo>>>  getAllToday()  {
        log.info("Get information about ubdated status by admin");
        List<RestaurantIsUpdatedInfo> updatedInfo = adminService.getAllUpdated();
        List<EntityModel<RestaurantIsUpdatedInfo>> updatedInfoList = updatedInfo.stream()
                .map(item -> {
                    Link link = (item.getLastMenu().isEqual(LocalDate.now())) ?
                            linkTo(methodOn(userRestaurantController).oneRestaurantInfoForToday(item.getId())).withRel("todayMenuInfo") :
                            linkTo(methodOn(userRestaurantController).oneRestaurantMenuForDate(item.getId(), item.getLastMenu())).withRel("historyMenuInfo");
                   return  EntityModel.of(item,link);   })
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(updatedInfoList,
                       linkTo(methodOn(userRestaurantController).allRestaurantsMenuForToday()).withRel("root")));
                }

    @GetMapping(value = "/{restaurantId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public  ResponseEntity<EntityModel<Restaurant>> getOne(@PathVariable @Min(1) Integer restaurantId) {
        log.info("get info about restaurant with id  {} by admin",restaurantId );
        EntityModel<Restaurant> restaurantEntityModel = EntityModel.of(adminService.getOne(restaurantId),
                linkTo(methodOn(userRestaurantController).oneRestaurantInfoForToday(restaurantId)).withRel("todayMenuInfo"),
                linkTo(methodOn(userRestaurantController).allRestaurantsMenuForToday()).withRel("root"));
        return ResponseEntity.ok(restaurantEntityModel);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<Restaurant>> createOne(@Valid @RequestBody Restaurant restaurant)  {
        log.info("Create restaurant  by admin");
        Restaurant createdRestaurant = adminService.save(restaurant);
        EntityModel<Restaurant> restaurantEntityModel = EntityModel.of(createdRestaurant,
                linkTo(methodOn(adminRestaurantControllerClass).getOne(createdRestaurant.getId())).withRel("restaurantInfo"),
                linkTo(methodOn(userRestaurantController).allRestaurantsMenuForToday()).withRel("root"));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/admin/restaurants/"+createdRestaurant.getId())
                .build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(restaurantEntityModel);

    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<Restaurant>>  updateOne(@Valid @RequestBody Restaurant restaurant){
        ValidationUtil.checkNotNew(restaurant);
        log.info("update info about restaurant with id  {} by admin",restaurant.getId() );
        EntityModel<Restaurant> restaurantEntityModel = EntityModel.of(adminService.updateRestaurant(restaurant),
                linkTo(methodOn(userRestaurantController).oneRestaurantInfoForToday(restaurant.getId())).withRel("todayMenuInfo"),
                linkTo(methodOn(userRestaurantController).allRestaurantsMenuForToday()).withRel("root"));
        return ResponseEntity.ok(restaurantEntityModel);
     }

    @DeleteMapping( "/{restaurantId}")
    public ResponseEntity<?>  deleteOne(@PathVariable @Min(1) Integer restaurantId){
        log.info("delete restaurant with id  {} by admin",restaurantId);
        adminService.delete(restaurantId);
        return ResponseEntity.noContent().build();
    }
}
