package com.restautantvote.web;


import com.restautantvote.AbstractControllerTest;
import com.restautantvote.model.User;


import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;


import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AllArgsConstructor

class UserControllerTest extends AbstractWebController {


    @Test
    void register() throws Exception {
        User user =
                new User("tony", "Tony", "Stark", "tony");
        perform(MockMvcRequestBuilders.post("/api/profile/register")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
        User savedUser = userRepository.findUserByLogin(user.getLogin()).orElseThrow();
        assertThat(savedUser).usingRecursiveComparison().ignoringFields("id", "password").isEqualTo(user);
    }

    @Test
    @WithUserDetails
    void updateUser_wrongID() throws Exception {
        User user =
                new User(5);
        perform(MockMvcRequestBuilders.put("/api/profile")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is4xxClientError());

    }

    @Test
    @WithUserDetails
    void updateUser() throws Exception {
        User user = userRepository.findUserByLogin("user").orElseThrow();
        user.setFirstName("changedName");
        user.setLastName("changedLastName");
        perform(MockMvcRequestBuilders.put("/api/profile")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("$.firstName", is("changedName")))
                .andExpect(jsonPath("$.lastName", is("changedLastName")));
    }


    @Test
    void register_withDuplicateLogin() throws Exception {
        User user =
                new User("user", "firstName1", "lastName2", "user");
        perform(MockMvcRequestBuilders.post("/api/profile/register")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void register_withBrokenEntity() throws Exception {
        String brokenEntity = "Some broken Data";
        perform(MockMvcRequestBuilders.post("/api/profile/register")
                .contentType(MediaType.APPLICATION_JSON).content(brokenEntity))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getUserProfile_unAuthorize() throws Exception {
        perform(MockMvcRequestBuilders.get("/api/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails
    void getUserProfile() throws Exception {
        perform(MockMvcRequestBuilders.get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.login", is("user")))
                .andExpect(jsonPath("$.firstName", is("User_First")))
                .andExpect(jsonPath("$.lastName", is("User_Last")));
    }
}