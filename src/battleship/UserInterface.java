package battleship;

import java.util.Scanner;

public class UserInterface {
    private final Battleship player1;
    private final Battleship player2;
    private final Scanner sc = new Scanner(System.in);

    public UserInterface() {
        this.player1 = new Battleship();
        this.player2 = new Battleship();
    }

    public void play() {
        System.out.println("Player 1, place your ships on the game field\n");
        System.out.println(player1 + "\n");
        player1.placeShips();
        pressEnterToContinue();
        System.out.println("Player 2, place your ships to the game field\n");
        System.out.println(player2 + "\n");
        player2.placeShips();
        pressEnterToContinue();

        while (player1.anyShipsLeft() && player2.anyShipsLeft()) {
            System.out.println(player2.boardCoveredInFogToString());
            System.out.println("---------------------");
            System.out.println(player1);
            System.out.println("\nPlayer 1, it's your turn:\n");
            player2.takeAShot();
            pressEnterToContinue();

            if (!player2.anyShipsLeft()) {
                break;
            }

            System.out.println(player1.boardCoveredInFogToString());
            System.out.println("---------------------");
            System.out.println(player2);
            System.out.println("\nPlayer 2, it's your turn:\n");
            player1.takeAShot();
            pressEnterToContinue();
        }
    }

    public void pressEnterToContinue() {
        System.out.println("Press Enter and pass the move to another player");
        sc.nextLine();
        System.out.println("...\n");
    }

}
