package database.manager;
import database.util.CSVUtils;
import database.util.ExpenseCSVParser;
import expense.Expense;
import expense.ExpenseManager;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Set<Expense> set = new HashSet<>(expenses);
        log.info("Loaded {} expenses from CSV file.", expenses.size());
        expenseManager.setExpenses(set);
        log.info("Expenses have been added to the expense manager.");
    }

    public void saveExpenses() throws IOException {
        List<Expense> expenses = new ArrayList<>(expenseManager.getExpenses());
        ExpenseCSVParser.writeCSV(FILE_PATH, expenses);
    }
}
