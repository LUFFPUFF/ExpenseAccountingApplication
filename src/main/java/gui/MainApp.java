package gui;

import database.DatabaseManager;
import expense.ExpenseManager;
import gui.view.expense.ExpenseTableView;
import gui.view.calendar.CalendarView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import product.ProductManager;

import java.io.IOException;
import java.time.LocalDate;

public class MainApp extends Application {

    private CalendarView calendarView;
    private Scene calendarScene;
    private Stage primaryStage;
    private ExpenseManager expenseManager;
    private ProductManager productManager;
    private DatabaseManager databaseManager;

    @SneakyThrows
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Календарь");

        expenseManager = new ExpenseManager(); // Создаем менеджер расходов
        databaseManager = new DatabaseManager(expenseManager); // Создаем менеджер базы данных

        // Загружаем данные из базы данных
        try {
            databaseManager.loadDatabase();
        } catch (IOException e) {
            // В случае ошибки загрузки данных
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка загрузки данных");
            alert.setContentText("Не удалось загрузить данные из базы данных. " + e.getMessage());
            alert.showAndWait();
        }

        BorderPane rootLayout = new BorderPane();
        calendarScene = new Scene(rootLayout, 800, 600);
        primaryStage.setScene(calendarScene);

        calendarView = new CalendarView();
        rootLayout.setCenter(calendarView.getView());

        calendarView.setDateSelectedListener(this::showExpenseTable);

        primaryStage.show();
    }

    private void showExpenseTable(LocalDate date) {
        ExpenseTableView expenseTableView = new ExpenseTableView(date, expenseManager, databaseManager);
        expenseTableView.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
