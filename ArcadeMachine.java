package Arcade;

import com.mysql.cj.conf.ConnectionUrlParser.Pair;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class ArcadeMachine {
    Scanner scnr = new Scanner(System.in);
    ArcadeServer server = new ArcadeServer();
    ArcadeGame mortalCombat = new ArcadeGame(20, 1);
    ArcadeGame fifa = new ArcadeGame(30, 2);
    ArcadeGame raceArcade = new ArcadeGame(30, 2.50);

    protected void insertCard() {
        boolean j = server.verifyCard();
        if (j == true)
            System.out.println("Verification success");
        else {
            System.out.println("Card error");
            return;
        }
        System.out.print("Please enter your first name:");
        String fName = scnr.next();
        System.out.print("Please enter you last name:");
        String lName = scnr.next();
        Pair<Integer, Double> newPlayer;
        try {
            newPlayer = server.checkName(fName, lName);
            int id = newPlayer.left;
            double balance = newPlayer.right;
            System.out.println("\nHello " + fName + "!");
            System.out.println("Your current balance: $" + balance + "\n");

            final int gameId = gameMenu(balance);
            final int score = gameScore(gameId);
            server.updateScore(id, gameId, score);
            viewBoard(gameId);
            chargeCard(id, gameId, balance);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Please take your card.");

    }

    protected void insertCash() {
        double choice2 = 0.00;
        try {
            System.out.println("Please insert the cash amount in dollars. Quarters are accepted.");
            choice2 = scnr.nextDouble();
            int gameId = gameMenu(choice2);
            gameScore(gameId);
            returnChange(gameId, choice2);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. please enter a decimal number.");
            scnr.next();
        }


    }

    public int gameMenu(double balance) {
        Scanner scanner = new Scanner(System.in);

        if (balance < mortalCombat.getCost()) {
            System.out.println("You don't have enough balance to play any game.");
            return -1;
        } else if (balance < fifa.getCost()) {
            System.out.println("Please select a game to play:");
            System.out.println("1. Mortal Combat ($1 for 20 mins)");
        } else if (balance < raceArcade.getCost()) {
            System.out.println("Please select a game to play:");
            System.out.println("1. Mortal Combat ($1 for 20 mins)");
            System.out.println("2. Fifa 2023 ($2 for 30 mins)");
        } else {
            System.out.println("Please select a game to play:");
            System.out.println("1. Mortal Combat ($1 for 20 mins)");
            System.out.println("2. Fifa 2023 ($2 for 30 mins)");
            System.out.println("3. Race Arcade ($2.50 for 30 mins)");
        }

        int game = 0;
        boolean validChoice = false;
        while (!validChoice) {
            System.out.print("Enter the number of the game you want to play: ");
            String input = scnr.next();
            try {
                game = Integer.parseInt(input);
                if (balance >= raceArcade.getCost() && game >= 1 && game <= 3) {
                    validChoice = true;
                } else if (balance >= fifa.getCost() && game >= 1 && game <= 2) {
                    validChoice = true;
                } else if (balance >= mortalCombat.getCost() && game == 1) {
                    validChoice = true;
                } else {
                    System.out.println("Invalid choice. Please enter a valid number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        playGame(game);
        return game;
    }

    public void playGame(int gameId) {
        switch (gameId) {
            case 1:
                mortalCombat.playMortalCombat();
                break;
            case 2:
                fifa.playFifa2023();
                break;
            case 3:
                raceArcade.playRaceArcade();
                break;
        }
    }

    public int gameScore(int gameId) {
        final int score = new Random().nextInt(100000);
        System.out.println("Final Score: " + score);
        return score;
    }

    public void viewBoard(int gameId) {
        System.out.println("View leaderboard? (Y/N)");
        String choice = scnr.next();
        while (!choice.equalsIgnoreCase("Y") && !choice.equalsIgnoreCase("N")) {
            System.out.println("Invalid input. Please try again.");
            choice = scnr.nextLine();
        }
        if (choice.equalsIgnoreCase("Y")) {
            server.leaderboard(gameId);
        } else {
            return;
        }
    }

    public void chargeCard(int id, int gameId, double balance) {
        double cost = 0;
        switch (gameId) {
            case 1:
                cost = mortalCombat.getCost();
                break;
            case 2:
                cost = fifa.getCost();
                break;
            case 3:
                cost = raceArcade.getCost();
                break;
        }
        balance = balance - cost;
        server.updateBalance(id, balance);
    }

    public void returnChange(int gameId, double cashAmount) {
        double cost = 0;
        switch (gameId) {
            case 1:
                cost = mortalCombat.getCost();
                break;
            case 2:
                cost = fifa.getCost();
                break;
            case 3:
                cost = raceArcade.getCost();
                break;
        }
        cashAmount = cashAmount - cost;
        System.out.println("Here is your change: $" + cashAmount);
    }

}
