package expense;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;


public class Expense {
    private SimpleStringProperty description;
    private SimpleDoubleProperty amount;
    private SimpleObjectProperty<LocalDate> expenseDate;
    private SimpleStringProperty category;
    private SimpleStringProperty nameShop;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public Expense(String description, double amount, LocalDate expenseDate, String category, String nameShop) {
        this.description = new SimpleStringProperty(description);
        this.amount = new SimpleDoubleProperty(amount);
        this.expenseDate = new SimpleObjectProperty<>(expenseDate);
        this.category = new SimpleStringProperty(category);
        this.nameShop = new SimpleStringProperty(nameShop);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public DoubleProperty amountProperty() {
        return amount;
    }

    public ObjectProperty<LocalDate> expenseDateProperty() {
        return expenseDate;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public StringProperty nameShopProperty() {
        return nameShop;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public double getAmount() {
        return amount.get();
    }

    public void setAmount(double amount) {
        this.amount.set(amount);
    }

    public LocalDate getExpenseDate() {
        return expenseDate.get();
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate.set(expenseDate);
    }

    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public String getNameShop() {
        return nameShop.get();
    }

    public void setNameShop(String nameShop) {
        this.nameShop.set(nameShop);
    }

    @Override
    public String toString() {
        return "Expense{" +
                "description=" + description +
                ", amount=" + amount +
                ", expenseDate=" + expenseDate +
                ", category=" + category +
                ", nameShop=" + nameShop +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Double.compare(expense.getAmount(), getAmount()) == 0 &&
                Objects.equals(getDescription(), expense.getDescription()) &&
                Objects.equals(getExpenseDate(), expense.getExpenseDate()) &&
                Objects.equals(getCategory(), expense.getCategory()) &&
                Objects.equals(getNameShop(), expense.getNameShop());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription(), getAmount(), getExpenseDate(), getCategory(), getNameShop());
    }
}
