package com.restautantvote.utils;

import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.time.LocalDate;

public class DateKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return LocalDate.now();
    }
}
