package com.projekcior;

import com.projekcior.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.UserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;

@CrossOrigin("*")
@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements ApplicationListener {

    private final DataSource dataSource;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final NoteRepository noteRepository;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")
                .and()
                .authorizeRequests().antMatchers(HttpMethod.POST, "/categories**", "/notes/**").hasAnyRole("FULL_USER", "ADMIN")
                .and()
                .authorizeRequests().antMatchers(HttpMethod.GET, "/create-category").hasAnyRole("FULL_USER", "ADMIN")
                .and()
                .authorizeRequests().antMatchers(
                        "/",
                        "/bootstrap/**",
                        "/img/**",
                        "/login.html",
                        "/login",
                        "/register").permitAll()
                .and()
                .anonymous()
                .and()
                .authorizeRequests().anyRequest().hasAnyRole("LIMITED_USER", "FULL_USER", "ADMIN")
                .and()
                .formLogin()
                .loginPage("/login.html")
                .successForwardUrl("/successLogin")
                .failureHandler(new SimpleUrlAuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {


                        super.setDefaultFailureUrl("/login?error");
                        super.onAuthenticationFailure(request, response, exception);
                    }
                })
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.jdbcAuthentication().dataSource(dataSource);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            var user = userRepository.save(new User("Krzycho", "Snobko", "krzycho_jp_3000_PL", passwordEncoder().encode("123"), 24, new Authority("krzycho_jp_3000_PL", "ROLE_FULL_USER")));
            var admin = userRepository.save(new User("Krzycho", "ADMIN", "ADMINOS", passwordEncoder().encode("123"), 24, new Authority("ADMINOS", "ROLE_ADMIN")));
            var retard = userRepository.save(new User("Krzycho", "Ograniczony ;/", "organiczony_z_gury", passwordEncoder().encode("123"), 24, new Authority("organiczony_z_gury", "ROLE_LIMITED_USER")));

            var category = categoryRepository.save(new Category("Ciekawostki"));
            categoryRepository.save(new Category("Lista zakupuw"));

            noteRepository.save(new Note("Kura",
                    "Kury mają doskonałą pamięć. Potrafią zapamiętać i rozróżnić ponad 100 twarzy innych kur oraz rozpoznać je nawet po kilku miesiącach rozłąki..",
                    category, user, List.of(admin)));

            noteRepository.save(new Note("Ważne ogłoszenie",
                    "Legia to kurczak",
                    category, user, List.of(admin, retard)));
        }
    }
}
