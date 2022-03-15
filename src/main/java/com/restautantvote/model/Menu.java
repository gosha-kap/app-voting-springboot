package com.restautantvote.model;


import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name="menus")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class Menu extends AbstractPersistable<Integer> {


    @Column(name = "meals")
    @OneToMany(mappedBy = "menu")
    private Set<Meal> meals;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="restaurant_id", nullable=false)
    private Restaurant restaurant;

    @Column(name = "date", nullable = false)
    @NotNull
    private LocalDate created;

    public Menu(Integer id, Set<Meal> meals) {
        setId(id);
        this.meals = meals;
        this.created = LocalDate.now();
    }



}
