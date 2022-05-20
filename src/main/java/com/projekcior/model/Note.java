package com.projekcior.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity(name = "NOTES")
@Data
@NoArgsConstructor
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String title;
    String content;
    LocalDate creationDate;
    @ManyToOne
    Category category;
    @ManyToOne
    User author;
    @ManyToMany
    List<User> sharedTo;

    public Note(String title, String content, Category category, User author, List<User> sharedTo) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.author = author;
        this.sharedTo = sharedTo;
        this.creationDate = LocalDate.now();
    }
}
