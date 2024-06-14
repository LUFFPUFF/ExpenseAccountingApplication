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

import java.io.IOException;
import java.time.LocalDate;

public class MainApp extends Application {

    private ExpenseManager expenseManager;
    private DatabaseManager databaseManager;

    @SneakyThrows
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Календарь");

        expenseManager = new ExpenseManager(); // Создаем менеджер расходов
        databaseManager = new DatabaseManager(expenseManager);

        try {
            databaseManager.loadDatabase();
        } catch (IOException e) {
            showAlert();
        }

        BorderPane rootLayout = new BorderPane();
        Scene calendarScene = new Scene(rootLayout, 800, 600);
        primaryStage.setScene(calendarScene);

        CalendarView calendarView = new CalendarView(expenseManager);
        rootLayout.setCenter(calendarView.getView());

        calendarView.setDateSelectedListener(this::showExpenseTable);

        primaryStage.show();
    }

    private void showExpenseTable(LocalDate date) {
        ExpenseTableView expenseTableView = new ExpenseTableView(date, expenseManager, databaseManager);
        expenseTableView.show();
    }

    public static void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Ошибка загрузки данных");
        alert.setContentText("Не удалось загрузить данные из базы данных. ");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
