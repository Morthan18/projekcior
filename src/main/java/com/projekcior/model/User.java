package com.projekcior.model;

import lombok.Value;

import java.util.UUID;

@Value
public class User {
    UUID id;
    String firstName;
    String lastName;
    String login;
    String password;
    int age;
}
