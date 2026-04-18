package com.budgettrip.service;

import com.budgettrip.entity.Expense;
import com.budgettrip.entity.Trip;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class PdfService {

    public void generateItinerary(OutputStream outputStream, Trip trip, List<Expense> expenses) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, outputStream);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);

        String routeTitle = trip.getStartLocation() + " ➝ " + trip.getEndLocation();
        Paragraph title = new Paragraph("Trip Itinerary: " + routeTitle, titleFont);

        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Distance: " + trip.getDistanceKm() + " km"));
        document.add(new Paragraph("Dates: " + trip.getStartDate() + " to " + trip.getEndDate()));
        document.add(new Paragraph("Total Budget: Rs. " + trip.getTotalBudget()));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);

        addTableHeader(table, "Item");
        addTableHeader(table, "Category");
        addTableHeader(table, "Cost (Rs.)");

        for (Expense expense : expenses) {
            table.addCell(expense.getTitle());
            String category = (expense.getCategory() != null) ? expense.getCategory().toString() : "N/A";
            table.addCell(category);
            table.addCell(expense.getCost().toString());
        }

        document.add(table);
        document.close();
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setPhrase(new Phrase(headerTitle));
        header.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(header);
    }
}
