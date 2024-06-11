package gui.view.expense;

import expense.Expense;
import expense.ExpenseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Optional;

@Getter
public class ExpenseTable {

    private final TableView<Expense> tableView;
    private final ObservableList<Expense> expenseList;
    private final ExpenseManager expenseManager;

    public ExpenseTable(ExpenseManager expenseManager) {
        this.expenseManager = expenseManager;
        tableView = new TableView<>();
        expenseList = FXCollections.observableArrayList(expenseManager.getExpenses());
        tableView.setItems(expenseList);

        TableColumn<Expense, String> descriptionColumn = new TableColumn<>("Описание");
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        TableColumn<Expense, Double> amountColumn = new TableColumn<>("Сумма");
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        TableColumn<Expense, LocalDate> dateColumn = new TableColumn<>("Дата");
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().expenseDateProperty());

        TableColumn<Expense, String> categoryColumn = new TableColumn<>("Категория");
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

        TableColumn<Expense, String> nameShopColumn = new TableColumn<>("Магазин");
        nameShopColumn.setCellValueFactory(cellData -> cellData.getValue().nameShopProperty());

        tableView.getColumns().addAll(descriptionColumn, amountColumn, dateColumn, categoryColumn, nameShopColumn);
    }



    // Метод для добавления нового расхода
    public void addNewExpense(Stage primaryStage) {
        Dialog<Expense> dialog = new Dialog<>();
        dialog.setTitle("Добавить расход");
        dialog.setHeaderText("Введите данные для нового расхода:");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox content = new VBox();
        content.setSpacing(10);

        TextField descriptionField = new TextField();
        TextField amountField = new TextField();
        DatePicker dateField = new DatePicker(LocalDate.now());
        TextField categoryField = new TextField();
        TextField nameShopField = new TextField();

        content.getChildren().addAll(
                new Label("Описание:"), descriptionField,
                new Label("Сумма:"), amountField,
                new Label("Дата:"), dateField,
                new Label("Категория:"), categoryField,
                new Label("Магазин:"), nameShopField
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Expense(
                        descriptionField.getText(),
                        Double.parseDouble(amountField.getText()),
                        dateField.getValue(),
                        categoryField.getText(),
                        nameShopField.getText()
                );
            }
            return null;
        });

        Optional<Expense> result = dialog.showAndWait();
        result.ifPresent(expense -> {
            expenseManager.addExpense(expense);
            expenseList.add(expense);
        });
    }

    // Метод для удаления выбранного расхода
    public void deleteSelectedExpense() {
        Expense selectedExpense = tableView.getSelectionModel().getSelectedItem();
        if (selectedExpense != null) {
            expenseManager.removeExpense(selectedExpense);
            expenseList.remove(selectedExpense);
        }
    }

    // Метод для редактирования выбранного расхода
    public void editSelectedExpense() {
        Expense selectedExpense = tableView.getSelectionModel().getSelectedItem();
        if (selectedExpense != null) {
            Optional<Expense> updatedExpense = showEditDialog(selectedExpense);
            updatedExpense.ifPresent(expense -> {
                expenseManager.updateExpense(selectedExpense, expense);
                tableView.refresh();
            });
        }
    }

    // Метод для отображения диалога редактирования расхода
    public Optional<Expense> showEditDialog(Expense selectedExpense) {
        Dialog<Expense> dialog = new Dialog<>();
        dialog.setTitle("Редактировать расход");
        dialog.setHeaderText("Введите новые данные:");

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        VBox content = new VBox();
        content.setSpacing(10);

        TextField descriptionField = new TextField(selectedExpense.getDescription());
        TextField amountField = new TextField(String.valueOf(selectedExpense.getAmount()));
        DatePicker dateField = new DatePicker(selectedExpense.getExpenseDate());
        TextField categoryField = new TextField(selectedExpense.getCategory());
        TextField nameShopField = new TextField(selectedExpense.getNameShop());

        content.getChildren().addAll(
                new Label("Описание:"), descriptionField,
                new Label("Сумма:"), amountField,
                new Label("Дата:"), dateField,
                new Label("Категория:"), categoryField,
                new Label("Магазин:"), nameShopField
        );

        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Expense(
                        descriptionField.getText(),
                        Double.parseDouble(amountField.getText()),
                        dateField.getValue(),
                        categoryField.getText(),
                        nameShopField.getText()
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }
}


