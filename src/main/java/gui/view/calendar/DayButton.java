package gui.view.calendar;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DayButton extends Button {
    public DayButton(String text) {
        super(text);

        Rectangle background = new Rectangle(30, 30);
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.BLACK);
        background.setArcWidth(10);
        background.setArcHeight(10);

        StackPane stack = new StackPane();
        stack.getChildren().addAll(background, new Text(text));
        stack.setAlignment(Pos.CENTER);

        setGraphic(stack);
        setFont(Font.font("Arial", 14));
        setMinSize(30, 30);
    }
}
