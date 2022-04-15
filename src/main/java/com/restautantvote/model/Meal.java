package com.restautantvote.model;

import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="meals")
@Getter
@Setter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Meal extends BaseEntity implements Serializable {

    @Column(name = "description", nullable = false)
    @NotBlank
    @Size(min = 2, max = 120)
    private String description;

    @Column(name = "price", nullable = false)
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer=5, fraction=2)
    private BigDecimal price;



    public Meal(@NotBlank @Size(min = 2, max = 120) String description,
                @DecimalMin(value = "0.0", inclusive = false) @Digits(integer = 5, fraction = 2) BigDecimal price
                ) {
      this(null,description,price);
    }

    public Meal(Integer id,
                @NotBlank @Size(min = 2, max = 120) String description,
                @DecimalMin(value = "0.0", inclusive = false) @Digits(integer = 5, fraction = 2) BigDecimal price) {
        this.id = id;
        this.description = description;
        this.price = price;
    }


}
