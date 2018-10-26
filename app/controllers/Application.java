package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import model.*;
import play.api.db.Database;
import play.libs.Json;
import play.mvc.*;
import views.html.*;

import javax.inject.Inject;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class Application extends Controller {


    private HashMap<String, User> loggedInUsers = new HashMap<>();
    public static Database db;
    private Quizquestion quizquestion;

    @Inject
    public Application(Database db) {
        Application.db = db;
    }

    //--------- GETTER AND SETTER ---------------------
    private User getCurrentUser() {
        String username = session().get("user");
        return loggedInUsers.get(username);
    }

    /**
     *
     * An action that renders an HTML page
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return redirect(routes.Application.login());
    }

    public Result login() {
        return ok(login.render());
    }

    public Result kueche(String recipe) {
        List<Recipe> recipes = getCurrentUser().getRecipeListOwned();
        int recipeId = 0;
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getName().equals(recipe)) {
                recipeId = i;
            }
        }
        getCurrentUser().setCurrentRecipe(recipeId);
        return ok(main.render("Küche", kueche.render(recipe)));
    }

    public Result kochbuch() {
        return ok(main.render("Kochbuch", kochbuch.render()));
    }

    public Result supermarkt() {
        return ok(main.render("Supermarkt", supermarkt.render()));
    }

    public Result quizseite() {
        return ok(main.render("Quiz", quizseite.render()));
    }

    public Result memory() {
        return ok(main.render("Memory", memory.render()));
    }

    public Result gameinstructions() {
        return ok(gameinstructions.render());
    }

    // ------------------------ MAIN ------------------------

    /**
     * Called when User logs out
     * Session is cleared, Login page is rendered
     */
    public Result logout() {
        session().clear();
        return redirect(routes.Application.index());
    }

    /**
     * @return Number of cookies user owns
     */
    public Result cookiesLoad() {
        int cookies = getCurrentUser().getCookies();
        ObjectNode result = Json.newObject();
        result.put("points", cookies);
        return ok(result);
    }

    /**
     * @return User status
     */
    public Result statusLoad() {
        String status = getCurrentUser().getStatus();
        ObjectNode result = Json.newObject();
        result.put("userStatus", status);
        return ok(result);
    }

    //----------------------- LOGIN ---------------------------

    /**
     * Called when user registers
     * Creates new session und new user object
     * puts user in hashMap loggedInUsers
     */
    public Result register() {
        JsonNode json = request().body().asJson();

        if (json == null) {
            return badRequest();
        } else {

            String username = json.findPath("username").textValue();
            String password = json.findPath("password").textValue();

            boolean inDatabase = false;

            try {
                Connection con = controllers.Application.db.getConnection();
                Statement statement = con.createStatement();
                String sql;
                sql = "SELECT * FROM User WHERE name ='" + username + "'";

                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    inDatabase = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            session("user", username);
            User user = new User(username, password);
            loggedInUsers.put(username, user);

            getCurrentUser().putDataInDatabase();
            getCurrentUser().getWareFromDatabase();
            getCurrentUser().getRecipeFromDatabase();
            ObjectNode result = Json.newObject();
            result.put("inDatabase", inDatabase);
            return ok(result);
        }
    }


    /**
     * Called when user logs in
     * Checks input for username and password in database.
     * If correct, new session is created and
     * User object is put in hashMap loggedInUsers
     */
    public Result createSessionFor() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            return badRequest();
        } else {
            String username = json.findPath("username").textValue();
            String password = json.findPath("password").textValue();
            ObjectNode result = Json.newObject();
            String wrong = "";
            try {
                Connection con = controllers.Application.db.getConnection();
                Statement statement = con.createStatement();
                String sql;
                sql = "SELECT * FROM User WHERE name ='" + username + "'";
                ResultSet resultSet = statement.executeQuery(sql);
                if (resultSet.next()) {
                    String pw, status = "";
                    int cookies = 0;
                    int idUser = 0;

                    pw = resultSet.getString("password");
                    status = resultSet.getString("status");
                    cookies = resultSet.getInt("cookies");
                    idUser = resultSet.getInt("idUser");
                    con.close();
                    if (pw.equals(password)) {
                        session("user", username);
                        User user = new User(idUser, username, password, status, cookies);
                        loggedInUsers.put(username, user);
                        user.getWareFromDatabase();
                        user.getRecipeFromDatabase();
                        wrong = "nothing";
                    } else {
                        wrong = "Passwort";
                    }
                } else {
                    wrong = "Name";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.put("wrong", wrong);
            return ok(result);
        }
    }

    //---------------------- COOKBOOK -------------------------

    /**
     * Loads all recipe names for cookbook
     *
     * @return
     */
    public Result recipeNamesForCookbook() {
        ArrayList<Recipe> allRecipes = getCurrentUser().getRecipeListAll();
        String[] recipeNames = new String[allRecipes.size()];
        for (int i = 0; i < allRecipes.size(); i++) {
            recipeNames[i] = allRecipes.get(i).getName();
        }

        Gson gson = new Gson();
        String json = gson.toJson(recipeNames);
        return ok(json);
    }

    /**
     * Loads recipe name and instruction from recipe list of user
     * If user does not own recipe, only load name
     */
    public Result recipeForCookbook() {
        JsonNode json = request().body().asJson();
        int recipeId = json.findPath("recipeId").asInt();
        String recipeName = "";
        String instruction = "";
        String linktext = "";
        String linkHref = "";
        if (recipeId < getCurrentUser().getRecipeListAll().size()) {
            Recipe recipe = getCurrentUser().getRecipeListAll().get(recipeId);
            recipeName = recipe.getName();
            if (recipeId < getCurrentUser().getRecipeListOwned().size()) {
                instruction = recipe.getInstruction();
                linktext = "Dieses Rezept kochen";
                linkHref = "kueche?recipe=" + recipeName;
            } else if (recipeId == getCurrentUser().getRecipeListOwned().size()) {
                if (getCurrentUser().getCookies() >= 3) {
                    linktext = "Dieses Rezept für 3 Cookies freischalten";
                    linkHref = "buyNextRecipe";
                } else {
                    instruction = "Du brauchst 3 Cookies, um ein Rezept freischalten zu können.";
                }
            } else {
                instruction = "Du musst zuerst die vorherigen Rezepte freischalten, bevor du dieses kochen kannst.";
            }
        }
        ObjectNode result = Json.newObject();
        result.put("recipeName", recipeName);
        result.put("instruction", instruction);
        result.put("linktext", linktext);
        result.put("linkHref", linkHref);
        return ok(result);
    }

    /**
     * User buys next Recipe
     *
     * @return
     */
    public Result buyNextRecipe() {
        getCurrentUser().buyNewRecipe();
        return kochbuch();
    }

    //---------------------- KITCHEN ---------------------------

    /**
     * Called in kitchen
     * Checks if user owns ware, to display in kitchen
     *
     * @return false, if user does not own ware
     */
    public Result userOwnsWare() {
        JsonNode json = request().body().asJson();
        String warename = json.findPath("warename").textValue();
        boolean userOwnsWare = getCurrentUser().userOwnsWare(warename);
        ObjectNode result = Json.newObject();
        result.put("userOwnsWare", userOwnsWare);
        return ok(result);
    }


    /**
     * User cooks current recipe
     *
     * @return false if current action or ingredient are false
     */
    public Result cookRecipe() {
        JsonNode json = request().body().asJson();
        int step = json.findPath("step").asInt();
        String currentAction = json.findPath("currentAction").textValue();
        String currentIngredient = json.findPath("currentIngredient").textValue();

        boolean rightAction = false;
        Recipe cookingRecipe = getCurrentUser().getRecipeListOwned().get(getCurrentUser().getCurrentRecipe());

        //Test if current ingredient and current action equal their coresponding step in recipe
        //If yes, test if step is last step of recipe
        if (step < cookingRecipe.getIngredients().size()) {
            if (currentIngredient.equals(cookingRecipe.getIngredients().get(step).getWarename()) && currentAction.equals(cookingRecipe.getActions().get(step))) {
                if (step == cookingRecipe.getIngredients().size() - 1) {
                    step = 0;
                    getCurrentUser().changeCookies(5, true);
                    rightAction = true;
                } else {
                    rightAction = true;
                    step++;
                }
            } else {
                step = 0;
            }
        }
        ObjectNode result = Json.newObject();
        result.put("newStep", step);
        result.put("rightAction", rightAction);
        return ok(result);
    }

    //------------ SUPERMARKET ------------------------

    /**
     * Called in supermarket
     * Checks if user could buy ingredient, to display in supermarket
     *
     * @return false, if user does not own ingredient
     */
    public Result userCanBuyIngredient() {
        JsonNode json = request().body().asJson();
        String zutat = json.findPath("zutat").textValue();
        String username = session().get("user");
        List<Ware> warenlist = loggedInUsers.get(username).getWareListToBuy();
        boolean canBuy = false;
        ObjectNode result = Json.newObject();
        for (Ware w : warenlist) {
            if (w.getWarename().equals(zutat)) {
                canBuy = true;
                result.put("price", w.getPrice());
            }
        }
        result.put("canBuy", canBuy);
        return ok(result);
    }

    /**
     * User buys ware in supermarket
     *
     * @return false, if user does not have enough cookies to buy ware
     */
    public Result supermarketBuy() {
        JsonNode json = request().body().asJson();
        ObjectNode result = Json.newObject();
        String wareToBuy = json.findPath("currentWare").textValue();
        result.put("canBuy", getCurrentUser().buyWare(wareToBuy));
        return ok(result);
    }

    //---------------QUIZ-METHODS---------------

    /**
     * @return new question for quiz
     */
    public Result quizNext() {
        quizquestion = new Quizquestion();
        ObjectNode result = Json.newObject();
        result.put("Question", quizquestion.getQuestion());
        result.put("AnswerA", quizquestion.getAnswerA());
        result.put("AnswerB", quizquestion.getAnswerB());
        result.put("AnswerC", quizquestion.getAnswerC());
        result.put("AnswerD", quizquestion.getAnswerD());
        return ok(result);
    }

    /**
     * Checks if answer to question was right
     * If answer was right, user gets 1 cookie
     */
    public Result quizOK() {
        JsonNode json = request().body().asJson();
        String answer = json.findPath("Answer").textValue();
        String rightAnswer = quizquestion.getRightAnswer();
        ObjectNode result = Json.newObject();
        if (answer.equals(rightAnswer)) {
            getCurrentUser().changeCookies(1, true);
            return ok(result);
        } else {
            result.put("rightAnswer", rightAnswer);
            return ok(result);
        }
    }

    //----------------------- MEMORY --------------------------

    /**
     * User gets 1 Cookie after having solved the Memory
     */
    public Result userWonMemory() {
        getCurrentUser().changeCookies(1, true);
        ObjectNode result = Json.newObject();
        result.put("cookies", getCurrentUser().getCookies());
        return ok(result);
    }

}