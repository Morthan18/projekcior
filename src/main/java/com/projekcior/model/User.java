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
    @ManyToOne(cascade = CascadeType.ALL)
    Authority authority;

    public User(String firstName, String lastName, String username, String password, Integer age, Authority authority) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.age = age;
        this.enabled = true;
        this.authority = authority;
    }
}
