package gui.view.calendar;

import database.util.NotesCVSParser;
import expense.Expense;
import expense.ExpenseManager;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CalendarView {

    @Getter
    private BorderPane view;
    private YearMonth currentYearMonth;
    private ExpenseManager expenseManager;
    @Setter
    private Consumer<LocalDate> dateSelectedListener;

    private TextArea notesTextArea;
    private Map<YearMonth, String> notesByMonth;

    private static final String FILE_PATH = "files/notes.csv";

    public CalendarView(ExpenseManager expenseManager) {
        this.expenseManager = expenseManager;
        this.view = new BorderPane();
        this.currentYearMonth = YearMonth.now();

        this.notesByMonth = new HashMap<>();
        this.notesTextArea = new TextArea();
        this.notesTextArea.setPromptText("Введите заметку...");
        this.notesTextArea.setPrefRowCount(5);
        this.notesTextArea.getStyleClass().add("text-area-notes"); // Apply CSS class

        try {
            this.notesByMonth = NotesCVSParser.readNotesFromCSV(FILE_PATH);
        } catch (IOException e) {
            this.notesByMonth = new HashMap<>();
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                saveNotesForCurrentMonth();
                NotesCVSParser.writeNotesToCSV(FILE_PATH, notesByMonth);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        loadNotesForCurrentMonth();
        createCalendarView();
    }

    private void createCalendarView() {
        // Navigation controls
        Button prevMonthButton = new Button("<");
        prevMonthButton.setOnAction(event -> animateMonthTransition(currentYearMonth.minusMonths(1)));
        prevMonthButton.getStyleClass().add("button-nav"); // Apply CSS class

        Button nextMonthButton = new Button(">");
        nextMonthButton.setOnAction(event -> animateMonthTransition(currentYearMonth.plusMonths(1)));
        nextMonthButton.getStyleClass().add("button-nav"); // Apply CSS class

        Button viewMonthExpensesButton = new Button("Просмотр расходов за месяц");
        viewMonthExpensesButton.setOnAction(event -> {
            YearMonth selectedYearMonth = currentYearMonth;
            List<Expense> monthExpenses = expenseManager.getExpensesInMonth(selectedYearMonth);
            showFilteredExpenses(monthExpenses);
        });
        viewMonthExpensesButton.getStyleClass().add("button-view-expenses"); // Apply CSS class

        Label monthLabel = new Label(currentYearMonth.getMonth().name() + " " + currentYearMonth.getYear());
        monthLabel.getStyleClass().add("label-month"); // Apply CSS class
        BorderPane topPane = new BorderPane();
        topPane.setLeft(prevMonthButton);
        topPane.setRight(nextMonthButton);
        topPane.setBottom(viewMonthExpensesButton);
        topPane.setCenter(monthLabel);
        BorderPane.setAlignment(monthLabel, Pos.CENTER);
        BorderPane.setMargin(monthLabel, new Insets(10, 0, 10, 0));

        VBox notesBox = new VBox(10, new Label("Заметки:"), notesTextArea);
        notesBox.setAlignment(Pos.CENTER);

        GridPane calendarGrid = new GridPane();
        calendarGrid.setPadding(new Insets(10));
        calendarGrid.setHgap(10);
        calendarGrid.setVgap(10);
        calendarGrid.setAlignment(Pos.CENTER);

        String[] daysOfWeek = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
        for (int i = 0; i < daysOfWeek.length; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            calendarGrid.add(dayLabel, i, 0);
        }

        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int dayOfWeekValue = firstDayOfMonth.getDayOfWeek().getValue() % 7;

        int daysInMonth = currentYearMonth.lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            int row = (day + dayOfWeekValue - 1) / 7 + 1;
            int col = (day + dayOfWeekValue - 1) % 7;

            LocalDate currentDate = currentYearMonth.atDay(day);
            Button dayButton = new Button(String.valueOf(day));
            dayButton.getStyleClass().add("button-day"); // Apply CSS class

            // Highlight today's date
            if (currentDate.equals(LocalDate.now())) {
                dayButton.getStyleClass().add("button-day-today"); // Apply CSS class
            }

            List<Expense> dailyExpenses = expenseManager.getExpensesByDate(currentDate);
            if (!dailyExpenses.isEmpty()) {
                double totalExpense = dailyExpenses.stream().mapToDouble(Expense::getAmount).sum();
                if (totalExpense > 5000) {
                    dayButton.setStyle("-fx-background-color: red;");
                } else if (totalExpense >= 2000 && totalExpense <= 3000) {
                    dayButton.setStyle("-fx-background-color: yellow;");
                } else {
                    dayButton.setStyle("-fx-background-color: green;");
                }
            }

            int finalDay = day;
            dayButton.setOnAction(event -> {
                LocalDate selectedDate = currentYearMonth.atDay(finalDay);
                if (dateSelectedListener != null) {
                    dateSelectedListener.accept(selectedDate);
                }
                showExpenseDetails(selectedDate);
            });
            calendarGrid.add(dayButton, col, row);
        }

        // Add filter and sort controls
        addFilterAndSortControls();

        VBox mainBox = new VBox();
        mainBox.getChildren().addAll(topPane, calendarGrid, notesBox);
        mainBox.setSpacing(10);
        mainBox.setAlignment(Pos.CENTER);
        view.setCenter(mainBox);
    }

    private void animateMonthTransition(YearMonth newYearMonth) {
        FadeTransition fadeOut = new FadeTransition(javafx.util.Duration.seconds(0.5), view);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            currentYearMonth = newYearMonth;
            createCalendarView();
            FadeTransition fadeIn = new FadeTransition(javafx.util.Duration.seconds(0.5), view);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void showExpenseDetails(LocalDate date) {
        List<Expense> dailyExpenses = expenseManager.getExpensesByDate(date);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Расходы на " + date);
        StringBuilder content = new StringBuilder();
        for (Expense expense : dailyExpenses) {
            content.append(expense.getCategory()).append(": ").append(expense.getAmount()).append("\n");
        }
        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    private void addFilterAndSortControls() {
        BorderPane filterAndSortPane = new BorderPane();
        filterAndSortPane.setPadding(new Insets(10));

        TextField categoryField = new TextField();
        categoryField.setPromptText("Категория");

        TextField minAmountField = new TextField();
        minAmountField.setPromptText("Мин сумма");
        TextField maxAmountField = new TextField();
        maxAmountField.setPromptText("Макс сумма");

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        Button filterButton = new Button("Фильтр");
        filterButton.setOnAction(_ -> {
            String category = categoryField.getText().isEmpty() ? null : categoryField.getText();
            Double minAmount = minAmountField.getText().isEmpty() ? null : Double.parseDouble(minAmountField.getText());
            Double maxAmount = maxAmountField.getText().isEmpty() ? null : Double.parseDouble(maxAmountField.getText());
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            List<Expense> filteredExpenses = expenseManager.filterExpenses(category, minAmount, maxAmount, startDate, endDate);
            showFilteredExpenses(filteredExpenses);
        });

        ComboBox<String> sortOptions = new ComboBox<>();
        sortOptions.getItems().addAll("Сумма (по возрастанию)", "Сумма (по убыванию)", "Дата (по возрастанию)", "Дата (по убыванию)");

        sortOptions.setOnAction(event -> {
            String selectedOption = sortOptions.getSelectionModel().getSelectedItem();
            List<Expense> sortedExpenses;
            List<Expense> currentExpenses = expenseManager.filterExpenses(
                    categoryField.getText().isEmpty() ? null : categoryField.getText(),
                    minAmountField.getText().isEmpty() ? null : Double.parseDouble(minAmountField.getText()),
                    maxAmountField.getText().isEmpty() ? null : Double.parseDouble(maxAmountField.getText()),
                    startDatePicker.getValue(),
                    endDatePicker.getValue());

            sortedExpenses = switch (selectedOption) {
                case "Сумма (по возрастанию)" -> expenseManager.sortExpensesByAmount(currentExpenses, true);
                case "Сумма (по убыванию)" -> expenseManager.sortExpensesByAmount(currentExpenses, false);
                case "Дата (по возрастанию)" -> expenseManager.sortExpensesByDate(currentExpenses, true);
                case "Дата (по убыванию)" -> expenseManager.sortExpensesByDate(currentExpenses, false);
                default -> currentExpenses;
            };
            showFilteredExpenses(sortedExpenses);
        });

        HBox filterControls = new HBox(10, new Label("Категория:"), categoryField,
                new Label("Мин сумма:"), minAmountField,
                new Label("Макс сумма:"), maxAmountField,
                new Label("Дата начала:"), startDatePicker,
                new Label("Дата конца:"), endDatePicker, filterButton);
        filterControls.setAlignment(Pos.CENTER);

        VBox filterAndSortBox = new VBox(10, filterControls, new Label("Сортировка:"), sortOptions);
        filterAndSortBox.setAlignment(Pos.CENTER);

        filterAndSortPane.setTop(filterAndSortBox);
        view.setBottom(filterAndSortPane);
    }

    private void showFilteredExpenses(List<Expense> expenses) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Отфильтрованные расходы");
        StringBuilder content = new StringBuilder();
        double totalAmount = 0;
        for (Expense expense : expenses) {
            content.append(expense.getExpenseDate()).append(" - ")
                    .append(expense.getCategory()).append(": ")
                    .append(expense.getAmount()).append("\n");
            totalAmount += expense.getAmount();
        }
        content.append("Общая сумма: ").append(totalAmount);
        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    private void loadNotesForCurrentMonth() {
        YearMonth key = currentYearMonth;
        String notes = notesByMonth.get(key);

        if (notes != null && !notes.isEmpty()) {
            notesTextArea.setText(notes);
        } else {
            notesTextArea.clear();
        }
    }

    private void saveNotesForCurrentMonth() {
        notesByMonth.put(currentYearMonth, notesTextArea.getText());
        saveNotesToFile();
    }

    private void saveNotesToFile() {
        try {
            NotesCVSParser.writeNotesToCSV(FILE_PATH, notesByMonth);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
