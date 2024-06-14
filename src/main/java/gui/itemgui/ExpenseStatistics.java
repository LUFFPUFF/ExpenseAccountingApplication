package gui.itemgui;

import expense.Expense;
import expense.ExpenseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpenseStatistics {

    private ExpenseManager expenseManager;
    private ObservableList<Expense> expenseList;

    public ExpenseStatistics(ExpenseManager expenseManager) {
        this.expenseManager = expenseManager;
    }

    public ExpenseStatistics(ObservableList<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    public void showTotalExpenses(LocalDate date) {
        List<Expense> expenses = expenseManager.getExpensesByDate(date);
        double totalExpenses = expenses.stream().mapToDouble(Expense::getAmount).sum();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Общие расходы");
        alert.setHeaderText(null);
        alert.setContentText("Общие расходы: " + totalExpenses);
        alert.showAndWait();
    }

    public void showPieChart() {
        Map<String, Double> categoryTotals = expenseList.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        categoryTotals.forEach((category, total) -> pieChartData.add(new PieChart.Data(category, total)));

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Расходы по категориям");

        Stage chartStage = new Stage();
        chartStage.setTitle("Диаграмма расходов по категориям");
        Scene scene = new Scene(new VBox(pieChart), 800, 600);
        chartStage.setScene(scene);
        chartStage.show();
    }
}
