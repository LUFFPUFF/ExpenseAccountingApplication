package product;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Getter
public class ProductManager {

    private final List<Product> productList = new ArrayList<>();

    public void addProduct(Product product) {
        if (product.getPrice() > 0) {
            productList.add(product);
            log.info("Product added: {}", product);
        } else {
            log.error("Product price must be positive: {}", product);
        }
    }

    public void removeProduct(String nameProduct) {
        boolean removed = productList.removeIf(product -> product.getName().equalsIgnoreCase(nameProduct));
        if (removed) {
            log.info("Product removed: {}", nameProduct);
        } else {
            log.warn("Product not found for removal: {}", nameProduct);
        }
    }

    public Product findProductByName(String nameProduct) {
        return findProductByNameHelper(nameProduct).orElse(null);
    }

    private Optional<Product> findProductByNameHelper(String nameProduct) {
        Optional<Product> product = productList.stream()
                .filter(p -> p.getName().equalsIgnoreCase(nameProduct))
                .findFirst();
        if (product.isPresent()) {
            log.info("Product found: {}", product.get());
        } else {
            log.warn("Product not found: {}", nameProduct);
        }
        return product;
    }

    public void updateProduct(String name, Product updatedProduct) {
        findProductByNameHelper(name).ifPresent(product -> {
            product.setName(updatedProduct.getName());
            product.setPrice(updatedProduct.getPrice());
            product.setCategory(updatedProduct.getCategory());
            log.info("Product updated: {}", product);
        });
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> productsByCategory = productList.stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        log.info("Products found for category {}: {}", category, productsByCategory.size());
        return productsByCategory;
    }

    public int getTotalProductCount() {
        int count = productList.size();
        log.info("Total product count: {}", count);
        return count;
    }

}
