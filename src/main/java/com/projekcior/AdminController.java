package com.projekcior;

import com.projekcior.model.UserRepository;
import lombok.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/admin")
    public ModelAndView admin() {
        ModelAndView mav = new ModelAndView("admin");
        var users = userRepository.findAll()
                .stream()
                .filter(u -> u.getFirstName() != null)
                .map(u -> {
                    var us = new UserToEdit();
                    us.setId(u.getId());
                    us.setFirstName(u.getFirstName());
                    us.setLastName(u.getLastName());
                    us.setAge(u.getAge());
                    return us;
                })
                .toList();
        mav.addObject("users", users);
        return mav;
    }



    @Data
    @NoArgsConstructor
    static class UserToEdit {
        private long id;

        private String firstName;

        private String lastName;

        private Integer age;

    }
}
