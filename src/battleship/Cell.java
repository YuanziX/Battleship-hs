package battleship;

public class Cell implements Comparable<Cell> {
    public final char row;
    public final int column;

    public Cell(char row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public int compareTo(Cell o) {
        if (this.row == o.row) {
            return this.column - o.column;
        }
        return this.row - o.row;
    }
}
