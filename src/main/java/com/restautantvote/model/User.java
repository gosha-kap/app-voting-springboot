package com.restautantvote.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.restautantvote.utils.JsonDeserializers;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.cache.annotation.Cacheable;


import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true,exclude = {"password","vote"})
@Access(AccessType.FIELD)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends BaseEntity implements Serializable {


    @Column(name = "login", nullable = false, unique = true)
    @NotEmpty
    @Size(max = 128)
    private String login;

    @Column(name = "first_name")
    @Size(max = 128)
    private String firstName;

    @Column(name = "last_name")
    @Size(max = 128)
    private String lastName;

    @Column(name = "password", nullable = false)
    @Size(min = 4, max = 128)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonDeserialize(using = JsonDeserializers.PasswordDeserializer.class)
    private String password;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role"},
                    name = "uk_user_roles")})
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Role> roles;


    @Column(name = "votes")
    @OneToMany
    @JsonIgnore
    private Set<Vote> vote;

    public User(Integer id, String login, String firstName, String lastName, String password, Set<Role> roles) {
        super(id);
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.roles = roles;
    }

    public User(Integer id) {
        super(id);
    }

    public User(String login, String firstName, String lastName, String password, Set<Role> roles) {
       this(null,login,firstName,lastName,password,roles);
    }

    public User(String login, String firstName, String lastName, String password) {
      this(null,login,firstName,lastName,password, Collections.singleton(Role.USER));
    }


}
