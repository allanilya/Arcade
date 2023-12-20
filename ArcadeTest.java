package Arcade;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ArcadeTest {

    public static void main(String[] args) {
        ArcadeMachine machine = new ArcadeMachine();
        Scanner scnr = new Scanner(System.in);
        int choice1 = 0;
        try {
            System.out.println("Howdy! Please insert cash(1) or card(2) to begin. \n$1 minimum to play!");
            choice1 = scnr.nextInt();

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        while (choice1 != 1 && choice1 != 2) {
            System.out.println("Sorry, please try again. Press 1 to insert cash or 2 to insert card.");
            choice1 = scnr.nextInt();
        }
        if (choice1 == 1) {
            machine.insertCash();
        } else if (choice1 == 2) {
            machine.insertCard();
        }
    }
}