package com.restautantvote.web;

import com.restautantvote.dto.RestaurantMenuInfo;
import com.restautantvote.model.AuthUser;
import com.restautantvote.model.Vote;
import com.restautantvote.services.VoteService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/restaurants")
@Validated
@Slf4j
public class UserRestaurantController  {

    private final VoteService voteService;
    private final  Class<UserRestaurantController> controllerClass = UserRestaurantController.class;

    /*List of menus for today */
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<RestaurantMenuInfo>>> allRestaurantsMenuForToday(){
        log.info("Get All Restaurants");
        List<EntityModel<RestaurantMenuInfo>> menusList = voteService.getAllForDate(LocalDate.now()).stream()
                .map(menuInfo -> EntityModel.of(menuInfo,
                                linkTo(methodOn(controllerClass).oneRestaurantInfoForToday(menuInfo.getId())).withSelfRel(),
                                linkTo(methodOn(controllerClass).makeVote(menuInfo.getId(),null,null)).withRel("voteForMenu")))
                   .collect(Collectors.toList());
        return ResponseEntity.ok(
                CollectionModel.of(menusList,
                        linkTo(methodOn(UserRestaurantController.class).allRestaurantsMenuForToday()).withSelfRel()));
     }

    /*List of Restaurants  menu for certain date */
    @GetMapping("/date/{date}")
    public ResponseEntity<CollectionModel<EntityModel<RestaurantMenuInfo>>>allRestaurantInfoForDate(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        log.info("Get All Restaurants for date = {}",date);
        List<EntityModel<RestaurantMenuInfo>> menusList = voteService.getAllForDate(date).stream()
                .map(menuInfo -> EntityModel.of(menuInfo,
                        linkTo(methodOn(controllerClass).oneRestaurantMenuForDate(menuInfo.getId(),menuInfo.getDate())).withSelfRel()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                CollectionModel.of(menusList,
                        linkTo(methodOn(controllerClass).allRestaurantInfoForDate(date)).withSelfRel(),
                        linkTo(methodOn(controllerClass).allRestaurantsMenuForToday()).withRel("root")));
    }

    /*Restaurant  menu for today */
    @GetMapping("/{restaurantId}")
    public ResponseEntity<EntityModel<RestaurantMenuInfo>> oneRestaurantInfoForToday(@PathVariable @Min(1) Integer restaurantId){
        log.info("Restaurant  menu for today , id = {}",restaurantId);
        EntityModel<RestaurantMenuInfo> restaurantMenuInfoEntityModel = EntityModel.of(voteService.getOneInfo(restaurantId,LocalDate.now()),
                linkTo(methodOn(controllerClass).oneRestaurantInfoForToday(restaurantId)).withSelfRel(),
                linkTo(methodOn(UserRestaurantController.class).makeVote(restaurantId,null,null)).withRel("voteForMenu"),
                linkTo(methodOn(controllerClass).historyForRestaurant(restaurantId)).withRel("history"),
                linkTo(methodOn(controllerClass).allRestaurantsMenuForToday()).withRel("root"));
        return ResponseEntity.ok(restaurantMenuInfoEntityModel);
    }

    /*History menu for one  restaurant on date */
    @GetMapping("/{restaurantId}/history/{date}")
    public ResponseEntity<EntityModel<RestaurantMenuInfo>> oneRestaurantMenuForDate(
            @PathVariable @Min(1) Integer restaurantId,@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        log.info("Restaurant  menu for date = {} , id = {}",date,restaurantId);
        EntityModel<RestaurantMenuInfo> restaurantMenuInfoEntityModel = EntityModel.of(voteService.getOneInfo(restaurantId,date),
                linkTo(methodOn(controllerClass).oneRestaurantMenuForDate(restaurantId,date)).withSelfRel(),
                linkTo(methodOn(controllerClass).allRestaurantInfoForDate(date)).withRel("allRestaurantsForDate"),
                linkTo(methodOn(controllerClass).allRestaurantsMenuForToday()).withRel("root"));

        return ResponseEntity.ok(restaurantMenuInfoEntityModel);
    }

    /*History menu of certain restaurant*/
    @GetMapping("/{restaurantId}/history")
    public ResponseEntity<CollectionModel<EntityModel<RestaurantMenuInfo>>>  historyForRestaurant(@PathVariable @Min(1) Integer restaurantId){
        log.info("History menu of certain restaurant , id = {}",restaurantId);
        List<RestaurantMenuInfo> historyInfo = voteService.getMenuHistory(restaurantId);
        List<EntityModel<RestaurantMenuInfo>> menusList = historyInfo.stream()
                .map(menuInfo -> EntityModel.of(menuInfo,
                        linkTo(methodOn(controllerClass).oneRestaurantMenuForDate(menuInfo.getId(),menuInfo.getDate())).withSelfRel(),
                        linkTo(methodOn(controllerClass).allRestaurantInfoForDate(menuInfo.getDate())).withRel("allRestaurantsForDate")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(menusList));
    }

    /*Make vote for menu*/
    @GetMapping(value="/{restaurantId}/vote/{rate}")
    public ResponseEntity<EntityModel<RestaurantMenuInfo>> makeVote(@PathVariable @Min(1) Integer restaurantId, @PathVariable(value = "rate") @Min(1) @Max(10) Integer rate ,
                                   @AuthenticationPrincipal AuthUser authUser){
        log.info("Vote for  restaurant with id = {}, by user = {}",restaurantId,authUser);
        Vote vote = new Vote(rate);
        vote.setUser(authUser.getUser());
        RestaurantMenuInfo restaurantMenuInfo = voteService.voteFor(restaurantId,vote);
        EntityModel<RestaurantMenuInfo> restaurantMenuInfoEntityModel = EntityModel.of(restaurantMenuInfo,
                linkTo(methodOn(controllerClass).oneRestaurantInfoForToday(restaurantId)).withSelfRel(),
                linkTo(methodOn(UserController.class).getUserVotes(authUser)).withRel("userVoteHistory"),
                linkTo(methodOn(controllerClass).allRestaurantsMenuForToday()).withRel("root"));
         return ResponseEntity.ok(restaurantMenuInfoEntityModel);
     }



}
