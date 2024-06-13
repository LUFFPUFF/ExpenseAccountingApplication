package database;

import database.manager.ExpenseCSVManager;
import expense.ExpenseManager;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

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
