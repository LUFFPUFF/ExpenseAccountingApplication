package database;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import product.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Log4j2
public class ProductDatabaseManager {

    private DatabaseManager databaseManager;

    public void addProduct(Product product) {
        String query = "INSERT INTO products (name, price, category) VALUES (?, ?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setString(3, product.getCategory());
            statement.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to add product: {}", e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Product product = new Product(
                        resultSet.getString("name"),
                        resultSet.getDouble("price"),
                        resultSet.getString("category")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            log.error("Failed to get all products: {}", e.getMessage());
        }
        return products;
    }
}
