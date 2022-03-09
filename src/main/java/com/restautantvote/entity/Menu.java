package com.restautantvote.entity;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Menu {

    @Id
    private Integer id;

    @OneToMany
    private List meals;

    @OneToOne
    private Restaurant restaurant;

    private LocalDateTime created;
}
