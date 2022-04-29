package com.restautantvote.services;

import com.restautantvote.AbstractControllerTest;
import com.restautantvote.model.User;
import com.restautantvote.model.Vote;
import com.restautantvote.repository.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;



class VoteServiceTest  extends AbstractService {

    @Autowired
    VoteService service;

    @Test
    void voteFor_IdNotExist() {
        Assert.assertThrows(IllegalArgumentException.class,()-> service.voteFor(300,new Vote(5))) ;
    }

    @Test
    void voteFor_MenuIsNotUpdated() {
        Assert.assertThrows(IllegalArgumentException.class,()-> service.voteFor(2,new Vote(5))) ;
    }

    @Test
    void voteFor_UpdateRate() {
        Integer restaurantId = 1;
        User user = userRepository.findUserByLogin("user").orElseThrow();
        Vote newVote = new Vote(10);
        newVote.setUser(user);
        service.voteFor(restaurantId,newVote);
        Vote voteresult =
                service.getUserVotes(user.getId()).stream()
                        .filter(v->v.getDate().equals(LocalDate.now()))
                        .findFirst().orElseThrow();
        assertThat(voteresult).usingRecursiveComparison().isEqualTo(newVote);
        System.out.println(service.getOneInfo(restaurantId,LocalDate.now()).getAverageGrade());

    }






}