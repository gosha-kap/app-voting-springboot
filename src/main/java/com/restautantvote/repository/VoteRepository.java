package com.restautantvote.repository;

import com.restautantvote.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote,Integer> {

    @Query("SELECT votes from Vote votes " +
            "where votes.date = :date ")
    List<Vote> getAllForDay(LocalDate date);

    @Query("SELECT votes from Vote votes " +
            "JOIN FETCH votes.restaurant  " +
            "where votes.date = current_date and votes.restaurant.id=:restaurantId")
    List<Vote> getVotesForOne(Integer restaurantId);

    @Query("SELECT votes from Vote votes " +
            "JOIN FETCH votes.restaurant  " +
            "where  votes.user.id =:userID")
    List<Vote> getVotesHistory(Integer userID);


    @Query("select  new com.restautantvote.model.Vote(v.id,v.rate)  from Vote v " +
              "WHERE v.user.id=:userId and v.date = CURRENT_DATE ")
    Optional<Vote> getByUserId(int userId);

    @Modifying
    @Query("delete  from Vote vote where vote.restaurant.id=:restaurantId")
    void deleteByRestaurantId(Integer restaurantId);

    @Query("SELECT votes from Vote votes " +
            "JOIN FETCH votes.restaurant  " +
            "where  votes.restaurant.id=:restaurantId")
    List<Vote> getAllForRestaurant(Integer restaurantId);
}
