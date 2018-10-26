package model;

import controllers.Application;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Recipe {

    private int id;
    private String name;
    private ArrayList<Ware> ingredients = new ArrayList();
    private ArrayList<String> actions = new ArrayList();
    private String instruction;

    /**
     * Constructor
     */
    public Recipe(int id, String dishname) {
        this.id = id;
        this.name = dishname;
        cookingInstructions();
        this.instruction = toString();
    }

    /**
     * Fills ingredients and action list from database
     */
    public void cookingInstructions() {
        try {
            Connection con = Application.db.getConnection();
            String query = "SELECT * FROM Ware_has_Recipe WHERE Recipe_idRecipe =" + id;
            Statement stmt = con.createStatement();
            ResultSet result = stmt.executeQuery(query);
            String sqlAnzahl = "SELECT COUNT(*) FROM Ware_has_Recipe WHERE Recipe_idRecipe =" + id;
            int anzahl = 0;
            Statement stmtAnzahl = con.createStatement();
            ResultSet resultAnzahl = stmtAnzahl.executeQuery(sqlAnzahl);
            while (resultAnzahl.next()) {
                anzahl = resultAnzahl.getInt(1);
            }
            for (int i = 1; i <= anzahl; i++) {
                result.next();
                String sql = "SELECT * FROM Ware_has_Recipe WHERE Recipe_idRecipe = " + id + " AND  positionOfWare =" + i;
                Statement stmtSql = con.createStatement();
                ResultSet resultSql = stmtSql.executeQuery(sql);
                if (resultSql.next()) {
                    int idWare = resultSql.getInt("Ware_idWare");
                    String action = resultSql.getString("action");
                    actions.add(i - 1, action);
                    String ware = "SELECT * FROM Ware WHERE idWare = " + idWare;
                    Statement stmtWare = con.createStatement();
                    ResultSet resultWare = stmtWare.executeQuery(ware);
                    resultWare.next();
                    String wareName = resultWare.getString("wareName");
                    int preis = resultWare.getInt("price");
                    Ware w = new Ware(idWare, wareName, preis);
                    ingredients.add(w);
                }
            }
            con.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @java.lang.Override
    /**
     * Returns the cooking instructions for the cookbook,
     * containing the ingredients and actions itself
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ingredients.size(); i++) {
            sb.append("- " + ingredients.get(i).getWarename() + " " + actions.get(i) + "<br>");
        }
        String finalString = sb.toString();
        return finalString;
    }

    //------------------- GETTER AND SETTER -----------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Ware> getIngredients() {
        return ingredients;
    }

    public ArrayList<String> getActions() {
        return actions;
    }

    public String getInstruction() {
        return instruction;
    }

}