package com.restautantvote.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.restautantvote.IgnoreJacksonWriteOnlyAccess;
import com.restautantvote.repository.UserRepository;
import com.restautantvote.services.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public abstract class AbstractControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserRepository userRepository;

    protected ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.setAnnotationIntrospector(new IgnoreJacksonWriteOnlyAccess());
    }


    protected ResultActions perform(MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder);
    }
}
