package com.restautantvote.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Restaurant extends AbstractBaseEntity {


    private String name;

    private Integer rating;

    @OneToMany
    private List<Menu> menu;




}
