package com.restautantvote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Schema(description = "Информация об актуальности меню ресторана. Было ли меню сегодня обновлено. ")
public class RestaurantIsUpdatedInfo {

    private Integer id;
    private String name;
    private Boolean updated;
    private LocalDate lastMenu;
}
