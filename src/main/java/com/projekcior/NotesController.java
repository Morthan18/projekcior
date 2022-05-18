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

    @RequestMapping(value = "notes", method = RequestMethod.GET)
    public ModelAndView messages() {
        ModelAndView mav = new ModelAndView("notes");
        var allNotes = List.of(
                new Note(UUID.randomUUID(),"Ważne ogłoszenie", "Legia to kurczak.", null, LocalDate.now(), new Category(UUID.randomUUID(),"Sport")),
                new Note(UUID.randomUUID(),"Ciekawostka",
                        "Kury mają doskonałą pamięć. Potrafią zapamiętać i rozróżnić ponad 100 twarzy innych kur oraz rozpoznać je nawet po kilku miesiącach rozłąki..",
                        "https://www.otwarteklatki.pl/blog/20-ciekawostek-o-kurach", LocalDate.now(), new Category(UUID.randomUUID(), "Rolnictwo"))
        );
        mav.addObject("notes", allNotes);
        return mav;
    }
}
