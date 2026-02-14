🇱🇰 BudgetTrip - Sri Lanka Tourism Planner

BudgetTrip is a Spring Boot web application designed to help tourists planning to visit Sri Lanka manage their itineraries and budgets effectively. It features budget tracking, expense management, PDF itinerary generation, and smart activity recommendations based on remaining funds.

🚀 Key Features

1. User Management & Security

Secure User Registration & Login (Session-based).

Password Encryption using BCrypt.

Role-based authorization (Traveler role).

2. Trip Management (CRUD)

Create new trips with Destinations, Dates, and Total Budget.

Dashboard view with Pagination (view 6 trips per page).

Visual progress bar indicating budget usage.

3. Expense Tracking

Add expenses to specific trips (Flights, Hotels, Food, etc.).

Real-time calculation of "Total Spent" and "Remaining Budget".

4. 🌟 Smart Recommendations (Beyond CRUD)

The system automatically suggests Sri Lankan activities (e.g., Sigiriya, Whale Watching) that fit within the user's remaining budget.

5. 📄 Reporting (Beyond CRUD)

Export the full trip itinerary and expense list as a PDF Document.

6. Reliability

Custom Error Pages for handling 404 and Server Errors gracefully.

🛠 Tech Stack

Backend: Java 17, Spring Boot 3.x

Database: MySQL

Frontend: Thymeleaf, Tailwind CSS (via CDN)

Security: Spring Security

PDF Generation: OpenPDF / iText

Build Tool: Maven

⚙️ Setup & Run Instructions

Prerequisites

Java JDK 17 or higher

MySQL Server installed and running

Step 1: Database Setup

Open MySQL Workbench and run:

CREATE DATABASE budget_trip_db;


Step 2: Configure Application

Open src/main/resources/application.properties and update your MySQL credentials:

spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD


Step 3: Run the Application

Open the project in IntelliJ IDEA.

Run BudgetTripApplication.java.

The app will start at http://localhost:8080.

📸 Usage Guide

Register: Create a new account at /register.

Login: Sign in to access your dashboard.

Plan Trip: Click "Plan New Trip" and enter details.

Manage: Click "View & Add Expenses" on a trip card.

Add Expense: Add costs like "Train Ticket to Ella".

Recommendations: Check the sidebar for suggested activities based on your balance.

Export: Click the "Export PDF" button to download your plan.

Developed for the Spring Boot Group Assignment.