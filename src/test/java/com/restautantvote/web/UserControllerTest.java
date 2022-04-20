package com.restautantvote.web;


import com.restautantvote.model.User;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.assertj.core.api.Assertions.assertThat;



import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AllArgsConstructor

class UserControllerTest extends AbstractControllerTest {


    @Test
    void register() throws Exception {
        User user =
                new User("tony","Tony","Stark","tony");
         perform(MockMvcRequestBuilders.post("/api/profile/register")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
        User savedUser =   userRepository.findUserByLogin(user.getLogin()).orElseThrow();
        assertThat(savedUser).usingRecursiveComparison().ignoringFields("id","password").isEqualTo(user);
    }


    @Test
    void register_withDuplicateLogin() throws Exception {
        User user =
                new User("user","firstName1","lastName2","user");
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

}