package org.yearup.data.mysql;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao
{
    public MySqlCategoryDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public List<Category> getAllCategories()
    {
        // ✅get all categories
        // Create an empty list to hold the objects we will retrieve.
        List<Category> categories = new ArrayList<>();

        // This is the SQL SELECT statement we will run.
        String sql = "SELECT * FROM categories";

        // This is a "try-with-resources" block.
        // It ensures that the Connection, Statement, and ResultSet are closed automatically after we are done.
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Loop through each row in the ResultSet.
            while (rs.next()) {
                // Create a new object.
                Category category = new Category();

                category.setCategoryId(rs.getInt("categoryID"));

                category.setName(rs.getString("categoryName"));

                category.setDescription(rs.getString("Description"));

                categories.add(category);
            }

        } catch (SQLException e) {
            // If something goes wrong (SQL error), print the stack trace to help debug.
            e.printStackTrace();
        }

        // Return the list of objects.
        return categories;
    }

    @Override
    public int getById(int categoryId)
    {
            // get category by id
            Category catID = new Category();

        try(Connection c = ds.getConnection();
        PreparedStatement q = c.prepareStatement("""
                SELECT
                    category_id, name, description
                FROM 
                    categories
                WHERE 
                    category_id = ?
                """)
        ){

        q.setInt(1,categoryId);

        ResultSet r = q.executeQuery();

        if(r.next()){
            catID.setCategoryId(r.getInt("Category_id"));
            catID.setName(r.getString("Name"));
            catID.setDescription(r.getString("Description"));
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }catch(SQLException e){
        System.out.println("Error getting category with id: " + categoryId);
    }
        return catID;
    }
    @Override
    public Category create(Category category)
    {
        // ✅create a new category
        String sql = "INSERT INTO categories (categoryName, description) VALUES (?, ?)";

        // This is a "try-with-resources" block.
        // It ensures that the Connection and PreparedStatement are closed automatically after we are done.
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Set the first parameter (?)
            stmt.setString(1, category.getName());

            // Set the second parameter (?)
            stmt.setString(2, category.getDescription());

            // Execute the INSERT statement — this will add the row to the database.
            stmt.executeUpdate();

            // Retrieve the generated category_id
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    category.setCategoryId(newId); // Set the generated ID on the category object
                }
            }

        } catch (SQLException e) {
            // If something goes wrong (SQL error), print the stack trace to help debug.
            e.printStackTrace();
        }

        return category;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // update category

    }

    @Override
    public void delete(int categoryId)
    {
        // delete category

    }

    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category(categoryId)
        {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }
}
