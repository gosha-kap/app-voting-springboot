package com.restautantvote.dto;


import com.restautantvote.model.Meal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "Restaurant menu information for date.")
public class RestaurantMenuInfo implements  Comparable<RestaurantMenuInfo>{

    private Integer id;
    @Schema(description = "Name of restaurant.")
    private String name;
    @Schema(description = "Count of votes.")
    private Integer voteCount;
    @Schema(description = "Average grade of votes.")
    private Double averageGrade;
    @Schema(description = "Date.")
    private LocalDate date;
    @Schema(description = "List of menu items")
    private List<Meal> mealList;

    public RestaurantMenuInfo(Integer id, String name, Integer voteCount, Double averageGrade,LocalDate date, List<Meal> mealList) {
        this.id = id;
        this.name = name;
        this.voteCount = voteCount;
        this.averageGrade = averageGrade;
        this.date = date;
        this.mealList = mealList;
    }

    public RestaurantMenuInfo(Integer id, String name,LocalDate date,  List<Meal> mealList) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.mealList = mealList;
    }


    @Override
    public int compareTo(RestaurantMenuInfo o) {
        int result;
        result =  (int) (o.getAverageGrade() - this.averageGrade);
        if (result == 0 )
            result = o.getVoteCount() - this.getVoteCount() ;
        return result;
    }
}
