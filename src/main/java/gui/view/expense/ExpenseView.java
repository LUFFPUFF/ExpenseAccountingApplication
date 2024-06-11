package gui.view.expense;

import database.DatabaseManager;
import expense.Expense;
import expense.ExpenseManager;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.control.DatePicker;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.LocalDate;

@Log4j2
public class ExpenseView {

    private final ExpenseManager expenseManager;
    private final DatabaseManager databaseManager;
    @Getter
    private final VBox view;

    public ExpenseView(ExpenseManager expenseManager, DatabaseManager databaseManager) {
        this.expenseManager = expenseManager;
        this.databaseManager = databaseManager;
        view = new VBox(10);
        view.setPadding(new Insets(10));

        createExpenseView();
    }

    public void createExpenseView() {
        Label label = new Label("Добавить расход:");

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Описание");

        TextField amountField = new TextField();
        amountField.setPromptText("Сумма");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Дата");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Категория");

        TextField nameShopField = new TextField();
        nameShopField.setPromptText("Магазин");

        Button addButton = new Button("Добавить");
        addButton.setOnAction(_ -> {
            String description = descriptionField.getText();
            double amount = Double.parseDouble(amountField.getText());
            LocalDate date = datePicker.getValue();
            String category = categoryField.getText();
            String nameShop = nameShopField.getText();

            Expense expense = new Expense(description, amount, date, category, nameShop);
            expenseManager.addExpense(expense);

            try {
                databaseManager.saveDatabase();
            } catch (IOException e) {
                log.error("Error saving expense", e);
                try {
                    throw new IOException(e.getMessage());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            descriptionField.clear();
            amountField.clear();
            datePicker.setValue(null);
            categoryField.clear();
            nameShopField.clear();
        });

        view.getChildren().addAll(label, descriptionField, amountField, datePicker, categoryField, nameShopField, addButton);
    }

}
