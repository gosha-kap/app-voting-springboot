package com.restautantvote.web;


import com.restautantvote.dto.RestaurantIsUpdatedInfo;
import com.restautantvote.model.Restaurant;
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
public class AdminRestaurantController {

    private final AdminService adminService;

    @GetMapping( produces = MediaType.APPLICATION_JSON_VALUE)
    public List<RestaurantIsUpdatedInfo> getAllToday()  {
           return adminService.getAllUpdated();
          }

    @GetMapping(value = "/{restaurantId}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Restaurant getOneInfo(@PathVariable @Min(1) Integer restaurantId) {
        return adminService.getOne(restaurantId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Restaurant createOne(@Valid @RequestBody Restaurant restaurant)  {
        return adminService.save(restaurant);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void  updateOne(@Valid @RequestBody Restaurant restaurant){
       adminService.updateRestaurant(restaurant);
    }

    @DeleteMapping( "/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void  deleteOne(@PathVariable @Min(1) Integer restaurantId){
        adminService.delete(restaurantId);
    }
}
