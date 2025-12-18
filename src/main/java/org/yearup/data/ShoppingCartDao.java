package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.util.List;
import java.util.Map;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    // add additional method signatures here

    //shoppingcart class stores map item, so shoppingcartdao has to return the same
    Map<Integer,ShoppingCartItem> getItemsByUserId(int userId);

    void updateItemQuantity(int userId, int productId, int quantity);
}
