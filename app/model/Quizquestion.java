package model;

import java.sql.*;

public class Quizquestion {
    private String question;
    private String answerA;
    private String answerB;
    private String answerC;
    private String answerD;
    private String rightAnswer;

    /**
     * Constructs a new Quizquestion
     */
    public Quizquestion() {
        try {
            String[] QuestionAnswer = getQuizquestionFromDB(random(getNumbersOfQuestions()));
            this.question = QuestionAnswer[0];
            this.answerA = QuestionAnswer[1];
            this.answerB = QuestionAnswer[2];
            this.answerC = QuestionAnswer[3];
            this.answerD = QuestionAnswer[4];
            this.rightAnswer = QuestionAnswer[5];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param maximum
     * @return random number between 1 - maximum(getNumbersOfQuestions())
     */
    private int random(int maximum) {
        int range = (maximum - 1) + 1;
        return (int) (Math.random() * range) + 1;
    }

    /**
     * Getter for property 'numbersOfQuestions'.
     *
     * @return Value for property 'numbersOfQuestions'.
     */
    private int getNumbersOfQuestions() throws SQLException {

        Connection con = controllers.Application.db.getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;
        String query = "SELECT COUNT(*) FROM Quizquestion;";
        int numberOfRows = 0;

        statement = con.prepareStatement(query);
        rs = statement.executeQuery();
        while (rs.next()) {
            numberOfRows = rs.getInt(1);
        }
        con.close();
        return numberOfRows;
    }

    /**
     * Loads quizquestion from database
     */
    private String[] getQuizquestionFromDB(int id) throws SQLException {

        String[] result = new String[6];
        Connection con = controllers.Application.db.getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;
        String query = "";

        //getQuestion
        query = "SELECT question FROM Quizquestion WHERE idQuizquestion = \"" + id + "\";";
        statement = con.prepareStatement(query);
        rs = statement.executeQuery();
        while (rs.next()) {
            result[0] = rs.getString("question");
        }

        //getAnswerA
        query = "SELECT answerA FROM Quizquestion WHERE idQuizquestion = \"" + id + "\";";
        statement = con.prepareStatement(query);
        rs = statement.executeQuery();
        while (rs.next()) {
            result[1] = rs.getString("answerA");
        }

        //getAnswerB
        query = "SELECT answerB FROM Quizquestion WHERE idQuizquestion = \"" + id + "\";";
        statement = con.prepareStatement(query);
        rs = statement.executeQuery();
        while (rs.next()) {
            result[2] = rs.getString("answerB");
        }

        //getAnswerC
        query = "SELECT answerC FROM Quizquestion WHERE idQuizquestion = \"" + id + "\";";
        statement = con.prepareStatement(query);
        rs = statement.executeQuery();
        while (rs.next()) {
            result[3] = rs.getString("answerC");
        }

        //getAnswerD
        query = "SELECT answerD FROM Quizquestion WHERE idQuizquestion = \"" + id + "\";";
        statement = con.prepareStatement(query);
        rs = statement.executeQuery();
        while (rs.next()) {
            result[4] = rs.getString("answerD");
        }

        //getRightAnswer
        query = "SELECT rightAnswer FROM Quizquestion WHERE idQuizquestion = \"" + id + "\";";
        statement = con.prepareStatement(query);
        rs = statement.executeQuery();
        while (rs.next()) {
            result[5] = rs.getString("rightAnswer");
        }
        con.close();
        return result;
    }

    //--------------- GETTER AND SETTER ------------------

    public String getQuestion() {
        return question;
    }

    public String getAnswerA() {
        return answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }
}
