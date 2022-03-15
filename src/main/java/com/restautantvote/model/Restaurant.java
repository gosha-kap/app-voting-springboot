package com.restautantvote.model;

import lombok.*;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Entity
@Table(name="restaurants")
public class Restaurant extends AbstractPersistable<Integer> {

    @Column(name = "name", nullable = false)
    @NotBlank
    @Size(min = 2, max = 120)
    private String name;


    @Column(name = "rating")
    @OneToMany(fetch = FetchType.LAZY)
    private Set<Vote> countVote;


    @Column(name = "menus")
    @OneToMany(fetch = FetchType.LAZY)
    private List<Menu> menu;

    public Restaurant(Integer id, String name) {
        setId(id);
        this.name = name;
    }
}
