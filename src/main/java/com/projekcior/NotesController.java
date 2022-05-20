package com.projekcior;

import com.projekcior.model.Category;
import com.projekcior.model.Note;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
public class NotesController {

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("index");
        var allNotes = getAllNotes();
        mav.addObject("notes", allNotes);
        return mav;
    }

    @RequestMapping(value = "notes", method = RequestMethod.GET)
    public ModelAndView messages() {
        ModelAndView mav = new ModelAndView("notes");
        var allNotes = getAllNotes();
        mav.addObject("notes", allNotes);
        return mav;
    }

    private List<Note> getAllNotes() {
        return List.of(
                new Note(UUID.randomUUID(), "Ważne ogłoszenie", "Legia to kurczak.", LocalDate.now(), new Category(UUID.randomUUID(), "Sport")),
                new Note(UUID.randomUUID(), "Ciekawostka",
                        "Kury mają doskonałą pamięć. Potrafią zapamiętać i rozróżnić ponad 100 twarzy innych kur oraz rozpoznać je nawet po kilku miesiącach rozłąki..",
                        LocalDate.now(), new Category(UUID.randomUUID(), "Rolnictwo"))
        );
    }
}
