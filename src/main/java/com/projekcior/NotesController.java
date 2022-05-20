package com.projekcior;

import com.projekcior.model.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class NotesController {

    private final NoteRepository noteRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");

        return mav;
    }

    @RequestMapping("/successLogin")
    public ModelAndView successLogin() {
        ModelAndView mav = new ModelAndView("redirect:/");

        return mav;
    }

    @GetMapping("/notes")
    ModelAndView getNotes() {
        var mav = new ModelAndView("notes");
        var user = userRepository.findAuthenticatedUser().get();

        mav.addObject("notes", noteRepository.findAllByAuthor(user).stream()
                .map(this::mapToDto)
                .toList());
        return mav;
    }

    @GetMapping("/notes/{id}")
    ModelAndView getNote(@PathVariable long id) {
        var mav = new ModelAndView("editNote");

        var note = noteRepository.findById(id);
        if (note.isEmpty()) {
            return new ModelAndView("redirect:/notes");
        }

        if (note.get().getAuthor().getId() != userRepository.findAuthenticatedUser().get().getId()) {
            return new ModelAndView("redirect:/notes");
        }

        mav.addObject("noteDto", mapToDto(note.get()));
        mav.addObject("allCategories", categoryRepository.findAll());
        return mav;
    }

    @GetMapping("/notes/{id}/delete")
    ModelAndView deleteNote(@PathVariable long id) {
        var mav = new ModelAndView("redirect:/notes");

        var user = userRepository.findAuthenticatedUser();
        var userNotes = noteRepository.findAllByAuthor(user.get());
        if (userNotes.stream().noneMatch(n-> n.getAuthor().getId() == user.get().getId())){
            return mav;
        }
        noteRepository.deleteById(id);

        return mav;
    }

    @PostMapping("/notes/{id}")
    ModelAndView updateNote(@PathVariable long id, @ModelAttribute NoteDto noteDto) {
        var mav = new ModelAndView("editNote");

        var noteOpt = noteRepository.findById(id);
        if (noteOpt.isEmpty()) {
            mav.addObject("error", "Note not found");
            return mav;
        }
        var categoryOpt = categoryRepository.findById(noteDto.getCategoryId());
        if (categoryOpt.isEmpty()) {
            mav.addObject("error", "Category not found");
            return mav;
        }

        var category = categoryOpt.get();
        var note = noteOpt.get();

        if (note.getAuthor().getId() != userRepository.findAuthenticatedUser().get().getId()) {
            return new ModelAndView("redirect:/notes");
        }

        note.setTitle(noteDto.getTitle());
        note.setContent(noteDto.getContent());
        note.setCategory(category);

        noteRepository.save(note);
        mav = new ModelAndView("redirect:/notes");
        return mav;
    }

    @GetMapping("/notes/external-link/{id}")
    ModelAndView getNotes(@PathVariable long id) {
        var mav = new ModelAndView("externalNote");

        var note = noteRepository.findById(id);
        if (note.isEmpty()) {
            mav.addObject("error", "Note not found");
            return mav;
        }

        mav.addObject("noteDto", mapToDto(note.get()));
        return mav;
    }

    @GetMapping("/create-note")
    String addNote(Model model) {
        model.addAttribute("noteDto", new NoteDto());
        model.addAttribute("categories", categoryRepository.findAll());
        return "create-note";
    }

    @PostMapping("/notes")
    ModelAndView addNote(@ModelAttribute NoteDto noteDto) {
        var category = categoryRepository.findById(noteDto.getCategoryId());
        if (category.isEmpty()) {
            var mav = new ModelAndView("create-note");
            mav.addObject("error", "Category doesnt exist");
            return mav;
        }
        var user = userRepository.findAuthenticatedUser();


        noteRepository.save(new Note(
                noteDto.getTitle(),
                noteDto.getContent(),
                category.get(),
                user.get(),
                List.of()
        ));

        var mav = new ModelAndView("redirect:/notes");
        mav.addObject("notes", noteRepository.findAllByAuthor(userRepository.findAuthenticatedUser().get()));
        return mav;
    }

    @GetMapping("/notes/shared-for-me")
    ModelAndView getSharedForMe() {
        var modelAndView = new ModelAndView("shared-for-me");
        modelAndView.addObject("sharedNotes", noteRepository.findAllBySharedToContains(userRepository.findAuthenticatedUser().get()));
        return modelAndView;
    }

    @GetMapping("/notes/share-note/{id}")
    ModelAndView shareNote(@PathVariable long id) {
        var modelAndView = new ModelAndView("share-note");

        var note = noteRepository.findById(id);
        if (note.isEmpty()) {
            modelAndView.addObject("error", "Note not found");
            return modelAndView;
        }
        if (note.get().getAuthor().getId() != userRepository.findAuthenticatedUser().get().getId()) {
            return new ModelAndView("redirect:/notes");
        }

        NoteDto noteDto = mapToDto(note.get());

        modelAndView.addObject("noteDto", noteDto);
        modelAndView.addObject("users", userRepository.findAll()
                .stream()
                .filter(u -> !u.getUsername().equals(AuthUserHelper.getAuthenticatedUsername()))
                .collect(Collectors.toList())
        );

        return modelAndView;
    }

    private NoteDto mapToDto(Note note) {
        var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        var noteDto = new NoteDto();
        noteDto.setId(note.getId());
        noteDto.setTitle(note.getTitle());
        noteDto.setContent(note.getContent());
        noteDto.setCategory(note.getCategory());
        noteDto.setCategoryId(note.getCategory().getId());
        noteDto.setLink(baseUrl + "/notes/external-link/" + note.getId());
        noteDto.setSharedTo(note.getSharedTo().stream().map(User::getId).collect(Collectors.toSet()));
        noteDto.setCreationDate(LocalDate.now());
        return noteDto;
    }

    @PostMapping("/notes/share-note/{id}")
    ModelAndView shareNote(@PathVariable long id, @ModelAttribute NoteDto noteDto) {
        var modelAndView = new ModelAndView("share-note");

        var note = noteRepository.findById(id);
        if (note.isEmpty()) {
            modelAndView.addObject("error", "Note not found");
            return modelAndView;
        }
        if (note.get().getAuthor().getId() != userRepository.findAuthenticatedUser().get().getId()) {
            return new ModelAndView("redirect:/notes");
        }
        var users = userRepository.findAllById(noteDto.getSharedTo());
        note.get().setSharedTo(users);
        noteRepository.save(note.get());

        var mav = new ModelAndView("redirect:/notes");
        mav.addObject("notes", noteRepository.findAllByAuthor(userRepository.findAuthenticatedUser().get()));
        return mav;
    }


    @Data
    class NoteDto {
        long id;
        String title;
        String content;
        Category category;
        long categoryId;
        String link;
        Set<Long> sharedTo;
        LocalDate creationDate;
    }

}
