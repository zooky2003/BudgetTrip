package com.budgettrip.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DistanceService {

    private final String NOMINATIM_API = "https://nominatim.openstreetmap.org/search?format=json&q=";

    public double getDistance(String startCity, String endCity) {
        try {
            Location loc1 = getCoordinates(startCity);
            Location loc2 = getCoordinates(endCity);

            if (loc1 == null || loc2 == null) {
                return 0.0;
            }
            return calculateHaversine(loc1.lat, loc1.lon, loc2.lat, loc2.lon);

        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private Location getCoordinates(String city) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = NOMINATIM_API + city + ", Sri Lanka";

            String response = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            if (root.isArray() && !root.isEmpty()) {
                JsonNode firstResult = root.get(0);
                double lat = Double.parseDouble(firstResult.get("lat").asText());
                double lon = Double.parseDouble(firstResult.get("lon").asText());
                return new Location(lat, lon);
            }
        } catch (Exception e) {
            System.out.println("Error finding coordinates for: " + city);
        }
        return null;
    }

    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(R * c * 100.0) / 100.0;
    }

    private static class Location {
        double lat, lon;
        public Location(double lat, double lon) { this.lat = lat; this.lon = lon; }
    }
}