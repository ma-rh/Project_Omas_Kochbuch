package model;

import controllers.Application;

import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import java.util.ArrayList;

public class User {

    private int id = 0;
    private String username;
    private String password;
    private String status;
    private int cookies;
    private int currentRecipe;
    private ArrayList<Ware> wareListOwned = new ArrayList();
    private ArrayList<Recipe> recipeListOwned = new ArrayList();
    private ArrayList<Ware> wareListToBuy = new ArrayList<>();
    private ArrayList<Recipe> recipeListAll = new ArrayList();

    /**
     * Constructor
     *
     * @param username
     * @param password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = "Tellerwäscher";
        this.cookies = 0;
        this.id = getNumberUser() + 1;
        create(id, username, password, status, cookies);
    }

    /**
     * Constructor
     *
     * @param id
     * @param username
     * @param password
     * @param status
     * @param cookies
     */
    public User(int id, String username, String password, String status, int cookies) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;
        this.cookies = cookies;
    }

    /**
     * Adds new user to database after registration
     *
     * @param userid
     * @param username
     * @param password
     * @param status
     * @param cookies
     * @return
     */
    public static boolean create(int userid, String username, String password, String status, int cookies) {
        try {
            Connection con = controllers.Application.db.getConnection();
            PreparedStatement stmt = con.prepareStatement(
                    "INSERT INTO User (`idUser`,`name`, `password`, `status`, `cookies`) VALUES (?,?,?,?,?);",
                    Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, userid);
            stmt.setString(2, username);
            stmt.setString(3, password);
            stmt.setString(4, status);
            stmt.setInt(5, cookies);
            stmt.executeUpdate();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * After registration user gets 1 recipe and 3 wares
     */
    public void putDataInDatabase() {
        try {
            Connection con = Application.db.getConnection();
            String sql = "INSERT INTO User_has_Ware VALUES (?,?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setInt(2, 1);
            stmt.executeUpdate();
            PreparedStatement stmt2 = con.prepareStatement(sql);
            stmt2.setInt(1, id);
            stmt2.setInt(2, 2);
            stmt2.executeUpdate();

            PreparedStatement stmt3 = con.prepareStatement(sql);
            stmt3.setInt(1, id);
            stmt3.setInt(2, 3);
            stmt3.executeUpdate();

            String sqlRezept = "INSERT INTO User_has_Recipe VALUES (?,?)";
            PreparedStatement stmtRezept = con.prepareStatement(sqlRezept);
            stmtRezept.setInt(1, id);
            stmtRezept.setInt(2, 1);
            stmtRezept.executeUpdate();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Getter for property 'numberUser'.
     *
     * @return Value for property 'numberUser'.
     */
    public int getNumberUser() {
        try {
            Connection con = controllers.Application.db.getConnection();
            PreparedStatement statement = null;
            ResultSet rs = null;
            String query = "SELECT COUNT(*) FROM User;";
            int numberOfRows = 0;

            statement = con.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                numberOfRows = rs.getInt(1);
            }
            con.close();
            return numberOfRows;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Gets all ware from database and fills lists wareListOwned and wareListToBuy
     * If user owns ware, it gets added to wareListOwned
     * If user does not own ware, it gets added to wareListToBuy
     */
    public void getWareFromDatabase() {
        try {
            Connection con = controllers.Application.db.getConnection();
            String query = "SELECT * FROM User_has_Ware WHERE User_idUser =" + id;
            Statement stmt = con.createStatement();
            ResultSet result = stmt.executeQuery(query);
            while (result.next()) {
                int wareId = result.getInt("Ware_idWare");
                String sqlWare = "SELECT * FROM Ware Where idWare = " + wareId;
                Statement stmtWare = con.createStatement();
                ResultSet resultWare = stmtWare.executeQuery(sqlWare);
                resultWare.next();
                String wareName = resultWare.getString("wareName");
                int price = resultWare.getInt("price");
                Ware ware = new Ware(wareId, wareName, price);
                wareListOwned.add(ware);
            }
            String queryWare = "SELECT * FROM Ware";
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(queryWare);
            while (resultSet.next()) {
                int wareId = resultSet.getInt("idWare");
                String wareName = resultSet.getString("wareName");
                int price = resultSet.getInt("price");
                Ware ware = new Ware(wareId, wareName, price);
                if (!wareListOwned.contains(ware)) {
                    wareListToBuy.add(ware);
                }
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all recipes from database and fills lists recipeListOwned and recipeListAll
     * If user owns recipe, it gets added to recipeListOwned
     * All recipes get added to recipeListAll
     */
    public void getRecipeFromDatabase() {
        try {
            Connection con = controllers.Application.db.getConnection();
            String sqlRecipe = "SELECT * FROM User_has_Recipe WHERE User_idUser =" + id;
            Statement stmtRecipe = con.createStatement();
            ResultSet resultRecipe = stmtRecipe.executeQuery(sqlRecipe);
            while (resultRecipe.next()) {
                int recipeId = resultRecipe.getInt("Recipe_idRecipe");
                String sql = "SELECT * FROM Recipe WHERE idRecipe = " + recipeId;
                Statement stmt = con.createStatement();
                ResultSet result = stmt.executeQuery(sql);
                result.next();
                String name = result.getString("recipeName");
                Recipe recipe = new Recipe(recipeId, name);
                recipeListOwned.add(recipe);
            }
            String queryRecipe = "SELECT * FROM Recipe";
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(queryRecipe);
            while (resultSet.next()) {
                int recipeId = resultSet.getInt("idRecipe");
                String recipeName = resultSet.getString("recipeName");
                Recipe recipe = new Recipe(recipeId, recipeName);
                recipeListAll.add(recipe);
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Change number of cookies user owns
     * Updates number of cookies in database
     *
     * @param cookiesValue
     * @param userGetCookies true if user gets Cookies, false if user looses cookies
     */
    public void changeCookies(int cookiesValue, boolean userGetCookies) {
        int userId = getId();
        try {

            if (userGetCookies) {
                setCookies(cookies + cookiesValue);
            } else {
                setCookies(cookies - cookiesValue);
            }
            Connection con = Application.db.getConnection();
            String sql = "UPDATE User SET cookies = ? WHERE idUser = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, getCookies());
            stmt.setInt(2, getId());
            stmt.executeUpdate();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Increase Status in addition of numbers of owned recipeListAll
     * Update status in database
     */
    private void increaseStatus() {
        int numbersOfRecipe = recipeListOwned.size();
        try {
            Connection con = Application.db.getConnection();
            String sql = "";
            PreparedStatement stmt = null;
            if (numbersOfRecipe == 1) {
                setStatus("Tellerwäscher");
            } else if (numbersOfRecipe == 2 || numbersOfRecipe == 3) {
                setStatus("Küchenhilfe");
            } else if (numbersOfRecipe == 4 || numbersOfRecipe == 5) {
                setStatus("Lehrling");
            } else if (numbersOfRecipe >= 6 && numbersOfRecipe < 9) {
                setStatus("Koch");
            } else {
                setStatus("Kochprofi");
            }
            sql = "UPDATE User SET status = ? WHERE idUser = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Buy Ware From Supermarket: Add to List of ownedWareList and remove from wareListToBuy
     * Update Data in Database
     * Cookies get updated
     *
     * @param wareToBuy
     */
    public boolean buyWare(String wareToBuy) {

        Ware wareNew = null;
        for (Ware w : getWareListToBuy()) {
            if (w.getWarename().equals(wareToBuy)) {
                wareNew = w;
            }
        }
        if (cookies < wareNew.getPrice()) {
            return false;
        }
        wareListToBuy.remove(wareNew);
        wareListOwned.add(wareNew);

        try {
            Connection con = Application.db.getConnection();
            String sql = "INSERT INTO User_has_Ware VALUES (?,?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setInt(2, wareNew.getId());
            stmt.executeUpdate();
            stmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Cookies get reduced
        changeCookies(wareNew.getPrice(), false);
        return true;
    }

    /**
     * User buys new Recipe for 2 Cookies;
     * Recipe gets added to recipeListOwned
     */
    public void buyNewRecipe() {

        int numberOwnedRecipes = recipeListOwned.size();
        Recipe recipeNew = recipeListAll.get(numberOwnedRecipes);
        recipeListOwned.add(recipeNew);

        try {
            Connection con = Application.db.getConnection();
            String sql = "INSERT INTO User_has_Recipe VALUES (?,?)";
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setInt(1, id);
            stmt.setInt(2, recipeNew.getId());
            stmt.executeUpdate();
            stmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        changeCookies(3, false);
        increaseStatus();
    }

    /**
     * Checks if user owns ware, to display in kitchen
     * User owns ware if ware is in wareListOwned
     *
     * @return false if user does not own ware
     */
    public boolean userOwnsWare(String warename) {
        for (Ware w : wareListOwned) {
            if (w.getWarename().equals(warename)) {
                return true;
            }
        }
        return false;
    }

    //-------------GETTER AND SETTER-----------------

    public String getStatus() {
        return status;
    }

    private void setStatus(String status) {
        this.status = status;
    }

    public int getCookies() {
        return cookies;
    }

    public void setCookies(int cookies) {
        this.cookies = cookies;
    }

    public List<Recipe> getRecipeListOwned() {
        return recipeListOwned;
    }

    public void setCurrentRecipe(int currentRecipe) {
        this.currentRecipe = currentRecipe;
    }

    public int getCurrentRecipe() {
        return currentRecipe;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Ware> getWareListToBuy() {
        return wareListToBuy;
    }

    public ArrayList<Recipe> getRecipeListAll() {
        return recipeListAll;
    }
}