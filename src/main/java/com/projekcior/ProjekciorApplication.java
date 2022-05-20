package com.projekcior;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static com.projekcior.LoginController.*;

@SpringBootApplication
@ControllerAdvice
public class ProjekciorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjekciorApplication.class, args);
	}

	@ExceptionHandler( BindException.class)
	@ResponseStatus(value = org.springframework.http.HttpStatus.BAD_REQUEST)
	protected ModelAndView handleBindException(BindException ex)
	{
		return new ModelAndView("register", Map.of(
				"user", new UserDto(),
				"errors", ex.getAllErrors()
		)
		);
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String exception(final Throwable throwable, final Model model) {
		String errorMessage = (throwable != null ? throwable.getMessage() : "Unknown error");
		model.addAttribute("errorMessage", errorMessage);
		return "error";
	}
}
