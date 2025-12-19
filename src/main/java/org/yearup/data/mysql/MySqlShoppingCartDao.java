package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {

    private final ProductDao productDao;

    public MySqlShoppingCartDao(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        // Create a ShoppingCart wrapper object
        ShoppingCart cart = new ShoppingCart();

        // Reuse your existing getItemsByUserId
        Map<Integer, ShoppingCartItem> items = getItemsByUserId(userId);
        cart.setItems(items);

        return cart;
    }

    @Override
    public void addItemToCart (int userId, int productId, int quantity) {
        String sql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding item to cart", e);
        }
    }

    @Override
    public void clearCart(int userId) {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error clearing cart", e);
        }
    }

    @Override
    public void updateItemQuantity (int userId, int productId, int quantity){
        String sql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(3, productId);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating item quantity for userId " + userId
                    + " and productId " + productId, e);
        }
    }

    @Override
    public Map<Integer, ShoppingCartItem> getItemsByUserId(int userId) {
        String sql = "SELECT * FROM shopping_cart WHERE user_id = ?";
        Map<Integer, ShoppingCartItem> cart = new HashMap<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    int quantity = rs.getInt("quantity");
                    Product product = productDao.getById(productId);

                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProduct(product);
                    item.setQuantity(rs.getInt("quantity"));

                    cart.put(item.getProductId(), item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching cart for userId " + userId, e);
        }
        return cart;
    }
}