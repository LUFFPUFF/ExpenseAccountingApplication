package gui.view.expense;

import database.DatabaseManager;
import expense.Expense;
import expense.ExpenseManager;
import javafx.collections.ListChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Log4j2
public class ExpenseTableView {

    private TableView<Expense> tableView;
    private ObservableList<Expense> expenseList;
    private Stage stage;
    private ExpenseManager expenseManager;
    private DatabaseManager databaseManager;
    private boolean isDataLoaded = false; // Флаг для отслеживания загрузки данных

    public ExpenseTableView(LocalDate date, ExpenseManager expenseManager, DatabaseManager databaseManager) {
        this.expenseManager = expenseManager;
        this.databaseManager = databaseManager;
        initializeTableView();
        loadDataFromDatabase(date);
    }

    private void initializeTableView() {
        stage = new Stage();
        stage.setTitle("Таблица расходов");

        tableView = new TableView<>();
        expenseList = FXCollections.observableArrayList();
        tableView.setItems(expenseList);

        TableColumn<Expense, String> descriptionColumn = new TableColumn<>("Описание");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Expense, Double> amountColumn = new TableColumn<>("Сумма");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Expense, LocalDate> dateColumn = new TableColumn<>("Дата");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("expenseDate"));

        TableColumn<Expense, String> categoryColumn = new TableColumn<>("Категория");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Expense, String> nameShopColumn = new TableColumn<>("Магазин");
        nameShopColumn.setCellValueFactory(new PropertyValueFactory<>("nameShop"));

        tableView.getColumns().addAll(descriptionColumn, amountColumn, dateColumn, categoryColumn, nameShopColumn);

        VBox root = new VBox(tableView);

        Button addButton = new Button("Добавить");
        addButton.setOnAction(event -> showAddExpenseDialog());
        Button removeButton = new Button("Удалить");
        removeButton.setOnAction(event -> removeSelectedExpense());

        Button totalButton = new Button("Общие расходы");
        totalButton.setOnAction(event -> showTotalExpenses());

        HBox buttonBox = new HBox(addButton, removeButton, totalButton);
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(buttonBox);

        tableView.setRowFactory(tv -> {
            TableRow<Expense> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Expense selectedExpense = row.getItem();
                    if (selectedExpense != null) {
                        showExpenseInfoDialog(selectedExpense);
                    }
                }
            });
            return row;
        });

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
    }

    public void loadDataFromDatabase(LocalDate date) {
        if (!isDataLoaded) {
            try {
                log.info("Loading data from database.");
                databaseManager.loadDatabase(); // Загружаем данные из базы данных
                List<Expense> expenses = expenseManager.getExpensesByDate(date);
                log.info("Loaded {} expenses for date {}.", expenses.size(), date);
                expenseList.addAll(expenses);
                isDataLoaded = true;
                log.info("Data has been loaded successfully.");
            } catch (IOException e) {
                log.error("Error loading data from database: {}", e.getMessage(), e);
                showErrorAlert("Ошибка загрузки данных из базы данных", e.getMessage());
            }
        } else {
            log.info("Data is already loaded, skipping.");
        }
    }

    public void show() {
        stage.show();
    }

    private void showAddExpenseDialog() {
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
                    showErrorAlert("Ошибка", "Пожалуйста, введите корректные данные для суммы.");
                    return null;
                } catch (IOException e) {
                    showErrorAlert("Ошибка", "Не удалось сохранить данные в базу данных. " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(expenseList::add);
    }

    private void removeSelectedExpense() {
        Expense selectedExpense = tableView.getSelectionModel().getSelectedItem();
        if (selectedExpense != null) {
            expenseManager.removeExpense(selectedExpense);
            expenseList.remove(selectedExpense);
            try {
                databaseManager.saveDatabase();
            } catch (IOException e) {
                showErrorAlert("Ошибка", "Не удалось сохранить данные в базу данных. " + e.getMessage());
            }
        }
    }

    private void showTotalExpenses() {
        double totalExpenses = expenseManager.getTotalExpenses();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Общие расходы");
        alert.setHeaderText(null);
        alert.setContentText("Общие расходы: " + totalExpenses);
        alert.showAndWait();
    }

    private void showExpenseInfoDialog(Expense expense) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Информация о расходе");
        dialog.setHeaderText("Детали расхода:");

        Label descriptionLabel = new Label("Описание: " + expense.getDescription());
        Label amountLabel = new Label("Сумма: " + expense.getAmount());
        Label dateLabel = new Label("Дата: " + expense.getExpenseDate());
        Label categoryLabel = new Label("Категория: " + expense.getCategory());
        Label nameShopLabel = new Label("Магазин: " + expense.getNameShop());

        VBox content = new VBox(10);
        content.getChildren().addAll(descriptionLabel, amountLabel, dateLabel, categoryLabel, nameShopLabel);
        dialog.getDialogPane().setContent(content);

        ButtonType closeButton = new ButtonType("Закрыть", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(closeButton);

        dialog.setResultConverter(dialogButton -> null);
        dialog.showAndWait();
    }

    // Метод для отображения сообщения об ошибке
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}