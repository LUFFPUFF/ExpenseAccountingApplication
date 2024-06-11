package database.util;

import expense.Expense;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ExpenseCSVParser {

    public static List<Expense> parseCSV(String filePath) throws IOException {
        List<String[]> csvData = CSVUtils.readCSV(filePath);
        List<Expense> expenses = new ArrayList<>();

        for (String[] line : csvData) {
            if (line.length < 5) {
                // Пропускаем строки, которые содержат меньше 5 элементов
                continue;
            }
            String description = line[0];
            double amount;
            try {
                amount = Double.parseDouble(line[1]);
            } catch (NumberFormatException e) {
                // В случае ошибки парсинга суммы, пропускаем эту строку
                continue;
            }
            LocalDate date;
            try {
                date = LocalDate.parse(line[2]);
            } catch (DateTimeParseException e) {
                // В случае ошибки парсинга даты, пропускаем эту строку
                continue;
            }
            String nameShop = line[3];
            String category = line[4];

            Expense expense = new Expense(description, amount, date, nameShop, category);
            expenses.add(expense);
        }

        return expenses;
    }

    public static void writeCSV(String filePath, List<Expense> expenses) throws IOException {
        try {
            List<String[]> csvList = new ArrayList<>();

            for (Expense expense : expenses) {
                String[] line = {
                        expense.getDescription(),
                        String.valueOf(expense.getAmount()),
                        expense.getExpenseDate().toString(), // Convert date to string
                        expense.getNameShop(),
                        expense.getCategory()
                };
                csvList.add(line);
            }

            CSVUtils.writeCSV(filePath, csvList);
        } catch (IOException e) {
            log.error("Error while writing CSV file: {}", e.getMessage());
            throw e;
        }
    }
}
