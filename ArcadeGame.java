package Arcade;

public class ArcadeGame {
    private int duration;  // in minutes
    private double cost;

    public ArcadeGame(int duration, double cost) {
        this.duration = duration;
        this.cost = cost;
    }

    public int getDuration() {
        return duration;
    }

    public double getCost() {
        return cost;
    }

    public void playMortalCombat() {
        System.out.println("Playing Mortal Combat...");
        try {
            // Game duration is sped up for demonstation
            Thread.sleep(duration * 20);
            System.out.println("...");
            Thread.sleep(duration * 20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Mortal Combat Game Over.\n");
    }

    public void playFifa2023() {
        System.out.println("Playing Fifa 2023...");
        try {
            Thread.sleep(duration * 20);
            System.out.println("...");
            Thread.sleep(duration * 20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Fifa 2023 Game Over.\n");
    }

    public void playRaceArcade() {
        System.out.println("Playing Race Arcade...");
        try {
            Thread.sleep(duration * 20);
            System.out.println("...");
            Thread.sleep(duration * 20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Race Arcade Game Over.\n");
    }
}