package com.budgettrip.controller;

import com.budgettrip.entity.User;
import com.budgettrip.repository.TripRepository;
import com.budgettrip.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    public AdminController(UserRepository userRepository, TripRepository tripRepository) {
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        List<User> users = userRepository.findAll();
        long totalTrips = tripRepository.count();

        model.addAttribute("users", users);
        model.addAttribute("userCount", users.size());
        model.addAttribute("tripCount", totalTrips);
        return "admin-dashboard";
    }

    @GetMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }
}