package expense;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Log4j2
public class ExpenseManager {

    private List<Expense> expenses = new ArrayList<>();
    private final ObservableList<Expense> expensesObservableList = FXCollections.observableArrayList();

    public void addExpense(Expense expense) {
        if (expense.getAmount() > 0) {
            expenses.add(expense);
        }
    }

    public void removeExpense(Expense expense) {
        expenses.remove(expense);
    }

    public void updateExpense(Expense oldExpense, Expense updatedExpense) {
        int index = expenses.indexOf(oldExpense);
        if (index != -1) {
            expenses.set(index, updatedExpense);
        }
    }

    public List<Expense> getExpensesInDateRange(LocalDate startDate, LocalDate endDate) {
        return expenses.stream()
                .filter(expense -> expense.getExpenseDate().isAfter(startDate) && expense.getExpenseDate().isBefore(endDate))
                .collect(Collectors.toList());
    }

    public List<Expense> getExpensesByDate(LocalDate date) {
        List<Expense> expensesByDate = expenses.stream()
                .filter(expense -> expense.getExpenseDate().equals(date))
                .collect(Collectors.toList());
        log.info("Found {} expenses for date {}", expensesByDate.size(), date);
        return expensesByDate;
    }

    public double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
        this.expensesObservableList.setAll(expenses);
    }
}
