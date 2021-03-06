package com.restautantvote.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;


@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Entity
@Table(name="restaurants")
public class Restaurant extends BaseEntity implements Serializable {

    @Column(name = "name", nullable = false,unique = true)
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 120)
    private String name;


    @Column(name = "menus")
    @OneToMany(mappedBy = "restaurant",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Menu> menu;


    public Restaurant(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Restaurant(String name) {
        this(null,name);
    }



}
