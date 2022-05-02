package com.restautantvote.utils;


import com.restautantvote.model.BaseEntity;


import java.time.LocalTime;
import java.util.Collection;

public class ValidationUtil {
    private ValidationUtil(){}

    public static void checkIfEmpty(Collection collection){
       if(collection.isEmpty())
           throw new IllegalArgumentException("No elements for adding is found");
    }



    public static void checkNew(BaseEntity entity) {
        if (!entity.isNew()) {
            throw new IllegalArgumentException(entity.getClass().getSimpleName() + " must be new (id=null)");
        }
    }

    public static void checkNotNew(BaseEntity entity) {
        if (entity.isNew()) {
            throw new IllegalArgumentException(entity + " must be not new )");
        }
    }


    public static void assureIdConsistent(BaseEntity entity, int id) {
        if (entity.isNew()) {
            entity.setId(id);
        } else if (entity.id() != id) {
            throw new IllegalArgumentException(entity + " must has id=" + id);
        }
    }


    public static void checkVoteTime(Integer hour, Integer minute) {
        LocalTime BORDER_TIME = LocalTime.of(hour,minute);
        if(LocalTime.now().isAfter(BORDER_TIME)) {
            throw new IllegalStateException("You can't voted after "+ BORDER_TIME);
        }
    }
}