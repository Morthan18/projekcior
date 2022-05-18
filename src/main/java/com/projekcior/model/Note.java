package com.projekcior.model;

import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

@Value
public class Note {
    UUID id;
    String title;
    String content;
    String link;
    LocalDate creationDate;
    Category category;
}
