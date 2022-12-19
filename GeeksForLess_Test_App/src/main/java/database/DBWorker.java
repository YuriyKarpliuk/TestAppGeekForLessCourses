package database;

import java.sql.*;

public class DBWorker {
    private static DBWorker db = null;
    private static final String URL = "jdbc:mysql://localhost:3306/testApp?allowPublicKeyRetrieval=true&&autoReconnect=true&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "2003";
    private Connection connection;

    private PreparedStatement preparedStatement;
    private Statement statement;


    public DBWorker() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Error while loading driver");
        }
    }

    public static DBWorker getInstance() {
        if (db == null) {
            db = new DBWorker();
        }
        return db;
    }

    public Connection getConnection() {
        return connection;
    }

    public void saveExpression(String exp, Double result) {
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO expression(expressionForm,result) VALUES (?,?)");
            preparedStatement.setString(1, exp);
            preparedStatement.setDouble(2, result);
            preparedStatement.executeUpdate();
            System.out.println("Expression and result successfully saved to database!");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void getExpressions() {
        try {
            preparedStatement = connection.prepareStatement("Select * from expression");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id")+ ". " + rs.getString("expressionForm") + " = " + rs.getDouble("result"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void editExpression(Integer number,String exp, Double result) {
        try {
            preparedStatement = connection.prepareStatement("update  expression set expressionForm= ? , result=? where id=?");
            preparedStatement.setString(1, exp);
            preparedStatement.setDouble(2, result);
            preparedStatement.setInt(3, number);
            preparedStatement.executeUpdate();
            System.out.println("Expression successfully updated!");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void getExpressionsByResult(Double result) {
        try {
            preparedStatement = connection.prepareStatement("Select * from expression where result=?");
            preparedStatement.setDouble(1, result);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id") +". " + rs.getString("expressionForm") + " = " + rs.getDouble("result"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void getExpressionsByResultBiggerThan(Double result) {
        try {
            preparedStatement = connection.prepareStatement("Select * from expression where result>?");
            preparedStatement.setDouble(1, result);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id")+ ". " + rs.getString("expressionForm") + " = " + rs.getDouble("result"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void getExpressionsByResultLessThan(Double result) {
        try {
            preparedStatement = connection.prepareStatement("Select * from expression where result<?");
            preparedStatement.setDouble(1, result);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("expressionForm") + " = " + rs.getDouble("result"));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

}
