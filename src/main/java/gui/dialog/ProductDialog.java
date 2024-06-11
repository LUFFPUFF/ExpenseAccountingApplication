package gui.dialog;

import database.DatabaseManager;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import product.Product;
import product.ProductManager;

import java.io.IOException;

public class ProductDialog extends Stage {

    public ProductDialog(ProductManager productManager, DatabaseManager databaseManager) {
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Добавить продукт");

        VBox dialogVbox = new VBox(10);
        dialogVbox.setPadding(new Insets(10));

        TextField nameField = new TextField();
        nameField.setPromptText("Название");

        TextField priceField = new TextField();
        priceField.setPromptText("Цена");

        TextField categoryField = new TextField();
        categoryField.setPromptText("Категория");

        Button addButton = new Button("Добавить");
        addButton.setOnAction(event -> {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            String category = categoryField.getText();

            Product product = new Product(name, price, category);
            productManager.addProduct(product);

            try {
                databaseManager.saveDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }

            close();
        });

        dialogVbox.getChildren().addAll(
                new Label("Добавить продукт"),
                nameField,
                priceField,
                categoryField,
                addButton
        );

        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        setScene(dialogScene);
    }
}
