package com.budgettrip.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private static final List<Activity> ALL_ACTIVITIES = new ArrayList<>();

    static {
        ALL_ACTIVITIES.add(new Activity("Sigiriya Rock Fortress", new BigDecimal("3000"), "CULTURAL"));
        ALL_ACTIVITIES.add(new Activity("Mirissa Whale Watching", new BigDecimal("5000"), "ADVENTURE"));
        ALL_ACTIVITIES.add(new Activity("Ella Train Ride", new BigDecimal("1000"), "SCENIC"));
        ALL_ACTIVITIES.add(new Activity("Yala Safari Jeep", new BigDecimal("12000"), "WILDLIFE"));
        ALL_ACTIVITIES.add(new Activity("Street Food Tour", new BigDecimal("1500"), "FOOD"));
        ALL_ACTIVITIES.add(new Activity("Temple of Tooth", new BigDecimal("2000"), "CULTURAL"));
        ALL_ACTIVITIES.add(new Activity("Surfing Lesson", new BigDecimal("4000"), "ADVENTURE"));
    }

    public List<Activity> getRecommendations(BigDecimal remainingBudget) {
        return ALL_ACTIVITIES.stream()
                .filter(activity -> activity.cost.compareTo(remainingBudget) <= 0)
                .collect(Collectors.toList());
    }

    public static class Activity {
        public String name;
        public BigDecimal cost;
        public String type;

        public Activity(String name, BigDecimal cost, String type) {
            this.name = name;
            this.cost = cost;
            this.type = type;
        }
    }
}