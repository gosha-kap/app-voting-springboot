package com.restautantvote.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String password;
    private Set<Role> roles;
    private Boolean isVoted;

}
