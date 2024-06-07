package expense;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@Getter
@Setter
public class Expense {
    private String description;
    private double amount;
    private LocalDate expenseDate;
    private String nameShop;
    private String category;

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return String.format(
                "Expense:\nDescription: %s\nAmount: %.2f\nDate: %s\nCategory: %s\nName shop: %s\n",
                description, amount, expenseDate.format(formatter), category, nameShop
        );
    }
}
