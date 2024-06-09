package expense;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Getter
public class ExpenseManager {

    private final ArrayList<Expense> expenses = new ArrayList<>();

    public void addExpense(Expense expense) {
        if (expense.getAmount() > 0) {
            expenses.add(expense);
        }
    }

    public void removeExpense(Expense expense) {
        if (expenses.remove(expense)) {
            log.info("Расход удален: {}", expense);
        } else {
            log.error("Не удалось удалить расход: {}", expense);
        }
    }

    public void updateExpense(Expense oldExpense, Expense updatedExpense) {
        if (expenses.contains(oldExpense)) {
            int index = expenses.indexOf(oldExpense);
            expenses.set(index, updatedExpense);
            log.info("Расход обновлен: {}", updatedExpense);
        } else {
            log.error("Расход для обновления не найден: {}", oldExpense);
        }
    }

    public List<Expense> getExpensesInDateRange(LocalDate startDate, LocalDate endDate) {
        List<Expense> expensesInDateRange = expenses.stream()
                .filter(expense -> expense.getExpenseDate().isAfter(startDate) && expense.getExpenseDate().isBefore(endDate))
                .collect(Collectors.toList());

        log.info("Расходы за период с {} по {}: {}", startDate, endDate, expensesInDateRange.size());
        return expensesInDateRange;
    }

    public Map<String, Double> getCategoryWiseExpense(LocalDate startDate, LocalDate endDate) {
        Map<String, Double> categoryWiseExpense = expenses.stream()
                .filter(expense -> expense
                        .getExpenseDate().isAfter(startDate)
                        && expense.getExpenseDate().isBefore(endDate))
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        log.info("Статистика расходов по категориям за период с {} по {}: {}", startDate, endDate, categoryWiseExpense);
        return categoryWiseExpense;
    }

    public List<Expense> getExpensesByNameShop(String nameShop) {
        List<Expense> expensesByNameShop = expenses.stream()
                .filter(expense -> expense.getNameShop().equalsIgnoreCase(nameShop))
                .collect(Collectors.toList());

        if (expensesByNameShop.isEmpty()) {
            log.error("Расходы для магазина с именем {} не найдены", nameShop);
        }

        return expensesByNameShop;
    }

    public List<Expense> getExpensesByCategory(String category) {
        List<Expense> expensesByCategory = expenses.stream()
                .filter(expense -> expense.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        if (expensesByCategory.isEmpty()) {
            log.error("Расходы для категории {} не найдены", category);
        }

        return expensesByCategory;
    }

    public List<Expense> getExpensesByDate(LocalDate date) {
        List<Expense> expensesByDate = expenses.stream()
                .filter(expense -> expense.getExpenseDate().equals(date))
                .collect(Collectors.toList());

        if (expensesByDate.isEmpty()) {
            log.error("Расходы для даты {} не найдены", date);
        }

        return expensesByDate;
    }

    public double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }
}
