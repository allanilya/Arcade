package Arcade;

import com.mysql.cj.conf.ConnectionUrlParser.Pair;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;

public class ArcadeServer {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; //replace the values below with your exact credentials
    static final String userName = "root";
    static final String password = "Gorillaglue2";
    static final String url = "jdbc:mysql://localhost:3306/arcadeserver";
    Scanner scnr = new Scanner(System.in);
    Random rand = new Random();
    private Connection conn;

    protected boolean verifyCard() {
        // Made card verification biased toward verifying the card
        double bias = 0.75;
        return rand.nextDouble() < bias;
    }

    // this method searches for a user with the given first name and last name and creates a new tuple if no match is found
    public Pair<Integer, Double> checkName(String firstName, String lastName) throws SQLException {
        conn = DriverManager.getConnection(url, userName, password);
        // create a SQL query to select the user with the given first and last name
        String query = "SELECT id, balance FROM user_balance WHERE fName = ? AND lName = ?";

        PreparedStatement ps = conn.prepareStatement(query);

        ps.setString(1, firstName);
        ps.setString(2, lastName);

        ResultSet rs = ps.executeQuery();

        // check if the result set has any rows
        if (rs.next()) {
            // if the result set is not empty, the user was found, and the id and balance will be returned
            int id = rs.getInt("id");
            double balance = rs.getDouble("balance");
            return new Pair<>(id, balance);
        } else {
            // if the result set is empty, the user was not found a new tuple is created with a random balance between 0 and 50
            double balance = new Random().nextDouble(51);
            DecimalFormat df = new DecimalFormat("#.##");
            balance = Double.parseDouble(df.format(balance));
            query = "INSERT INTO user_balance (fName, lName, balance) VALUES (?, ?, ?)";

            // prepares the SQL statement for the insert query
            PreparedStatement insert = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            insert.setString(1, firstName);
            insert.setString(2, lastName);
            insert.setDouble(3, balance);

            insert.executeUpdate();
            ResultSet generatedKeys = insert.getGeneratedKeys();

            // get the ID of the new tuple
            int id = -1;
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            }

            return new Pair<>(id, balance);
        }
    }

    public void updateScore(int id, int gameNumber, int score) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String user = null;

        try {
            conn = DriverManager.getConnection(url, userName, password);

            // search for username with the given id across all tables
            String sql = "SELECT username FROM mc_leaderboard WHERE id = ? UNION "
                    + "SELECT username FROM fifa_leaderboard WHERE id = ? UNION "
                    + "SELECT username FROM ra_leaderboard WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setInt(2, id);
            ps.setInt(3, id);
            rs = ps.executeQuery();

            // if a username is found in one table, copy the username and insert it into the given game
            if (rs.next()) {
                user = rs.getString("username");
                sql = "INSERT INTO ";
                switch (gameNumber) {
                    case 1:
                        sql += "mc_leaderboard";
                        break;
                    case 2:
                        sql += "fifa_leaderboard";
                        break;
                    case 3:
                        sql += "ra_leaderboard";
                        break;
                    default:
                        System.out.println("Invalid game number.");
                        return;
                }
                sql += " (id, username, score) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE score = IF(score < VALUES(score), VALUES(score), score)";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
                ps.setString(2, user);
                ps.setInt(3, score);
                ps.executeUpdate();

            }
            // if no username is found, ask for a username and insert a new tuple into the given game table
            else {
                System.out.println("Please enter a username:");
                user = scnr.nextLine();
                sql = "INSERT INTO ";
                switch (gameNumber) {
                    case 1:
                        sql += "mc_leaderboard";
                        break;
                    case 2:
                        sql += "fifa_leaderboard";
                        break;
                    case 3:
                        sql += "ra_leaderboard";
                        break;
                    default:
                        System.out.println("Invalid game number.");
                        return;
                }
                sql += " (id, username, score) VALUES (?, ?, ?)";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, id);
                ps.setString(2, user);
                ps.setInt(3, score);
                ps.executeUpdate();
                switch (gameNumber) {
                    case 1:
                        System.out.println("New user " + user + " added to the Mortal Combat Leaderboard.");
                        break;
                    case 2:
                        System.out.println("New user " + user + " added to the Fifa 2023 Leaderboard.");
                        break;
                    case 3:
                        System.out.println("New user " + user + " added to the Race Arcade Leaderboard");
                        break;
                    default:
                        System.out.println("Invalid game number.");
                        return;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void leaderboard(int gameId) {
        String tableName;
        if (gameId == 1) {
            tableName = "mc_leaderboard";
        } else if (gameId == 2) {
            tableName = "fifa_leaderboard";
        } else if (gameId == 3) {
            tableName = "ra_leaderboard";
        } else {
            System.out.println("Invalid game number!");
            return;
        }

        // Leaderboard query to select top 10 scores for the given game
        String query = "SELECT username, score FROM " + tableName + " ORDER BY score DESC LIMIT 10;";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            switch (gameId) {
                case 1:
                    System.out.println("Leaderboard for Mortal Combat");
                    break;
                case 2:
                    System.out.println("Leaderboard for Fifa 2023");
                    break;
                case 3:
                    System.out.println("LeaderBoard for Race Arcade");
                    break;
            }
            System.out.println("Username\tScore");
            while (rs.next()) {
                String username = rs.getString("username");
                int score = rs.getInt("score");
                System.out.println(username + "\t\t" + score);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //updates user balance
    public void updateBalance(int id, double updatedBalance) {
        try {
            conn = DriverManager.getConnection(url, userName, password);
            Statement stmt = conn.createStatement();
            String sql = "UPDATE user_balance SET balance = " + updatedBalance + " WHERE id = " + id;
            int rowsAffected = stmt.executeUpdate(sql);
            System.out.println("\nUpdated balance: $" + updatedBalance);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
