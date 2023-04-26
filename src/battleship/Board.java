package battleship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;

public class Board {
    private final String name;
    private final Scanner sc = new Scanner(System.in);
    private final String[][] board;
    private final String[][] boardInFog;

    public Board(String name) {
        this.name = name;
        this.board = newBoard();
        this.boardInFog = newBoard();
    }

    private static String[][] newBoard() {
        String[][] board = new String[11][11];
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
        return board;
    }

    public void placeShips() {
        for (Battleships b : Battleships.values()) {
            this.placeShip(b);
            System.out.println(this);
            System.out.println();
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

    public void takeAShot() {
        while (true) {
            Cell[] coord = this.parseInput(sc.nextLine());

            if (coord == null) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
                continue;
            }

            String symbolAtCell = board[coord[0].row - 64][coord[0].column];
            if (symbolAtCell.equals("O") || symbolAtCell.equals("X")) {
                board[coord[0].row - 64][coord[0].column] = "X";
                boardInFog[coord[0].row - 64][coord[0].column] = "X";

                if (shipSunk(coord)) {
                    System.out.println("You sank a ship!");
                } else {
                    System.out.println("You hit a ship!");
                }

            } else {
                board[coord[0].row - 64][coord[0].column] = "M";
                boardInFog[coord[0].row - 64][coord[0].column] = "M";
                System.out.println("You missed!");
            }

            if (!anyShipsLeft()) {
                System.out.println("You sank the last ship. You won. Congratulations!");
            }
            break;
        }
    }

    public boolean shipSunk(Cell[] coords) {
        /* only meant for single cell */
        if (coords.length > 1) {
            return false;
        }

        char row = coords[0].row;
        int column = coords[0].column;

        boolean nothingUp = row == 'A' || !board[row - 64 - 1][column].equals("O");
        boolean nothingDown = row == 'J' || !board[row - 64 + 1][column].equals("O");
        boolean nothingLeft = column == 1 || !board[row - 64][column - 1].equals("O");
        boolean nothingRight = column == 10 || !board[row - 64][column + 1].equals("O");

        return nothingUp && nothingDown && nothingLeft && nothingRight;
    }

    public boolean anyShipsLeft() {
        for (String[] strings : board) {
            for (String string : strings) {
                if (string.equals("O")) {
                    return true;
                }
            }
        }
        return false;
    }

    private void placeShip(Battleships battleship) {
        System.out.printf("Enter the coordinates of the %s (%d cells):%n%n", battleship.name, battleship.size);
        while (true) {
            Cell[] coords = this.parseInput(sc.nextLine());
            System.out.println();
            if (coords == null) {
                System.out.println("Invalid input! Try again:");
                continue;
            }

            boolean verified = verifyPlacement(coords, battleship);
            if (verified) {
                placeShipOnBoard(coords);
                break;
            }
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
            System.out.printf("Error! Wrong length of the %s! Try again:%n%n", battleship.name);
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
            System.out.println("Error! You placed it too close to another one. Try again:\n");
            return false;
        }

        return true;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        /* returns normal board */
        return getString(board);
    }

    public String boardCoveredInFogToString() {
        return getString(boardInFog);
    }

    private String getString(String[][] board) {
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
