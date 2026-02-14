package com.budgettrip.repository;

import com.budgettrip.entity.Expense;
import com.budgettrip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByTrip(Trip trip);
}