package com.projekcior;

import com.projekcior.model.Authority;
import com.projekcior.model.User;
import com.projekcior.model.UserRepository;
import lombok.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/admin")
    public ModelAndView admin() {
        ModelAndView mav = new ModelAndView("admin");
        var users = getAllUsersToDisplay();
        mav.addObject("users", users);
        mav.addObject("userDto", new UserDto());
        return mav;
    }

    @GetMapping("/admin/users/{id}")
    ModelAndView getUser(@PathVariable long id) {
        var modelAndView = new ModelAndView("user");

        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            modelAndView.addObject("error", "User not found");
            return modelAndView;
        }


        modelAndView.addObject("userDto", user.map(this::mapToDto).get());
        modelAndView.addObject("allRoles", ROLES);
        return modelAndView;
    }

    @PostMapping("/admin/users/{id}/update")
    ModelAndView updateUser(@PathVariable long id, @ModelAttribute UserDto userDto) {
        var modelAndView = new ModelAndView("admin");

        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            modelAndView.addObject("error", "User not found");
            return modelAndView;
        }
        if (!ROLES.contains(userDto.getRole())) {
            modelAndView.addObject("error", "Role doenst exist");
            return modelAndView;
        }
        user.get().setAuthority(new Authority(user.get().getUsername(), userDto.getRole()));


        modelAndView.addObject("users", getAllUsersToDisplay());
        return modelAndView;
    }

    private List<UserDto> getAllUsersToDisplay() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private UserDto mapToDto(User u) {
        var user = new UserDto();
        user.setId(u.getId());
        user.setFirstName(u.getFirstName());
        user.setLastName(u.getLastName());
        user.setAge(u.getAge());
        user.setRole(u.getAuthority().getAuthority());
        return user;
    }

    @Data
    public static class UserDto {
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

    public static final List<String> ROLES = List.of("ROLE_ADMIN","ROLE_LIMITED_USER", "ROLE_FULL_USER");
}
