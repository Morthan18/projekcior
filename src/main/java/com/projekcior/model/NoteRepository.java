package com.projekcior.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByAuthor(User author);

    List<Note> findAllBySharedToContains(User user);

}
