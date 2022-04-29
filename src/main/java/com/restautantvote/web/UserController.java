package com.restautantvote.web;


import com.restautantvote.dto.RestaurantMenuInfo;
import com.restautantvote.model.AuthUser;
import com.restautantvote.model.Role;
import com.restautantvote.model.User;
import com.restautantvote.model.Vote;
import com.restautantvote.services.VoteService;
import com.restautantvote.utils.ValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(value = "/api/profile")
@Tag(name = "Profile controller.", description = "Register user. Get user information and update it. Show user vote history (Authorize required).")
public class UserController {

    private final VoteService voteService;
    private final  Class<UserRestaurantController> UserRestaurantController = UserRestaurantController.class;
    private final  Class<UserController> UserController = UserController.class;

    @Operation(
            summary = "Register user.")
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<User>> register(@Valid @RequestBody User user){
        log.info("register {}", user);
        ValidationUtil.checkNew(user);
        user.setRoles(Set.of(Role.USER));
        EntityModel<User> userEntityModel = EntityModel.of(voteService.save(user),
                linkTo(methodOn(UserController).getUserProfile(null)).withRel("userProfile"),
                linkTo(methodOn(UserRestaurantController).allRestaurantsMenuForToday()).withRel("root"));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/profile")
                .build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(userEntityModel);
    }

    @Operation(
            summary = "Get user information.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<User>> getUserProfile(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthUser authUser){
        log.info("get {}", authUser);
        EntityModel<User> userEntityModel = EntityModel.of(authUser.getUser(),
                linkTo(methodOn(UserController).getUserProfile(null)).withSelfRel(),
                linkTo(methodOn(UserController).getUserVotes(null)).withRel("history"),
                linkTo(methodOn(UserRestaurantController).allRestaurantsMenuForToday()).withRel("root"));
        return ResponseEntity.ok(userEntityModel);
    }

    @Operation(
            summary = "Update user information.")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<User>> updateUser(
            @Parameter(hidden = true)  @AuthenticationPrincipal AuthUser authUser,
            @Valid @RequestBody User user){
        log.info("update {} ", user);
        User oldUser = authUser.getUser();
        ValidationUtil.assureIdConsistent(user, oldUser.id());
        user.setRoles(oldUser.getRoles());
        if (user.getPassword() == null) {
            user.setPassword(oldUser.getPassword());
        }
        EntityModel<User> userEntityModel = EntityModel.of(voteService.save(user),
                linkTo(methodOn(UserController).getUserProfile(null)).withRel("user"),
                linkTo(methodOn(UserRestaurantController).allRestaurantsMenuForToday()).withRel("root"));
           return ResponseEntity.ok(userEntityModel);
    }

    @Operation(
            summary = "User voting information.")
    @GetMapping(value ="/history",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<Vote>>> getUserVotes(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthUser authUser){
        log.info("get votes for  {}", authUser);
        List<Vote> votes = voteService.getUserVotes(authUser.id());
        List<EntityModel<Vote>> votesEntityList = votes.stream()
                .map(vote ->
                        EntityModel.of(vote,linkTo(methodOn(UserRestaurantController).
                                oneRestaurantMenuForDate(vote.getRestaurant().getId(),vote.getDate())).withSelfRel()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                CollectionModel.of(votesEntityList,
                        linkTo(methodOn(UserController).getUserProfile(null)).withSelfRel(),
                        linkTo(methodOn(UserRestaurantController).allRestaurantsMenuForToday()).withRel("root")));
    }


}
