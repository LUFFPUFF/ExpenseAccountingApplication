package gui.view.expense;

import database.DatabaseManager;
import expense.Expense;
import expense.ExpenseManager;
import gui.dialog.ExpenseDialog;
import gui.itemgui.ExpenseAnimation;
import gui.itemgui.ExpenseStatistics;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logger.MyLogger;

import java.io.IOException;
import java.time.LocalDate;

public class ExpenseTableView {

    private final TableView<Expense> tableView;
    private final ObservableList<Expense> expenseList;
    private final Stage stage;
    private final ExpenseManager expenseManager;
    private final DatabaseManager databaseManager;

    private static final String ERROR_LOADING = "Ошибка загрузки данных из базы данных";
    private static final String ERROR_SAVE = "Не удалось сохранить данные в базу данных";

    public ExpenseTableView(LocalDate date, ExpenseManager expenseManager, DatabaseManager databaseManager) {
        this.expenseManager = expenseManager;
        this.databaseManager = databaseManager;
        this.tableView = new TableView<>();
        this.expenseList = FXCollections.observableArrayList();
        this.stage = new Stage();

        initializeTableView(date);
        loadDataFromDatabase(date);
    }

    private void initializeTableView(LocalDate date) {
        stage.setTitle("Таблица расходов");

        setupTableColumns();
        FilteredList<Expense> filteredData = new FilteredList<>(expenseList, _ -> true);
        tableView.setItems(filteredData);

        tableView.setRowFactory(_ -> {
            TableRow<Expense> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty()) {
                    Expense rowData = row.getItem();
                    new ExpenseDialog(expenseManager, databaseManager, expenseList).showEditExpenseDialog(rowData);
                    tableView.refresh();
                }
            });
            return row;
        });

        VBox root = new VBox(10, createFilterField(filteredData), createButtonBox(date), tableView);
        root.setAlignment(Pos.TOP_CENTER);

        setupAnimations();

        stage.setScene(new Scene(root, 800, 600));
    }

    private void setupTableColumns() {
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
    }

    private TextField createFilterField(FilteredList<Expense> filteredData) {
        TextField filterField = new TextField();
        filterField.setPromptText("Поиск...");
        filterField.textProperty().addListener((_, _, newValue) -> {
            filteredData.setPredicate(expense -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return expense.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                        expense.getCategory().toLowerCase().contains(lowerCaseFilter) ||
                        expense.getNameShop().toLowerCase().contains(lowerCaseFilter);
            });
        });
        return filterField;
    }

    private HBox createButtonBox(LocalDate date) {
        Button addButton = new Button("Добавить");
        addButton.setOnAction(_ -> new ExpenseDialog(expenseManager, databaseManager, expenseList).showAddExpenseDialog());

        Button removeButton = new Button("Удалить");
        removeButton.setOnAction(_ -> removeSelectedExpense());

        Button totalButton = new Button("Общие расходы");
        totalButton.setOnAction(_ -> new ExpenseStatistics(expenseManager).showTotalExpenses(date));

        Button pieChartButton = new Button("Диаграмма по категориям");
        pieChartButton.setOnAction(_ -> new ExpenseStatistics(expenseList).showPieChart());

        HBox buttonBox = new HBox(10, addButton, removeButton, totalButton, pieChartButton);
        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }

    private void setupAnimations() {
        ExpenseAnimation.playFadeInAnimation(tableView);
        ExpenseAnimation.playTranslateAnimation(new Button("Добавить"), -200, 0);
        ExpenseAnimation.playTranslateAnimation(new Button("Удалить"), -200, 0);
        ExpenseAnimation.playTranslateAnimation(new Button("Общие расходы"), 200, 0);
        ExpenseAnimation.playTranslateAnimation(new Button("Диаграмма по категориям"), 200, 0);
    }

    public void loadDataFromDatabase(LocalDate date) {
        try {
            databaseManager.loadDatabase();
            expenseList.setAll(expenseManager.getExpensesByDate(date));
        } catch (IOException e) {
            MyLogger.error("Error loading data from database: {}" + e.getMessage());
            showErrorAlert(ERROR_LOADING, e.getMessage());
        }
    }

    public void show() {
        stage.show();
    }

    private void removeSelectedExpense() {
        Expense selectedExpense = tableView.getSelectionModel().getSelectedItem();
        if (selectedExpense != null) {
            expenseManager.removeExpense(selectedExpense);
            expenseList.remove(selectedExpense);
            try {
                databaseManager.saveDatabase();
            } catch (IOException e) {
                MyLogger.error("Error save is database");
                showErrorAlert("Ошибка", ERROR_SAVE + e.getMessage());
            }
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

