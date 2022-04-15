package com.restautantvote;

import com.restautantvote.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@AllArgsConstructor

public class AppApplication implements ApplicationRunner {

    final private RestaurantRepository restaurantRepository;

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
            }
}
