package gui.dialog;

import database.DatabaseManager;
import expense.Expense;
import expense.ExpenseManager;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class ExpenseDialog extends Stage {

    public ExpenseDialog(ExpenseManager expenseManager, DatabaseManager databaseManager, LocalDate date) {
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Добавить расход");

        VBox dialogVbox = new VBox(10);
        dialogVbox.setPadding(new Insets(10));

        TextField descriptionField = new TextField();
        descriptionField.setPromptText("Описание");

        TextField amountField = new TextField();
        amountField.setPromptText("Сумма");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Категория");

        TextField nameShopField = new TextField();
        nameShopField.setPromptText("Магазин");

        Button addButton = new Button("Добавить");
        addButton.setOnAction(event -> {
            String description = descriptionField.getText();
            double amount = Double.parseDouble(amountField.getText());
            String category = categoryField.getText();
            String nameShop = nameShopField.getText();

            Expense expense = new Expense(description, amount, date, category, nameShop);
            expenseManager.addExpense(expense);

            try {
                databaseManager.saveDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }

            close();
        });

        dialogVbox.getChildren().addAll(
                new Label("Добавить расход"),
                descriptionField,
                amountField,
                categoryField,
                nameShopField,
                addButton
        );

        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        setScene(dialogScene);
    }
}
