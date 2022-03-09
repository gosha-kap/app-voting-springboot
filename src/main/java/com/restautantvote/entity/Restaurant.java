package com.restautantvote.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Restaurant {
    @Id
    private Integer id;

    private String name;

    private Integer rating;

    @ManyToOne
    private Menu menu;


}
