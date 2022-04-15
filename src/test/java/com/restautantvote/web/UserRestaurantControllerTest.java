package com.restautantvote.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "time-border.hour=23",
                                "time-border.minute=59" })
class UserRestaurantControllerTest extends AbstractControllerTest {
    static final String URL = "/api/restaurants";

    @Test
    void allRestaurantsMenuForToday() throws Exception {
        perform(MockMvcRequestBuilders.get(URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE));
    }


    @Test()
    void allRestaurantInfoForDate_WithWrongTypeParametr() throws Exception {
        String badDataParametr = "blablabla";
        String ErrorAxpectedMessage = "Failed to convert value of type 'java.lang.String' to required type 'java.time.LocalDate';" +
                " nested exception is org.springframework.core.convert.ConversionFailedException:" +
                " Failed to convert from type [java.lang.String] to type [@org.springframework.web.bind.annotation.PathVariable @org.springframework.format.annotation.DateTimeFormat java.time.LocalDate] for value 'blablabla';" +
                " nested exception is java.lang.IllegalArgumentException: " +
                "Parse attempt failed for value [blablabla]";
        perform(MockMvcRequestBuilders.get(URL+"/date/{badDataParametr}", badDataParametr))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(result -> assertEquals(ErrorAxpectedMessage, result.getResolvedException().getMessage()));
    }


    @Test
    void oneRestaurantInfoForToday_withValidationError() throws  Exception{
        String restaurantId = "-67";
        String ErrorAxpectedMessage = "oneRestaurantInfoForToday.restaurantId: must be greater than or equal to 1";
        perform(MockMvcRequestBuilders.get(URL+"/{restaurantId}", restaurantId))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertEquals(ErrorAxpectedMessage, result.getResolvedException().getMessage()));
    }

    @Test
    void makeVoteUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get(URL+"/2/vote/2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = "user")
    void makeVoteSuccess() throws Exception {
          perform(MockMvcRequestBuilders.get(URL+"/2/vote/7"))
                .andExpect(status().isOk());
    }




}