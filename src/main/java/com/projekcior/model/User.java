package com.projekcior.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
@Entity(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String firstName;
    String lastName;
    String username;
    String password;
    Integer age;
    boolean enabled;
    @OneToMany(cascade = CascadeType.ALL)
    List<Authority> authorities;

    public User(String firstName, String lastName, String username, String password, Integer age, List<Authority> authorities) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.age = age;
        this.enabled = true;
        this.authorities = authorities;
    }
}
