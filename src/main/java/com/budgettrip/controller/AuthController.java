package com.budgettrip.controller;

import com.budgettrip.dto.UserRegistrationDto;
import com.budgettrip.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        UserRegistrationDto user = new UserRegistrationDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String registerUserAccount(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
                                      BindingResult result,
                                      Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.saveUser(userDto);
        } catch (Exception e) {
            result.rejectValue("email", "error.user", "An account already exists for this email.");
            return "register";
        }

        return "redirect:/login?success";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

