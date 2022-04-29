package com.restautantvote;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.restautantvote.utils.JpaUtil;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "time-border.hour=23",
        "time-border.minute=59" })
@Transactional
@AutoConfigureMockMvc
public abstract class AbstractControllerTest {

    @Autowired
    private CacheManager cacheManager;



    @Before
    public void setUp()   {

        cacheManager.getCache("restaurantsTodayInfo").clear();
        cacheManager.getCache("restaurantHistoryInfo").clear();

    }

    protected ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.setAnnotationIntrospector(new IgnoreJacksonWriteOnlyAccess());
    }


}
