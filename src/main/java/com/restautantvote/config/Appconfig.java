package com.restautantvote.config;


import com.restautantvote.utils.DateKeyGenerator;
import com.restautantvote.utils.JpaUtil;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.sql.SQLException;

@Configuration
@EnableCaching
@Slf4j
public class Appconfig {


    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        log.info("Start H2 TCP server");
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

    @Bean
    public JpaUtil getJpaUtil(){
        return new JpaUtil();
    }

    @Bean("DateKeyGenerator")
    public KeyGenerator keyGenerator(){
        return  new DateKeyGenerator();
    }


}
