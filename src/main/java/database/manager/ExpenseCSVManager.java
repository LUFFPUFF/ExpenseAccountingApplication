package database.manager;
import database.util.CSVUtils;
import database.util.ExpenseCSVParser;
import expense.Expense;
import expense.ExpenseManager;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class ExpenseCSVManager {

    private static final String FILE_PATH = "files/expense.csv";

    private final ExpenseManager expenseManager;

    public ExpenseCSVManager(ExpenseManager expenseManager) {
        this.expenseManager = expenseManager;
    }

    public void loadExpenses() throws IOException {
        log.info("Starting to load expenses from CSV file.");
        List<Expense> expenses = ExpenseCSVParser.parseCSV(FILE_PATH);
        log.info("Loaded {} expenses from CSV file.", expenses.size());
        expenseManager.getExpenses().addAll(expenses);
        log.info("Expenses have been added to the expense manager.");
    }

    public void saveExpenses() throws IOException {
        List<Expense> expenses = expenseManager.getExpenses();
        ExpenseCSVParser.writeCSV(FILE_PATH, expenses);
    }
}
