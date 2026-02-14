package com.budgettrip.controller;

import com.budgettrip.entity.Expense;
import com.budgettrip.entity.Trip;
import com.budgettrip.entity.User;
import com.budgettrip.repository.ExpenseRepository;
import com.budgettrip.repository.TripRepository;
import com.budgettrip.repository.UserRepository;
import com.budgettrip.service.DistanceService;
import com.budgettrip.service.PdfService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
public class TripController {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final PdfService pdfService;
    private final ExpenseRepository expenseRepository;
    private final DistanceService distanceService;

    public TripController(TripRepository tripRepository, UserRepository userRepository, PdfService pdfService, ExpenseRepository expenseRepository, DistanceService distanceService) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.pdfService = pdfService;
        this.expenseRepository = expenseRepository;
        this.distanceService = distanceService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(defaultValue = "0") int page, Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        Page<Trip> tripPage = tripRepository.findByUser(user, PageRequest.of(page, 6, Sort.by("id").descending()));

        model.addAttribute("username", username);
        model.addAttribute("trips", tripPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tripPage.getTotalPages());
        return "dashboard";
    }

    @GetMapping("/create-trip")
    public String showCreateTripForm(Model model) {
        model.addAttribute("trip", new Trip());
        return "create-trip";
    }

    @Transactional
    @PostMapping("/create-trip")
    public String createTrip(@Valid @ModelAttribute("trip") Trip trip,
                             BindingResult result,
                             Principal principal,
                             Model model) {
        if (result.hasErrors()) {
            return "create-trip";
        }

        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        trip.setUser(user);

        Trip savedTrip = tripRepository.save(trip);
        return "redirect:/trips/" + savedTrip.getId();
    }

    @Transactional
    @PostMapping("/trips")
    public String createTripAlternative(@Valid @ModelAttribute("trip") Trip trip, BindingResult result, Principal principal, Model model) {
        return createTrip(trip, result, principal, model);
    }

    @GetMapping("/trips/{id}")
    public String viewTripDetails(@PathVariable Long id, Model model) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid trip Id:" + id));
        List<Expense> expenses = expenseRepository.findByTrip(trip);

        BigDecimal totalSpent = expenses.stream()
                .map(Expense::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalBudget = trip.getTotalBudget();
        BigDecimal remaining = totalBudget.subtract(totalSpent);

        boolean isOverBudget = totalSpent.compareTo(totalBudget) > 0;

        BigDecimal lowThreshold = new BigDecimal("2000");
        boolean isLowBudget = !isOverBudget && remaining.compareTo(lowThreshold) <= 0 && remaining.compareTo(BigDecimal.ZERO) > 0;

        double progress = 0;
        if (totalBudget.compareTo(BigDecimal.ZERO) > 0) {
            progress = totalSpent.doubleValue() / totalBudget.doubleValue() * 100;
        }
        int progressInt = (int) Math.min(progress, 100);

        model.addAttribute("trip", trip);
        model.addAttribute("expenses", expenses);
        model.addAttribute("newExpense", new Expense());
        model.addAttribute("totalSpent", String.format("%.2f", totalSpent));
        model.addAttribute("remaining", String.format("%.2f", remaining));
        model.addAttribute("rawRemaining", remaining);
        model.addAttribute("progress", progressInt);

        model.addAttribute("isOverBudget", isOverBudget);
        model.addAttribute("isLowBudget", isLowBudget);

        return "trip-details";
    }

    @Transactional
    @PostMapping({"/trips/{id}/add-expense", "/trips/{id}/expenses"})
    public String addExpense(@PathVariable Long id, @ModelAttribute Expense expense) {
        Trip trip = tripRepository.findById(id).orElseThrow();
        expense.setId(null);
        expense.setTrip(trip);
        expenseRepository.saveAndFlush(expense);
        return "redirect:/trips/" + id;
    }

    @GetMapping("/trips/{tripId}/expenses/{expenseId}/edit")
    public String showEditExpenseForm(@PathVariable Long tripId, @PathVariable Long expenseId, Model model) {
        Trip trip = tripRepository.findById(tripId).orElseThrow();
        Expense expense = expenseRepository.findById(expenseId).orElseThrow();

        model.addAttribute("trip", trip);
        model.addAttribute("expense", expense);
        return "edit-expense";
    }

    @PostMapping("/trips/{tripId}/expenses/{expenseId}/edit")
    public String updateExpense(@PathVariable Long tripId, @PathVariable Long expenseId, @ModelAttribute Expense expenseDetails) {
        Expense expense = expenseRepository.findById(expenseId).orElseThrow();
        expense.setTitle(expenseDetails.getTitle());
        expense.setCategory(expenseDetails.getCategory());
        expense.setCost(expenseDetails.getCost());
        expenseRepository.save(expense);
        return "redirect:/trips/" + tripId;
    }

    @GetMapping("/trips/{id}/expenses")
    public String viewTripExpenses(@PathVariable Long id) {
        return "redirect:/trips/" + id;
    }

    @Transactional
    @GetMapping({"/trips/{tripId}/expenses/{expenseId}/delete", "/trips/{tripId}/expenses/{expenseId}/remove"})
    public String deleteExpense(@PathVariable Long tripId, @PathVariable Long expenseId) {
        if(expenseRepository.existsById(expenseId)) {
            expenseRepository.deleteById(expenseId);
        }
        return "redirect:/trips/" + tripId;
    }

    @GetMapping("/trips/{id}/export")
    public void exportToPDF(HttpServletResponse response, @PathVariable Long id) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Trip_Itinerary_" + id + ".pdf";
        response.setHeader(headerKey, headerValue);
        Trip trip = tripRepository.findById(id).orElseThrow();
        List<Expense> expenses = expenseRepository.findByTrip(trip);
        pdfService.generateItinerary(response.getOutputStream(), trip, expenses);
    }

    @GetMapping("/reviews")
    public String showReviews(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        List<Trip> trips = tripRepository.findByUser(user);

        model.addAttribute("username", username);
        model.addAttribute("trips", trips);
        return "review";
    }

    @PostMapping("/reviews")
    public String submitReview(@RequestParam Long tripId,
                               @RequestParam int rating,
                               @RequestParam(required = false) String highlight,
                               @RequestParam(required = false) String feedback,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {

        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        Trip trip = tripRepository.findById(tripId).orElseThrow();

        if (!trip.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("reviewError", "Trip not found for your account.");
            return "redirect:/reviews";
        }

        int safeRating = Math.max(1, Math.min(5, rating));
        redirectAttributes.addFlashAttribute("reviewMessage", "Thanks for reviewing your trip to " + trip.getEndLocation() + "!");
        redirectAttributes.addFlashAttribute("lastRating", safeRating);
        redirectAttributes.addFlashAttribute("lastTrip", trip.getStartLocation() + " -> " + trip.getEndLocation());
        redirectAttributes.addFlashAttribute("lastHighlight", highlight);
        redirectAttributes.addFlashAttribute("lastFeedback", feedback);

        return "redirect:/reviews";
    }
}