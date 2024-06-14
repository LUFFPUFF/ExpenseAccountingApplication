package gui.itemgui;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class ExpenseAnimation {

    public static void playFadeInAnimation(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(1000), node);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    public static void playTranslateAnimation(Node node, double fromX, double toX) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), node);
        tt.setFromX(fromX);
        tt.setToX(toX);
        tt.play();
    }
}
