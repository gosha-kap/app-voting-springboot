package com.restautantvote.repository;

import com.restautantvote.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal,Integer> {
}
