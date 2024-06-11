package gui.view;

import database.DatabaseManager;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import product.Product;
import product.ProductManager;

import java.io.IOException;

@Log4j2
public class ProductView {

    private final ProductManager productManager;
    private final DatabaseManager databaseManager;
    @Getter
    private VBox view;

    public ProductView(ProductManager productManager, DatabaseManager databaseManager) {
        this.productManager = productManager;
        this.databaseManager = databaseManager;
        this.view = new VBox(10);
        this.view.setPadding(new Insets(10));

        createProductView();
    }

    private void createProductView() {
        Label label = new Label("Добавить продукт:");

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
                log.error("error while saving product", e);
                try {
                    throw new IOException(e.getMessage());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            nameField.clear();
            priceField.clear();
            categoryField.clear();
        });

        view.getChildren().addAll(label, nameField, priceField, categoryField, addButton);
    }

}
