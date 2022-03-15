package com.restautantvote.repository;

import com.restautantvote.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal,Integer> {
}
