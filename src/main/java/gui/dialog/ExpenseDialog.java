package gui.dialog;

import database.DatabaseManager;
import expense.Expense;
import expense.ExpenseManager;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import logger.MyLogger;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.LocalDate;

@Log4j2
@AllArgsConstructor
public class ExpenseDialog {

    private final ExpenseManager expenseManager;
    private final DatabaseManager databaseManager;
    private final ObservableList<Expense> expenseList;

    private static final String ERROR = "Ошибка";
    private static final String ERROR_DATA = "Пожалуйста, введите корректные данные для суммы.";
    private static final String ERROR_SAVE = "Не удалось сохранить данные в базу данных. ";

    public void showAddExpenseDialog() {
        Dialog<Expense> dialog = new Dialog<>();
        dialog.setTitle("Добавить расход");
        dialog.setHeaderText("Введите информацию о расходе:");

        TextField descriptionField = new TextField();
        TextField amountField = new TextField();
        TextField categoryField = new TextField();
        TextField nameShopField = new TextField();

        dialog.getDialogPane().setContent(new VBox(8,
                new Label("Описание:"), descriptionField,
                new Label("Сумма:"), amountField,
                new Label("Категория:"), categoryField,
                new Label("Магазин:"), nameShopField
        ));

        ButtonType addButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                try {
                    String description = descriptionField.getText();
                    double amount = Double.parseDouble(amountField.getText());
                    String category = categoryField.getText();
                    String nameShop = nameShopField.getText();
                    Expense expense = new Expense(description, amount, LocalDate.now(), category, nameShop);
                    expenseManager.addExpense(expense);
                    databaseManager.saveDatabase();
                    return expense;
                } catch (NumberFormatException e) {
                    MyLogger.error("NumberFormatException " + e.getMessage());
                    showErrorAlert(ERROR_DATA);
                    return null;
                } catch (IOException e) {
                    MyLogger.error("Error save is database");
                    showErrorAlert(ERROR_SAVE + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(expenseList::add);
    }

    public void showEditExpenseDialog(Expense expense) {
        Dialog<Expense> dialog = new Dialog<>();
        dialog.setTitle("Редактировать расход");
        dialog.setHeaderText("Измените информацию о расходе:");

        TextField descriptionField = new TextField(expense.getDescription());
        TextField amountField = new TextField(Double.toString(expense.getAmount()));
        TextField categoryField = new TextField(expense.getCategory());
        TextField nameShopField = new TextField(expense.getNameShop());

        dialog.getDialogPane().setContent(new VBox(8,
                new Label("Описание:"), descriptionField,
                new Label("Сумма:"), amountField,
                new Label("Категория:"), categoryField,
                new Label("Магазин:"), nameShopField
        ));

        ButtonType saveButton = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                try {
                    expense.setDescription(descriptionField.getText());
                    expense.setAmount(Double.parseDouble(amountField.getText()));
                    expense.setCategory(categoryField.getText());
                    expense.setNameShop(nameShopField.getText());
                    expenseManager.updateExpense(expense);
                    databaseManager.saveDatabase();
                    return expense;
                } catch (NumberFormatException e) {
                    MyLogger.error("NumberFormatException: " + e.getMessage());
                    showErrorAlert(ERROR_DATA);
                    return null;
                } catch (IOException e) {
                    MyLogger.error("Error save is database");
                    showErrorAlert(ERROR_SAVE + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
