package battleship;

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
            this.placeShip(b.name, b.size);
            System.out.println(this);
        }
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

    public Cell[] parseInput(String coords) {
        if (!coords.matches("[A-J]\\d{1,2} [A-J]\\d{1,2}")) {
            return null;
        }

        String[] coordsData = coords.split(" ");
        return new Cell[]{new Cell(coordsData[0].charAt(0), Integer.parseInt(coordsData[0].substring(1))),
                new Cell(coordsData[1].charAt(0), Integer.parseInt(coordsData[1].substring(1)))};
    }

    private void placeShip(String name, int size) {
        System.out.printf("Enter the coordinates of the %s (%d cells):%n> ", name, size);
        while (true) {
            Cell[] coords = this.parseInput(sc.nextLine());
            if (coords == null) {
                System.out.print("Invalid input! Try again:\n> ");
                continue;
            }

            boolean verified = verifyPlacement(coords, size, name);
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

    private boolean isOutsideBoard(Cell[] coords) {
        return coords[1].row > 'J' || coords[1].column > 10;
    }

    private boolean verifyPlacement(Cell[] coords, int size, String name) {
        char row1 = coords[0].row;
        char row2 = coords[1].row;
        int column1 = coords[0].column;
        int column2 = coords[1].column;

        if (isOutsideBoard(coords)) {
            System.out.println("Placement is outside of the board! Try again:");
            return false;
        }

        // Checking if the coordinates are diagonal
        if (row1 != row2 && column1 != column2) {
            System.out.println("Error! Wrong ship location! Try again:");
            return false;
        }

        boolean isVertical = this.isVertical(coords);
        boolean correctLength;
        if (isVertical) {
            correctLength = size == row2 - row1 + 1;
        } else {
            correctLength = size == Math.abs(column2 - column1) + 1;
        }
        if (!correctLength) {
            System.out.printf("Error! Wrong length of the %s! Try again:%n", name);
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

            for (int i = column1; i <= column2; i++) {
                board[row1 - 64][i] = "O";
            }
        }

        boolean nothingAround = nothingUp && nothingDown && nothingLeft && nothingRight && nothingColliding;
        if (!nothingAround) {
            System.out.println("Error! You placed it too close to another one. Try again:");
        }

        return nothingAround;
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
