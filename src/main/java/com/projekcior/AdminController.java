package com.projekcior;

import com.projekcior.model.Authority;
import com.projekcior.model.User;
import com.projekcior.model.UserRepository;
import lombok.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/admin")
    public ModelAndView admin() {
        ModelAndView mav = new ModelAndView("admin");
        var users = getAllUsersToDisplay();
        mav.addObject("users", users);
        return mav;
    }

    private List<UserToEdit> getAllUsersToDisplay() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getFirstName() != null)
                .map(this::mapToDto)
                .toList();
    }

    @GetMapping("/admin/users/{id}")
    ModelAndView getUser(@PathVariable long id) {
        var modelAndView = new ModelAndView("user");

        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            modelAndView.addObject("error", "User not found");
            return modelAndView;
        }
        modelAndView.addObject("user", user.map(this::mapToDto2).get());
        modelAndView.addObject("allRoles", List.of("ROLE_FULL_USER", "ROLE_ADMIN", "ROLE_LIMITED_USER"));
        return modelAndView;
    }

    @PostMapping("/admin/users/{id}/update")
    ModelAndView updateUser(@PathVariable long id, @RequestParam String role) {
        var modelAndView = new ModelAndView("admin");

        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            modelAndView.addObject("error", "User not found");
            return modelAndView;
        }


        modelAndView.addObject("users", getAllUsersToDisplay());
        return modelAndView;
    }

    @Value
    public static class UserDto {
        private Long id;
        private String firstName;
        private String lastName;
        private Integer age;
        private String role;
    }

    private UserToEdit mapToDto(User u) {
        return new UserToEdit(u.getId(),
                u.getFirstName(),
                u.getLastName(),
                u.getAge(),
                u.getAuthority().getAuthority()
        );
    }

    private UserDto mapToDto2(User u) {
        return new UserDto(u.getId(),
                u.getFirstName(),
                u.getLastName(),
                u.getAge(),
                u.getAuthority().getAuthority()
        );
    }

    @Value
    static class UserToEdit {
        private long id;

        private String firstName;

        private String lastName;

        private Integer age;

        private String role;
    }

    @Data
    @NoArgsConstructor
    public static class RoleDto {
        String name;
    }
}
