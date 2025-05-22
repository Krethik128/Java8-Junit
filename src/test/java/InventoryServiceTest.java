import com.gevernova.inventorymanagemntsystem.*;

import com.gevernova.inventorymanagemntsystem.exceptions.InvalidProductException;
import com.gevernova.inventorymanagemntsystem.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    private InventoryService inventoryService;
    private InventoryRepository inventoryRepository;

    @BeforeEach
    void setUp() {
        // Initialize a new repository and service for each test
        inventoryRepository = new InMemoryInventoryRepository();
        inventoryService = new InventoryService(inventoryRepository);
    }

    @Test
    @DisplayName("Should add a valid new product using generated UUID")
    void shouldAddNewValidProduct() throws InvalidProductException {
        // Product constructor no longer takes an ID
        Product product = new Product("Laptop", ProductCategory.Electronics, 10, 1200.00);
        inventoryService.addNewProduct(product); // Use addNewProduct

        assertEquals(1, inventoryService.getAllProducts().size());
        // Verify by name as ID is generated
        Optional<Product> addedProduct = inventoryService.searchProductsByName("Laptop").stream().findFirst();
        assertTrue(addedProduct.isPresent());
        assertEquals(10, addedProduct.get().getQuantity());
    }

    @Test
    @DisplayName("Should increase quantity of an existing product using its ID")
    void shouldIncreaseQuantityOfExistingProduct() throws InvalidProductException, ProductNotFoundException {
        // Add the initial product
        Product product1 = new Product("Laptop", ProductCategory.Electronics, 10, 1200.00);
        inventoryService.addNewProduct(product1);

        // Now, increase its quantity using its generated ID
        inventoryService.increaseProductQuantity(product1.getId(), 5);

        assertEquals(1, inventoryService.getAllProducts().size());
        Optional<Product> updatedProduct = inventoryService.searchProductsByName("Laptop").stream().findFirst();
        assertTrue(updatedProduct.isPresent());
        assertEquals(15, updatedProduct.get().getQuantity()); // 10 + 5 = 15
    }

    @Test
    @DisplayName("Should throw InvalidProductException for product with negative price in constructor")
    void shouldThrowInvalidProductExceptionForNegativePrice() {
        assertThrows(InvalidProductException.class, () ->
                new Product("Bad Product", ProductCategory.Others, 5, -10.00));
    }

    @Test
    @DisplayName("Should throw InvalidProductException for product with negative initial quantity in constructor")
    void shouldThrowInvalidProductExceptionForNegativeInitialQuantity() {
        assertThrows(InvalidProductException.class, () ->
                new Product("Zero Stock", ProductCategory.Others, -1, 50.00));
    }

    @Test
    @DisplayName("Should throw InvalidProductException when decreasing quantity to negative")
    void shouldThrowInvalidProductExceptionWhenDecreasingToNegative() throws InvalidProductException, ProductNotFoundException {
        Product product = new Product("Laptop", ProductCategory.Electronics, 5, 1200.00);
        inventoryService.addNewProduct(product);

        assertThrows(InvalidProductException.class, () ->
                inventoryService.decreaseProductQuantity(product.getId(), 10)); // Try to decrease by more than available
    }

    @Test
    @DisplayName("Should decrease product quantity successfully")
    void shouldDecreaseProductQuantitySuccessfully() throws InvalidProductException, ProductNotFoundException {
        Product product = new Product("Laptop", ProductCategory.Electronics, 10, 1200.00);
        inventoryService.addNewProduct(product);
        inventoryService.decreaseProductQuantity(product.getId(), 3);

        Optional<Product> updatedProduct = inventoryService.searchProductsByName("Laptop").stream().findFirst();
        assertTrue(updatedProduct.isPresent());
        assertEquals(7, updatedProduct.get().getQuantity());
    }

    @Test
    @DisplayName("Should remove product successfully")
    void shouldRemoveProductSuccessfully() throws InvalidProductException, ProductNotFoundException {
        Product product = new Product("Laptop", ProductCategory.Electronics, 10, 1200.00);
        inventoryService.addNewProduct(product);
        inventoryService.removeProduct(product.getId()); // Use the actual ID

        assertTrue(inventoryService.getAllProducts().isEmpty());
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when removing non-existent product")
    void shouldThrowProductNotFoundExceptionWhenRemovingNonExistentProduct() {
        // Use a dummy UUID as no product with that ID exists
        assertThrows(ProductNotFoundException.class, () ->
                inventoryService.removeProduct("some-non-existent-uuid"));
    }

    @Test
    @DisplayName("Should filter low stock items correctly")
    void shouldFilterLowStockItemsCorrectly() throws InvalidProductException {
        Product productOne = new Product("Laptop", ProductCategory.Electronics, 3, 1200.00);
        Product productTwo = new Product("Bread", ProductCategory.Grocery, 8, 2.50);
        Product productThree = new Product("Shirt", ProductCategory.Clothing, 2, 25.00);

        inventoryService.addNewProduct(productOne);
        inventoryService.addNewProduct(productTwo);
        inventoryService.addNewProduct(productThree);

        List<Product> lowStock = inventoryService.filterLowStockItems(5);
        assertEquals(2, lowStock.size());
        // Verify by actual product objects or their generated IDs
        assertTrue(lowStock.stream().anyMatch(p -> p.getId().equals(productOne.getId())));
        assertTrue(lowStock.stream().anyMatch(p -> p.getId().equals(productThree.getId())));
    }

    @Test
    @DisplayName("Should sort products by category and price")
    void shouldSortProductsByCategoryAndPrice() throws InvalidProductException {
        Product productOne = new Product("Novel", ProductCategory.Books, 5, 15.00);
        Product productTwo = new Product("Milk", ProductCategory.Grocery, 10, 3.00);
        Product productThree = new Product("TV", ProductCategory.Electronics, 3, 800.00);
        Product productFour = new Product("Butter", ProductCategory.Grocery, 7, 4.50);
        Product productFive = new Product("Fantasy", ProductCategory.Books, 2, 12.00);

        inventoryService.addNewProduct(productOne);
        inventoryService.addNewProduct(productTwo);
        inventoryService.addNewProduct(productThree);
        inventoryService.addNewProduct(productFour);
        inventoryService.addNewProduct(productFive);

        List<Product> sortedProducts = inventoryService.sortProductsByCategoryAndPrice();

        // Expected order based on ProductCategory enum's natural order (Electronics, Grocery, Books)
        assertEquals(productThree.getId(), sortedProducts.get(0).getId()); // TV (ELECTRONICS, 800.00)
        assertEquals(productTwo.getId(), sortedProducts.get(1).getId()); // Milk (GROCERY, 3.00)
        assertEquals(productFour.getId(), sortedProducts.get(2).getId()); // Butter (GROCERY, 4.50)
        assertEquals(productFive.getId(), sortedProducts.get(3).getId()); // Fantasy (BOOKS, 12.00)
        assertEquals(productOne.getId(), sortedProducts.get(4).getId()); // Novel (BOOKS, 15.00)
    }

    @Test
    @DisplayName("Should search products by name correctly")
    void shouldSearchProductsByNameCorrectly() throws InvalidProductException {
        Product productOne = new Product("Laptop Pro", ProductCategory.Electronics, 5, 1500.00);
        Product productTwo = new Product("Gaming Laptop", ProductCategory.Electronics, 3, 1800.00);
        Product productThree = new Product("Mouse", ProductCategory.Electronics, 15, 20.00);

        inventoryService.addNewProduct(productOne);
        inventoryService.addNewProduct(productTwo);
        inventoryService.addNewProduct(productThree);

        List<Product> laptops = inventoryService.searchProductsByName("laptop");
        assertEquals(2, laptops.size());
        assertTrue(laptops.stream().anyMatch(p -> p.getId().equals(productOne.getId())));
        assertTrue(laptops.stream().anyMatch(p -> p.getId().equals(productTwo.getId())));
    }

    @Test
    @DisplayName("Should search products by category correctly")
    void shouldSearchProductsByCategoryCorrectly() throws InvalidProductException {
        Product productOne = new Product("Laptop", ProductCategory.Electronics, 5, 1200.00);
        Product productTwo = new Product("Bread", ProductCategory.Grocery, 8, 2.50);
        Product productThree = new Product("Shirt", ProductCategory.Clothing, 2, 25.00);

        inventoryService.addNewProduct(productOne);
        inventoryService.addNewProduct(productTwo);
        inventoryService.addNewProduct(productThree);

        List<Product> electronics = inventoryService.searchProductsByCategory(ProductCategory.Electronics);
        assertEquals(1, electronics.size());
        assertEquals(productOne.getId(), electronics.get(0).getId());
    }

    // Additional test for InvalidProductException from Product constructor
    @Test
    @DisplayName("Should throw InvalidProductException for null product name")
    void shouldThrowInvalidProductExceptionForNullName() {
        assertThrows(InvalidProductException.class, () ->
                new Product(null, ProductCategory.Others, 1, 10.0));
    }

    @Test
    @DisplayName("Should throw InvalidProductException for blank product name")
    void shouldThrowInvalidProductExceptionForBlankName() {
        assertThrows(InvalidProductException.class, () ->
                new Product("   ", ProductCategory.Others, 1, 10.0));
    }

    @Test
    @DisplayName("Should throw InvalidProductException for null product category")
    void shouldThrowInvalidProductExceptionForNullCategory() {
        assertThrows(InvalidProductException.class, () ->
                new Product("Test Item", null, 1, 10.0));
    }
}