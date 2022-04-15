package com.restautantvote.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class RestaurantIsUpdatedInfo {

    private Integer id;
    private String name;
    private Boolean updated;
    private LocalDate lastMenu;
}
