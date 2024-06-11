package database.manager;

import database.util.CSVUtils;
import product.Product;
import product.ProductManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductCSVManager {

    private static final String FILE_PATH = "files/products.csv";
    private final ProductManager productManager;

    public ProductCSVManager(ProductManager productManager) {
        this.productManager = productManager;
    }

    public void loadProducts() throws IOException {
        List<String[]> rows = CSVUtils.readCSV(FILE_PATH);
        List<Product> products = new ArrayList<>();
        for (String[] row : rows) {
            String name = row[0];
            double price = Double.parseDouble(row[1]);
            String category = row[2];
            Product product = new Product(name, price, category);
            products.add(product);
        }
        productManager.getProductList().addAll(products);
    }

    public void saveProducts() throws IOException {
        List<String[]> data = new ArrayList<>();
        for (Product product : productManager.getProductList()) {
            String[] row = new String[] {
                    product.getName(),
                    String.valueOf(product.getPrice()),
                    product.getCategory()
            };
            data.add(row);
        }
        CSVUtils.writeCSV(FILE_PATH, data);
    }
}
