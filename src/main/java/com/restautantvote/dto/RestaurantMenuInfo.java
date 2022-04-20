package com.restautantvote.dto;


import com.restautantvote.model.Meal;
import lombok.Data;


import java.time.LocalDate;
import java.util.List;

@Data
public class RestaurantMenuInfo implements  Comparable<RestaurantMenuInfo>{

    private Integer id;
    private String name;
    private Integer voteCount;
    private Double averageGrade;
    private LocalDate date;
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
        int result = 0 ;
        result =  (int) (o.getAverageGrade() - this.averageGrade);
        if (result == 0 )
            result = o.getVoteCount() - this.getVoteCount() ;
        return result;
    }
}
