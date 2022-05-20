package com.projekcior;

import com.projekcior.model.Category;
import com.projekcior.model.CategoryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryRepository categoryRepository;

    @GetMapping("/categories")
    ModelAndView getCategories() {
        var mav = new ModelAndView("categories");
        mav.addObject("categories", categoryRepository.findAll());
        return mav;
    }

    @GetMapping("/create-category")
    String addCategory(Model model) {
        model.addAttribute("categoryDto", new CategoryDto());
        return "create-category";
    }

    @PostMapping("/categories")
    ModelAndView addCategory(@ModelAttribute CategoryDto categoryDto) {
        if (categoryRepository.findByName(categoryDto.getName()).isPresent()){
            var mav = new ModelAndView("create-category");
            mav.addObject("error","Category already exist");
            return mav;
        }

        var mav = new ModelAndView("categories");
        categoryRepository.save(new Category(categoryDto.getName()));
        mav.addObject("categories", categoryRepository.findAll());
        return mav;
    }

    @Data
    class CategoryDto{
        String name;
    }
}
