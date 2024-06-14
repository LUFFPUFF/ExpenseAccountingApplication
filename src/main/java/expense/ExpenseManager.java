package expense;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.time.YearMonth;
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
                .filter(expense -> !expense.getExpenseDate().isBefore(startDate) && !expense.getExpenseDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public List<Expense> getExpensesByDate(LocalDate date) {
        return expenses.stream()
                .filter(expense -> expense.getExpenseDate().equals(date))
                .collect(Collectors.toList());
    }

    public void updateExpense(Expense expense) {
        expenses.remove(expense);
    }

    public double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public List<Expense> filterExpenses(String category, Double minAmount, Double maxAmount, LocalDate startDate, LocalDate endDate) {
        return expenses.stream()
                .filter(expense -> (category == null || expense.getCategory().equalsIgnoreCase(category)) &&
                        (minAmount == null || expense.getAmount() >= minAmount) &&
                        (maxAmount == null || expense.getAmount() <= maxAmount) &&
                        (startDate == null || !expense.getExpenseDate().isBefore(startDate)) &&
                        (endDate == null || !expense.getExpenseDate().isAfter(endDate)))
                .collect(Collectors.toList());
    }

    public List<Expense> sortExpensesByAmount(List<Expense> expenses, boolean ascending) {
        return expenses.stream()
                .sorted(Comparator.comparingDouble(Expense::getAmount).reversed())
                .collect(Collectors.toList());
    }

    public List<Expense> sortExpensesByDate(List<Expense> expenses, boolean ascending) {
        return expenses.stream()
                .sorted(Comparator.comparing(Expense::getExpenseDate).reversed())
                .collect(Collectors.toList());
    }

    public List<Expense> getExpensesInMonth(YearMonth yearMonth) {
        return expenses.stream()
                .filter(expense -> YearMonth.from(expense.getExpenseDate()).equals(yearMonth))
                .collect(Collectors.toList());
    }

    public double getTotalExpensesInDateRange(LocalDate startDate, LocalDate endDate) {
        return getExpensesInDateRange(startDate, endDate).stream()
                .mapToDouble(Expense::getAmount).sum();
    }
}
