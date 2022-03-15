package com.restautantvote.model;


import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@ToString(callSuper = true)
@Entity
@Table(name = "vote")
public class Vote extends AbstractPersistable<Integer> {

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "restaurant_id", nullable = false)
 @NotNull
 private Restaurant restaurant;

@Column(name = "date", nullable = false)
@NotNull
private LocalDate date;

 @Column(name = "rate")
 @Min(value = 1, message = "Rate value should not be less than 1")
 @Max(value = 10, message = "Rate Value  should not be greater than 10")
private Integer rate ;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "user_id", nullable = false)
 @NotNull
private User user;





}
