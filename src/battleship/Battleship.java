package battleship;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Battleship {
    private final Scanner sc = new Scanner(System.in);
    private String[][] board;

    public Battleship() {
        newBoard();
    }

    public void play() {
        System.out.println(this);
        for (Battleships b : Battleships.values()) {
            this.placeShip(b);
            System.out.println(this);
        }
        System.out.println("The game starts!");
        System.out.println(this);
        this.takeAShot();

    }

    private void newBoard() {
        board = new String[11][11];
        board[0][0] = " ";
        for (int i = 1; i <= 10; i++) {
            board[0][i] = String.valueOf(i);
            board[i][0] = String.valueOf((char) (64 + i));
        }

        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                board[i][j] = "~";
            }
        }
    }

    private Cell[] parseInput(String coords) {
        String[] coordsData = coords.split(" ");

        ArrayList<Cell> cells = new ArrayList<>();

        for (String s : coordsData) {
            if (!s.matches("[A-J]([1-9]|10)")) {
                return null;
            }
            char letter = s.charAt(0);
            int number = Integer.parseInt(s.substring(1));
            cells.add(new Cell(letter, number));
        }

        if (cells.size() < 1 || cells.size() > 2) {
            return null;
        }

        Collections.sort(cells);

        Cell[] cellArray = new Cell[cells.size()];
        cellArray = cells.toArray(cellArray);

        return cellArray;
    }

    private void takeAShot() {
        System.out.print("Take a shot!\n> ");
        while (true) {
            Cell[] coord = this.parseInput(sc.nextLine());

            if (coord == null) {
                System.out.print("Error! You entered the wrong coordinates! Try again:\n> ");
                continue;
            }

            if (board[coord[0].row - 64][coord[0].column].equals("O")) {
                System.out.println("You hit a ship!");
                board[coord[0].row - 64][coord[0].column] = "X";
                System.out.println(this);
            } else {
                System.out.println("You missed!");
                board[coord[0].row - 64][coord[0].column] = "M";
                System.out.println(this);
            }
            break;
        }
    }

    private void placeShip(Battleships battleship) {
        System.out.printf("Enter the coordinates of the %s (%d cells):%n> ", battleship.name, battleship.size);
        while (true) {
            Cell[] coords = this.parseInput(sc.nextLine());
            if (coords == null) {
                System.out.print("Invalid input! Try again:\n> ");
                continue;
            }

            boolean verified = verifyPlacement(coords, battleship);
            if (verified) {
                placeShipOnBoard(coords);
                break;
            }
            System.out.print("> ");
        }
    }

    private void placeShipOnBoard(Cell[] coords) {
        if (this.isVertical(coords)) {
            for (int i = coords[0].row; i <= coords[1].row; i++) {
                board[i - 64][coords[0].column] = "O";
            }
        } else {
            for (int i = coords[0].column; i <= coords[1].column; i++) {
                board[coords[0].row - 64][i] = "O";
            }
        }
    }


    private boolean isVertical(Cell[] coords) {
        // Checking orientation of the ship to be placed
        return coords[0].row != coords[1].row;
    }

    private boolean verifyPlacement(Cell[] coords, Battleships battleship) {
        char row1 = coords[0].row;
        char row2 = coords[1].row;
        int column1 = coords[0].column;
        int column2 = coords[1].column;

        // Checking if the coordinates are diagonal
        if (row1 != row2 && column1 != column2) {
            System.out.println("Error! Wrong ship location! Try again:");
            return false;
        }

        boolean isVertical = this.isVertical(coords);
        boolean correctLength;
        if (isVertical) {
            correctLength = battleship.size == row2 - row1 + 1;
        } else {
            correctLength = battleship.size == Math.abs(column2 - column1) + 1;
        }
        if (!correctLength) {
            System.out.printf("Error! Wrong length of the %s! Try again:%n", battleship.name);
            return false;
        }

        boolean nothingUp = true;
        boolean nothingDown = true;
        boolean nothingLeft = true;
        boolean nothingRight = true;
        boolean nothingColliding = true;

        if (isVertical) {
             /* or (||) short-circuits hence the second part of the statement isn't run if first is satisfied
             therefore no IndexOutOfBoundsException is thrown */
            nothingUp = row1 == 'A' || this.board[row1 - 64 - 1][column1].equals("~");
            nothingDown = row2 == 'J' || this.board[row2 - 64 + 1][column2].equals("~");
            if (column1 != 1) {
                for (int i = row1; i <= row2; i++) {
                    if (board[i - 64][column1 - 1].equals("O")) {
                        nothingLeft = false;
                        break;
                    }
                }
            }

            if (column1 != 10) {
                for (int i = row1; i <= row2; i++) {
                    if (board[i - 64][column1 + 1].equals("O")) {
                        nothingRight = false;
                        break;
                    }
                }
            }

            for (int i = row1; i <= row2; i++) {
                if (Objects.equals(board[i - 64][column1], "O")) {
                    nothingColliding = false;
                    break;
                }
            }

        } else {
            nothingLeft = column1 == 1 || this.board[row1 - 64][column1 - 1].equals("~");
            nothingRight = column1 == 10 || this.board[row1 - 64][column1 + 1].equals("~");

            if (row1 != 'A') {
                for (int i = column1; i <= column2; i++) {
                    if (board[row1 - 64 - 1][i].equals("O")) {
                        nothingUp = false;
                        break;
                    }
                }
            }

            if (row1 != 'J') {
                for (int i = column1; i <= column2; i++) {
                    if (board[row1 - 64 + 1][i].equals("O")) {
                        nothingDown = false;
                        break;
                    }
                }
            }
        }

        boolean nothingAround = nothingUp && nothingDown && nothingLeft && nothingRight && nothingColliding;
        if (!nothingAround) {
            System.out.println("Error! You placed it too close to another one. Try again:");
            return false;
        }

        return true;
    }

    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                ret.append(board[i][j]).append(" ");
            }
            if (i != board.length - 1) {
                ret.append('\n');
            }
        }
        return ret.toString();
    }
}
