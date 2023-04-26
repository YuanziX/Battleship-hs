package battleship;

import java.util.Scanner;

public class UserInterface {
    private final Board player1;
    private final Board player2;
    private final Scanner sc = new Scanner(System.in);

    public UserInterface() {
        this.player1 = new Board("Player 1");
        this.player2 = new Board("Player 2");
    }

    public void play() {
        Board player = player1;
        for (int i = 0; i < 2; i++) {
            System.out.printf("%s, place your ships on the game field%n", player.getName());
            System.out.println(player + "\n");
            player.placeShips();
            player = player == player1 ? player2 : player1;
            pressEnterToContinue();
        }

        Board notPlayer = player2;
        while (player1.anyShipsLeft() && player2.anyShipsLeft()) {
            System.out.println(notPlayer.boardCoveredInFogToString());
            System.out.println("---------------------");
            System.out.println(player);
            System.out.printf("%n%s, it's your turn:%n", player.getName());
            notPlayer.takeAShot();
            pressEnterToContinue();

            if (!notPlayer.anyShipsLeft()) {
                break;
            }

            notPlayer = notPlayer == player1 ? player2 : player1;
            player = player == player1 ? player2 : player1;
        }

        sc.close();
    }

    public void pressEnterToContinue() {
        System.out.println("Press Enter and pass the move to another player");
        sc.nextLine();
        System.out.println("...\n");
    }

}
