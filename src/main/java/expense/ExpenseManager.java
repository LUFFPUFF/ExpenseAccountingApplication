package expense;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Log4j2
public class ExpenseManager {

    private Set<Expense> expenses = new HashSet<>();
    private final ObservableList<Expense> expensesObservableList = FXCollections.observableArrayList();

    public void addExpense(Expense expense) {
        if (expense.getAmount() > 0) {
            expenses.add(expense);
        }
    }

    public void addExpenses(List<Expense> newExpenses) {
        int initialSize = expenses.size();
        expenses.addAll(newExpenses);
        int addedCount = expenses.size() - initialSize;
        log.info("Added {} new expenses, {} total expenses now.", addedCount, expenses.size());
    }

    public void removeExpense(Expense expense) {
        expenses.remove(expense);
    }

    public List<Expense> getExpensesInDateRange(LocalDate startDate, LocalDate endDate) {
        return expenses.stream()
                .filter(expense -> expense.getExpenseDate().isAfter(startDate) && expense.getExpenseDate().isBefore(endDate))
                .collect(Collectors.toList());
    }

    public List<Expense> getExpensesByDate(LocalDate date) {
        return expenses.stream()
                .filter(expense -> expense.getExpenseDate().equals(date))
                .collect(Collectors.toList());
    }

    public double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }
}
