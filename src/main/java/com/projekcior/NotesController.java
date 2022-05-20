package com.projekcior;

import com.projekcior.model.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

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

    @GetMapping("/notes")
    ModelAndView getNotes() {
        var mav = new ModelAndView("notes");
        var user = userRepository.findAuthenticatedUser().get();

        mav.addObject("notes", noteRepository.findAllByAuthor(user));
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

        var mav = new ModelAndView("notes");
        mav.addObject("notes", noteRepository.findAll());
        return mav;
    }

    @Data
    class NoteDto {
        String title;
        String content;
        long categoryId;
    }

}
