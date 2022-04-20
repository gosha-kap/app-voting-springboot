package com.restautantvote.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name="menus")
@Getter
@Setter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class Menu extends AbstractPersistable<Integer> {



    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<Meal> meals;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="restaurant_id", nullable=false)
    private Restaurant restaurant;

    @Column(name = "date", nullable = false)
    @NotNull
    private LocalDate created;

    public Menu(Integer id, List<Meal> meals) {
        setId(id);
        this.meals = meals;
        this.created = LocalDate.now();
    }

    public Menu(List<Meal> meals){
        this.setId(null);
        this.setMeals(meals);
        this.setCreated(LocalDate.now());

    }

    public void addMealsToMenu(List<Meal> meals ){
        this.getMeals().addAll(meals);
    }



    @Override
    public String toString() {
        return "Menu{" +
                "meals=" + meals +
                ", created=" + created +
                '}';
    }
}
