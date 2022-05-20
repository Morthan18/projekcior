package com.projekcior.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@NoArgsConstructor
@Data
@Entity(name = "AUTHORITIES")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long technicalId;
    String username;
    String authority;

    public Authority(String username, String authority) {
        this.username = username;
        this.authority = authority;
    }
}
