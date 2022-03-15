package com.restautantvote.model;

import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(callSuper = true,exclude = {"password"})
public class User extends AbstractPersistable<Integer> {


    @Column(name = "login", nullable = false, unique = true)
    @NotEmpty
    @Size(max = 128)
    private String login;

    @Column(name = "password", nullable = false)
    @NotBlank
    @Size(min = 4, max = 128)
    private String password;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role"}, name = "uk_user_roles")})
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;


    @Column(name = "votes")
    @OneToMany(mappedBy = "user")
    private Set<Vote> vote;



}
