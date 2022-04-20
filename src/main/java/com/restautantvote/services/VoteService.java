package com.restautantvote.services;



import com.restautantvote.dto.RestaurantMenuInfo;
import com.restautantvote.model.*;
import com.restautantvote.repository.MenuRepository;
import com.restautantvote.repository.RestaurantRepository;
import com.restautantvote.repository.UserRepository;
import com.restautantvote.repository.VoteRepository;

import com.restautantvote.utils.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class VoteService {


    private final RestaurantRepository restaurantRepository;
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final MenuRepository menuRepository;

    public VoteService(RestaurantRepository restaurantRepository, VoteRepository voteRepository, UserRepository userRepository, MenuRepository menuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;

    }

    @Value("${time-border.hour}")
    private  Integer HOUR;

    @Value("${time-border.minute}")
    private  Integer MINUTE;

    @Cacheable(value = "restaurantsTodayInfo")
    public List<RestaurantMenuInfo> getAllForDate(LocalDate date)    {
         List<Restaurant>   updatedRestaurants = restaurantRepository.getAllForDate(date);
         List<Vote> votes = voteRepository.getAllForDay(date);
         List<RestaurantMenuInfo> result =  updatedRestaurants.stream().
                 map(restaurant -> Util.toMenuFromRestaurantList(restaurant,votes,date)).sorted().collect(Collectors.toList());
        return result;

    }

    @Cacheable(value = "restaurantTodayInfo")
    public RestaurantMenuInfo getOneInfo(Integer restaurantId,LocalDate date) {
        List<Vote> votes = voteRepository.getVotesForOne(restaurantId);
        Restaurant restaurant = restaurantRepository.getOneInfo(restaurantId,date).
                orElseThrow(()-> new IllegalArgumentException("No entity with restaurantId = " + restaurantId + " and date = "+date.toString()+" for is found."));
        return Util.toMenuFromRestaurantList(restaurant,votes,date);
    }


    @Cacheable(value = "restaurantHistoryInfo")
    public  List<RestaurantMenuInfo>getMenuHistory(Integer restaurantId) {
        Restaurant   restaurant = restaurantRepository.getMenuHistory(restaurantId).
                orElseThrow(()-> new IllegalArgumentException("No entity with id = " + restaurantId + " is found."));
        List<Vote> votes = voteRepository.getAllForRestaurant(restaurantId);
        return Util.toMenuHistoryListFromRestaurant(restaurant,votes);
      }


    @Modifying
    @Transactional
    public RestaurantMenuInfo voteFor(Integer restaurantId, Vote vote) {
        LocalTime BORDER_TIME = LocalTime.of(HOUR,MINUTE);
        if(LocalTime.now().isBefore(BORDER_TIME)){
           Restaurant restaurant = restaurantRepository.getOneInfo(restaurantId,LocalDate.now()).orElseThrow(
                   ()-> new IllegalArgumentException("No entity with id = " + restaurantId + " is found."));
           vote.setRestaurant(restaurant);
           vote.setDate(LocalDate.now());
           Optional<Vote> oldVote = voteRepository.getByUserId(vote.getUser().id());
           oldVote.ifPresent(value -> vote.setId(value.getId()));
           voteRepository.save(vote);
           return this.getOneInfo(restaurantId,LocalDate.now());
        }
        else
            throw new IllegalStateException("You can't voted after "+BORDER_TIME.toString());
    }


    @Modifying
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    public List<Vote> getUserVotes(Integer userId ) {
        return voteRepository.getVotesHistory(userId);
    }
}
