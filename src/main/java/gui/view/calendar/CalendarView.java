package gui.view.calendar;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.function.Consumer;

public class CalendarView {

    @Getter
    private BorderPane view;
    private YearMonth currentYearMonth;
    @Setter
    private Consumer<LocalDate> dateSelectedListener;

    public CalendarView() {
        this.view = new BorderPane();
        this.currentYearMonth = YearMonth.now();

        createCalendarView();
    }

    private void createCalendarView() {
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
        int dayOfWeekValue = firstDayOfMonth.getDayOfWeek().getValue();

        int daysInMonth = currentYearMonth.lengthOfMonth();

        for (int day = 1; day <= daysInMonth; day++) {
            int row = (day + dayOfWeekValue - 2) / 7 + 1;
            int col = (day + dayOfWeekValue - 2) % 7;

            Button dayButton = new Button(String.valueOf(day));
            int finalDay = day;
            dayButton.setOnAction(event -> {
                LocalDate selectedDate = currentYearMonth.atDay(finalDay);
                if (dateSelectedListener != null) {
                    dateSelectedListener.accept(selectedDate);
                }
            });
            calendarGrid.add(dayButton, col, row);
        }

        view.setCenter(calendarGrid);
    }

}
