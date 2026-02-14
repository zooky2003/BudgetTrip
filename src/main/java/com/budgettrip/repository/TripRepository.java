package com.budgettrip.repository;

import com.budgettrip.entity.Trip;
import com.budgettrip.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    Page<Trip> findByUser(User user, Pageable pageable);

    List<Trip> findByUser(User user);
}