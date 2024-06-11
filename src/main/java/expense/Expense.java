package expense;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class Expense {
    private String description;
    private double amount;
    private LocalDate expenseDate;
    private String category;
    private String nameShop;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public Expense(String description, double amount, LocalDate expenseDate, String category, String nameShop) {
        this.description = description;
        this.amount = amount;
        this.expenseDate = expenseDate;
        this.category = category;
        this.nameShop = nameShop;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNameShop() {
        return nameShop;
    }

    public void setNameShop(String nameShop) {
        this.nameShop = nameShop;
    }

    public StringProperty descriptionProperty() {
        return new SimpleStringProperty(description);
    }

    public DoubleProperty amountProperty() {
        return new SimpleDoubleProperty(amount);
    }

    public ObjectProperty<LocalDate> expenseDateProperty() {
        return new SimpleObjectProperty<>(expenseDate);
    }

    public StringProperty categoryProperty() {
        return new SimpleStringProperty(category);
    }

    public StringProperty nameShopProperty() {
        return new SimpleStringProperty(nameShop);
    }

    @Override
    public String toString() {
        return "Expense{" +
                "description='" + description + '\'' +
                ", amount=" + amount +
                ", expenseDate=" + expenseDate.format(DATE_FORMATTER) +
                ", category='" + category + '\'' +
                ", nameShop='" + nameShop + '\'' +
                '}';
    }
}
