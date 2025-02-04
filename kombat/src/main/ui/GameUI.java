package main.ui;

import java.util.Scanner;

import main.game.GameState;
import main.game.Player;

public class GameUI {
    public static void main(String[] args) {
        Player p1 = new Player("Alice", 1000);
        Player p2 = new Player("Bob", 1000);
        GameState game = new GameState(p1, p2, 20);
        
        Scanner scanner = new Scanner(System.in);
        while (!game.isGameOver()) {
            System.out.println("Enter command for player 1:");
            String command = scanner.nextLine();
            p1.spawnMinion("Warrior", 0, 0, game.board);
            game.processTurn(p1);
            game.processTurn(p2);
        }
        scanner.close();
    }
}
