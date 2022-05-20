package com.projekcior;

import com.projekcior.model.Authority;
import com.projekcior.model.User;
import com.projekcior.model.UserRepository;
import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@ControllerAdvice
@Validated
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @RequestMapping("/login.html")
    public String loginHtml() {
        return "login";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }


    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        return "register";
    }

    @PostMapping("/register")
    public ModelAndView register(@ModelAttribute("user") @Valid UserDto userDto, ModelAndView mav, HttpServletRequest req) {
        boolean userExist = userRepository.findByUsername(userDto.getUsername()).isPresent();
        if (userExist) {
            mav.addObject("message", "An account for that username already exists.");
            return mav;
        }
        if (!userDto.getPassword().equals(userDto.getMatchingPassword())) {
            mav.addObject("message", "Passwords doesnt match!");
            return mav;
        }
        userRepository.save(new User(
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getUsername(),
                passwordEncoder.encode(userDto.getPassword()),
                userDto.getAge(),
                new Authority(userDto.getUsername(), "ROLE_FULL_USER")
        ));

        var sc = SecurityContextHolder.getContext();
        var auth = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(userDto.getUsername(), userDto.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_FULL_USER"))),
                new CredentialsDto(userDto.getUsername(), userDto.getPassword()),
                List.of(new SimpleGrantedAuthority("ROLE_FULL_USER"))
        );

        sc.setAuthentication(auth);
        HttpSession session = req.getSession(true);
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

        return new ModelAndView("index", "user", userDto);
    }

    @Value
    public static class CredentialsDto {
        String username;
        String password;
    }

    @Data
    @NoArgsConstructor
    public static class UserDto {
        @NotNull
        @NotBlank
        private String firstName;

        @NotNull
        @NotBlank
        private String lastName;

        @NotNull
        @NotBlank
        private String password;
        @NotNull
        @NotBlank
        private String matchingPassword;

        @NotNull
        @NotBlank
        private String username;

        @NotNull
        @Min(10)
        private Integer age;

    }
}
