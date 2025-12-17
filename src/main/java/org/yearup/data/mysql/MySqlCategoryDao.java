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

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement query = conn.prepareStatement("""
                        SELECT *
                        FROM categories
                        """);
                ResultSet results = query.executeQuery()
        ){
            while(results.next()){
                Category category = new Category();
                category.setCategoryId(results.getInt("Category_id"));
                category.setName(results.getString("Name"));
                category.setDescription(results.getString("Description"));

                categories.add(category);
            }
        }catch(SQLException e){
            System.out.println("Error getting all categories" + e);
        }
        return categories;
    }

    @Override
    public int getById(int catID)
    {
        // ✅get category by id
        Category category = new Category();

        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement query = conn.prepareStatement("""
                SELECT
                    category_id, name, description
                FROM
                    categories
                WHERE
                    category_id = ?
                """)
        ){

        query.setInt(1, catID);

        ResultSet results = query.executeQuery();

            if(results.next()){
            category.setCategoryId(results.getInt("CategoryID"));
            category.setName(results.getString("Name"));
            category.setDescription(results.getString("Description"));
            }else{
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
        }catch(SQLException e){
            System.out.println(category + " :NOT FOUND");
        }
        return catID;
    }
    @Override
    public Category create(Category category)
    {
        // ✅create a new category
        try(
                Connection conn = dataSource.getConnection();
                PreparedStatement query = conn.prepareStatement("""
                INSERT INTO Categories(Name, Description) VALUES(?,?)
                """)){
            query.setString(1, category.getName());
            query.setString(2, category.getDescription());

            query.executeUpdate();
        }catch (SQLException e){
            System.out.println("Error adding category");
        }
        return category;
    }

    @Override
    public void update(int categoryId, Category category)
    {
        // ✅update category
        try(
                Connection conn = dataSource.getConnection();
                // 'coalesce' returns first non-null value, can take more than one argument
                PreparedStatement query = conn.prepareStatement("""
                  UPDATE
                        categories
                  SET
                        Category_ID = COALESCE(?, Category_ID),
                        Name = COALESCE(?, Name),
                        Description = COALESCE(?, Description)
                   WHERE
                        Category_ID = ?
                """)){
            if(category.getCategoryId() == null || category.getCategoryId() == 0){
                query.setNull(1, Types.INTEGER);
            }else{
                query.setInt(1, category.getCategoryId());
            }
            query.setString(2, category.getName());
            query.setString(3, category.getDescription());

            query.setInt(4, categoryId);

            query.executeUpdate();
        }catch(SQLException e){
            System.out.println("Error updating category" + e);
        }
    }

    @Override
    public void delete(int categoryId)
    {
        //✅ delete category
        try(Connection conn = dataSource.getConnection();
            PreparedStatement query = conn.prepareStatement("""
                    DELETE
                        categories
                    WHERE
                        category_ID = ?
                    """)){
            query.setInt(1, categoryId);

            query.executeUpdate();
        }catch(SQLException e){
            System.out.println("Error deleting category: " + e);
        }
    }

    //mapRow: iterates over each result, calls maRow for each and returns objects as a list
    private Category mapRow(ResultSet row) throws SQLException
    {
        int categoryID = row.getInt("categoryID");
        String name = row.getString("Name");
        String description = row.getString("description");

        Category category = new Category()
        {{
            setCategoryId(categoryID);
            setName(name);
            setDescription(description);
        }};
        return category;
    }
}