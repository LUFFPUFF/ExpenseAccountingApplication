package database;


import expense.Expense;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Log4j2
public class ExpenseDatabaseManager {

    private DatabaseManager databaseManager;

    public void addExpense(Expense expense) {
        String query = "INSERT INTO expenses (description, amount, expenseDate, category, nameShop) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, expense.getDescription());
            statement.setDouble(2, expense.getAmount());
            statement.setDate(3, java.sql.Date.valueOf(expense.getExpenseDate()));
            statement.setString(4, expense.getCategory());
            statement.setString(5, expense.getNameShop());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to add expense: {}", e.getMessage());
        }
    }

    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        String query = "SELECT * FROM expenses";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Expense expense = new Expense(
                        resultSet.getString("description"),
                        resultSet.getDouble("amount"),
                        resultSet.getDate("expenseDate").toLocalDate(),
                        resultSet.getString("category"),
                        resultSet.getString("nameShop")
                );
                expenses.add(expense);
            }
        } catch (SQLException e) {
            log.error("Failed to get all expenses: {}", e.getMessage());
        }
        return expenses;
    }
}
