package com.restautantvote.web;

import com.restautantvote.dto.RestaurantMenuInfo;
import com.restautantvote.model.AuthUser;
import com.restautantvote.model.Vote;
import com.restautantvote.services.VoteService;
import com.restautantvote.utils.ValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@RequestMapping(value = "/api/restaurants")
@Validated
@Slf4j
@Tag(name = "Public restaurant information.", description = "Show restaurants with their menu. Their history. For voting  authorize is required.")

public class UserRestaurantController  {


    private final VoteService voteService;

    private final  Class<UserRestaurantController> controllerClass = UserRestaurantController.class;

    public UserRestaurantController(VoteService voteService) {
        this.voteService = voteService;
    }

    @Value("${time-border.hour}")
    private  Integer HOUR;

    @Value("${time-border.minute}")
    private  Integer MINUTE;


    @Operation(
            summary = "All restaurants today info.",
            description = "Gets all restaurants menu for today.")
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


    @Operation(
            summary = "One restaurant today info.",
            description = "Get restaurant menu information by it identifier for today."
    )
    @GetMapping("/{restaurantId}")
    public ResponseEntity<EntityModel<RestaurantMenuInfo>> oneRestaurantInfoForToday(
            @Parameter(description = "Identifier of restaurant") @PathVariable @Min(1) Integer restaurantId){
        log.info("Restaurant  menu for today , id = {}",restaurantId);
        EntityModel<RestaurantMenuInfo> restaurantMenuInfoEntityModel = EntityModel.of(voteService.getOneInfo(restaurantId,LocalDate.now()),
                linkTo(methodOn(controllerClass).oneRestaurantInfoForToday(restaurantId)).withSelfRel(),
                linkTo(methodOn(UserRestaurantController.class).makeVote(restaurantId,null,null)).withRel("voteForMenu"),
                linkTo(methodOn(controllerClass).historyForRestaurant(restaurantId)).withRel("history"),
                linkTo(methodOn(controllerClass).allRestaurantsMenuForToday()).withRel("root"));
        return ResponseEntity.ok(restaurantMenuInfoEntityModel);
    }


    @Operation(
            summary = "All restaurants info for previous date .",
            description = "Gets all restaurants menu for certain date."
    )
    @GetMapping("/date/{date}")
    public ResponseEntity<CollectionModel<EntityModel<RestaurantMenuInfo>>>allRestaurantInfoForDate(
            @Parameter(description = "Date", example = "2022-04-10") @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
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




    @Operation(
            summary = "History one restaurant info for previous date.",
            description = "Get restaurant menu information by it identifier for certain date")
    @GetMapping("/{restaurantId}/history/{date}")
    public ResponseEntity<EntityModel<RestaurantMenuInfo>> oneRestaurantMenuForDate(
            @Parameter(description = "Identifier of restaurant") @PathVariable @Min(1) Integer restaurantId,
            @Parameter(description = "Date", example = "2022_04_10") @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        log.info("Restaurant  menu for date = {} , id = {}",date,restaurantId);
        EntityModel<RestaurantMenuInfo> restaurantMenuInfoEntityModel = EntityModel.of(voteService.getOneInfo(restaurantId,date),
                linkTo(methodOn(controllerClass).oneRestaurantMenuForDate(restaurantId,date)).withSelfRel(),
                linkTo(methodOn(controllerClass).allRestaurantInfoForDate(date)).withRel("allRestaurantsForDate"),
                linkTo(methodOn(controllerClass).allRestaurantsMenuForToday()).withRel("root"));

        return ResponseEntity.ok(restaurantMenuInfoEntityModel);
    }


    @Operation(
            summary = "All history one restaurant info.",
            description = "Get restaurant menu information by it identifier for all period."
    )
    @GetMapping("/{restaurantId}/history")
    public ResponseEntity<CollectionModel<EntityModel<RestaurantMenuInfo>>>  historyForRestaurant(
            @Parameter(description = "Identifier of restaurant") @PathVariable @Min(1) Integer restaurantId){
        log.info("History menu of certain restaurant , id = {}",restaurantId);
        List<RestaurantMenuInfo> historyInfo = voteService.getMenuHistory(restaurantId);
        List<EntityModel<RestaurantMenuInfo>> menusList = historyInfo.stream()
                .map(menuInfo -> EntityModel.of(menuInfo,
                        linkTo(methodOn(controllerClass).oneRestaurantMenuForDate(menuInfo.getId(),menuInfo.getDate())).withSelfRel(),
                        linkTo(methodOn(controllerClass).allRestaurantInfoForDate(menuInfo.getDate())).withRel("allRestaurantsForDate")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(menusList));
    }


    @Operation(
            summary = "Voting. Authorize requared.",
            description = "Allows you to vote for the restaurant menu."
    )
    @GetMapping(value="/{restaurantId}/vote/{rate}")
    public ResponseEntity<EntityModel<RestaurantMenuInfo>> makeVote(
            @Parameter(description = "Identifier of restaurant") @PathVariable @Min(1) Integer restaurantId,
            @Parameter(description = "Your rate. Integer value from 1 to 10 ") @PathVariable(value = "rate") @Min(1) @Max(10) Integer rate ,
            @Parameter(hidden = true) @AuthenticationPrincipal AuthUser authUser){
        log.info("Vote for  restaurant with id = {}, by user = {}",restaurantId,authUser);
        ValidationUtil.checkVoteTime(HOUR,MINUTE);
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
