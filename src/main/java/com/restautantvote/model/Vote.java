package com.restautantvote.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Entity
@Table(name = "vote",uniqueConstraints = { @UniqueConstraint(name = "userid_date_vote", columnNames = { "user_id", "date" }) })
public class Vote extends BaseEntity implements Serializable {

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "restaurant_id", nullable = false, referencedColumnName = "id")
  private Restaurant restaurant;

 @Column(name = "date", nullable = false)
 private LocalDate date;


 @Column(name = "rate")
 @Min(value = 1, message = "Rate value should not be less than 1")
 @Max(value = 10, message = "Rate Value  should not be greater than 10")
 private Integer rate ;


 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "user_id", nullable = false,referencedColumnName = "id")
 @JsonIgnore
 private User user;

 public Vote(Integer rate) {
  this.date = LocalDate.now();
  this.rate = rate;
 }

 public Vote(int id, int rate) {
  this.id = id;
  this.rate = rate;
  this.date = LocalDate.now();
 }




 @Override
 public String toString() {
  return "Vote{" +
          "id=" + id +
          ", date=" + date +
          ", rate=" + rate +
          ", user_id =" +(Objects.isNull(this.getUser()) ? "null" : this.getUser().getId() ) +
          ", restaurant_id = " +(Objects.isNull(this.getRestaurant()) ? "null" : this.getRestaurant().getId()) +
          "}";
 }
}
