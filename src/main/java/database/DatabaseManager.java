package database;

import database.manager.ExpenseCSVManager;
import database.manager.ProductCSVManager;
import expense.ExpenseManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import product.ProductManager;

import java.io.IOException;
import java.time.LocalDate;

@Log4j2
public class DatabaseManager {

    private final ExpenseCSVManager expenseCSVManager;

    public DatabaseManager(ExpenseManager expenseManager) {
        this.expenseCSVManager = new ExpenseCSVManager(expenseManager);
    }

    public void loadDatabase() throws IOException {
        expenseCSVManager.loadExpenses();
    }

    public void saveDatabase() throws IOException {
        expenseCSVManager.saveExpenses();
    }
}
