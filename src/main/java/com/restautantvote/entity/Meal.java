package com.restautantvote.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
public class Meal {
    @Id
    private Integer id;
    private String description;
    private BigDecimal price;
    @ManyToOne
    private Menu menu;
}
