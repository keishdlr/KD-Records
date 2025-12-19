package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;
import java.util.Map;

// ✅convert this class to a REST controller
// only logged-in users should have access to these actions
@RestController
@RequestMapping("/cart")
public class ShoppingCartController
{
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao,
                                  UserDao userDao,
                                  ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    // each method in this controller requires a Principal object as a parameter
    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            // get the currently logged-in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            //✅ use the shoppingcartDao to get all items in the cart and return the cart
            //get user cart
            ShoppingCart cart = shoppingCartDao.getByUserId(userId);
            //get all items in the user cart
            Map<Integer, ShoppingCartItem> items = shoppingCartDao.getItemsByUserId(userId);
            // attach items to cart object
            cart.setItems(items);
            return cart;
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added
    @PostMapping("/cart/products/{productId}")
    public ShoppingCart addToCart(@PathVariable int productId, @RequestBody ShoppingCartItem newItem,Principal principal){

        String username = principal.getName();

        User user = userDao.getByUserName(username);
        int userId = user.getId();

        ShoppingCart cart = shoppingCartDao.getByUserId(userId);
        cart.add(newItem);
        shoppingCartDao.addItemToCart(userId, productId, newItem.getQuantity());

        return cart;
    }


    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated
    @PutMapping("/cart/products/{productId}")
    public ShoppingCart updateProductInCart(@PathVariable int productId,
                                            @RequestBody ShoppingCartItem updatedItem, Principal principal)
    {
        // 1. Get the logged-in username
        String username = principal.getName();

        // 2. Look up the user
        User user = userDao.getByUserName(username);
        int userId = user.getId();

        // 3. Get the user's cart
        ShoppingCart cart = shoppingCartDao.getByUserId(userId);

        // 4. Get the existing item from the cart
        ShoppingCartItem existingItem = cart.get(productId);

        // 5. Update ONLY the quantity
        existingItem.setQuantity(updatedItem.getQuantity());

        // 6. Save the updated item in the database
        shoppingCartDao.updateItemQuantity(userId, productId, existingItem.getQuantity());

        // 7. Return the updated cart
        return cart;
    }

    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart
    @DeleteMapping("/cart")
    public ShoppingCart clearCart (Principal principal){

        try {
            String username = principal.getName();

            User user = userDao.getByUserName(username);
            int userId = user.getId();

            //clear all items in cart
            shoppingCartDao.clearCart(userId);

            //return empty car project
            return new ShoppingCart();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}