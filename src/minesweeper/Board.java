/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

/**
 * A minesweeper board is a grid of squares. Each square is either flagged, dug,
 * or untouched. Each square also contains a bomb or does not contain a bomb.
 * <p>
 * The axis of the board is defined with (x,y) coordinates starting at (0,0) in
 * the top-left corner, extending horizontally to the right in the x-direction,
 * and vertically down in the y-direction.
 * <p>
 * (x,y) coordinate of a square is within bounds if both x and y are greater
 * than or equal to 0 and less than the size of the board.
 */
public class Board {
    private final Square[][] board;
    private final int sizeX;
    private final int sizeY;

    // TODO: Abstraction function, rep invariant, rep exposure, thread safety
    // Rep invariant:
    //  board must be of dimensions sizeY by sizeX. A Square cannot be dug
    //  and mined at same time.
    // Abstract function:
    //  board, dimensions sizeY by sizeX, represents a sizeX by sizeY grid of
    //  squares where board[y][x] represents square x,y on the axis defined in
    //  the spec. Each Square board[y][x] corresponds to the following values:
    //  - If board[y][x] is dug with no mined neighbors ---> square x,y is dug
    //  and is denoted by " " (single-space).
    //  - If board[y][x] is dug with mined neighbors ---> square x,y is dug and
    //  is denoted by an integer value between 1-8 counting the number of mined
    //  neighbors.
    //  - If board[y][x] is flagged ---> square x,y is also flagged and denoted
    //  with "F".
    //  - If board[y][x] is untouched ---> square x,y is also untouched and
    //  denoted with "-".
    // Rep exposure:
    //  Only exposed parts of the rep--sizeX and sizeY--are immutable.
    // Thread safety argument:
    //  - No rep exposure.
    //  - sizeX and sizeY are immutable and final.
    //  - All access to the mutable rep, board, is guarded by this object's lock.
    
    // TODO: Specify, test, and implement in problem 2
    
    private static class Square {
        private boolean mined;
        private State state;
        
        private enum State {
            Untouched,
            Flagged,
            Dug
        }
        
        Square(boolean mined) {
            this.mined = mined;
            this.state = State.Untouched;
        }
        
        /**
         * Digs this square exploding its mine (if there is one)
         * 
         * @return true if this dig exploded a mine.
         */
        boolean dig() {
            if (state == State.Untouched) {
                state = State.Dug;
            }
            
            if (mined) {
                mined = false;
                return true;
            }
            
            return false;
        }
        
        /**
         * @return true if this square contains a bomb
         */
        boolean mined(){
            return mined;
        }
        
        /**
         * Flag this square
         */
        void flag() {
            if(state == State.Untouched) {
                state = State.Flagged;
            }
        }
        
        /**
         * Deflag this square
         */
        void deflag() {
            if(state == State.Flagged) {
                state = State.Untouched;
            }
        }
        
        /**
         * @return The sate of this square
         */
        State state() {
            return state;
        }   
    }
    
    /**
     * Determines if coordinates x,y on the axis are within the bounds of this
     * board.
     * 
     * @param x
     *            x-coordinate
     * @param y
     *            y-coordinate
     * @return true if x,y is within bounds of this board (0 <= x and <= sizeX
     *         and 0 <= y <= sizeY)
     */
    private boolean inBound(int x, int y) {
       return 0 <= x && x < sizeX && 0 <= y && y < sizeY;
    }
    
    /**
     * The number of mined neighbors of square x,y
     * 
     * @param x
     *            x-coordinate of the a square
     * @param y
     *            y-coordinate of the square
     * @return The number of mined neighbors of square x,y if x,y is in bounds;
     *         else, return 0.
     */
    private int count(int x, int y) {
        int count = 0;
        count += mined(x - 1, y - 1) ? 1 : 0;
        count += mined(x, y - 1) ? 1 : 0;
        count += mined(x + 1, y - 1) ? 1 : 0;
        count += mined(x + 1, y) ? 1 : 0;
        count += mined(x + 1, y + 1) ? 1 : 0;
        count += mined(x, y + 1) ? 1 : 0;
        count += mined(x - 1, y + 1) ? 1 : 0;
        count += mined(x - 1, y) ? 1 : 0;
        return count;
    }
        
    /**
     * Indicates if square at the x,y coordinate in this board contains a bomb.
     * 
     * @param x
     *            x-coordinate of the square
     * @param y
     *            y-coordinate of the square
     * @return true if square x,y is in bound and contains a bomb
     */
    boolean mined(int x, int y){
        if(inBound(x, y)) {
            return board[y][x].mined(); 
        }
        
        return false;
    }
    
    /**
     * Initialize a board from a bombs grid. A board of equals size to bombs
     * will be created with all squares untouched. An square x,y will contain a
     * bomb if only if bombs contains a 1 in the same location.
     * 
     * @param sizeX
     *            The size of the grid along the x-axis
     * @param sizeY
     *            The size of the grid along the y-axis
     * @param bombs
     *            A sizeX by sizeY grid of newline-separated (\n, \r, or \r\n)
     *            rows of space-separated 1s and 0s representing the location of
     *            bombs in a board. A location contains a bomb if and only if it
     *            contains a 1.
     */
    public Board(int sizeX, int sizeY, String bombs) {
        this.board = new Square[sizeY][sizeX];
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        String[] rows = bombs.split("\n|\r\n?");

        if (rows.length != sizeY) {
            throw new IllegalArgumentException(String.format("expected %d rows but parsed %", sizeY, rows.length));
        }

        for (int j = 0; j < sizeY; j++) {
            final String[] values = rows[j].split(" ");
            if (values.length != sizeX) {
                throw new IllegalArgumentException(
                        String.format("expected %d values in row but % found only %d", sizeX, j, values.length));
            }
            
            for(int i = 0; i < sizeX; i++) {
                int value = Integer.parseInt(values[i]);
                if (value == 1) {
                    board[j][i] = new Square(true);
                } else if (value == 0) {
                    board[j][i] = new Square(false);
                } else {
                    throw new IllegalArgumentException("value %d,%d in the grid provided is neither a 1 nor a 0");
                }
            }
        }
    }
    
    /**
     * Create a sizeX by sizeY board populated with bombs at random locations.
     * 
     * @param sizeX
     *            Size of the board along x-axis
     * @param sizeY
     *            Size of the board along y-axis
     */
    public Board(int sizeX, int sizeY) {
        //TODO placeholder
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        board = new Square[sizeY][sizeX];
        
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                board[j][i] = new Square(false);
            }
        }
    }

    /**
     * If x,y are out of bounds, the board's state remains unchanged. If x,y are
     * in bounds, dig square x,y if untouched, exploding its mine (if there is
     * one), otherwise leave the state as is.
     * <p>
     * Digging square x,y expands recursively through all neighboring squares if
     * none of those squares contain a bomb.
     * 
     * @param x
     *            the x-coordinate of the square in the board.
     * @param y
     *            the y-coordinate of the square in the board.
     * @return true if a mine exploded
     */
    public synchronized boolean dig(int x, int y) {
        if (!inBound(x, y) || board[y][x].state() != Square.State.Untouched) {
            return false;
        }
        final boolean exploded = board[y][x].dig();

        if (count(x, y) == 0) {
            dig(x - 1, y - 1);
            dig(x, y - 1);
            dig(x + 1, y - 1);
            dig(x + 1, y);
            dig(x + 1, y + 1);
            dig(x, y + 1);
            dig(x - 1, y + 1);
            dig(x - 1, y);
        }

        return exploded;
    }
    
    /**
     * If x,y are out of bounds, the boards' state remains unchanged. If x,y are
     * in bounds, flag square x,y if untouched, otherwise leave the state as is.
     * 
     * @param x
     *            the x-coordinate of the square in the board.
     * @param y
     *            the y-coordinate of the square in the board.
     */
    public synchronized void flag(int x, int y) {
        if (inBound(x, y)) {
            board[y][x].flag();
        }
    }

    /**
     * If x,y are out of bounds, the boards' state remains unchanged. If x,y are
     * in bounds, deflag square x,y if flagged, otherwise leave the state as is.
     * 
     * @param x
     *            the x-coordinate of the square in the board.
     * @param y
     *            the y-coordinate of the square in the board.
     */
    public synchronized void deflag(int x, int y) {
        if (inBound(x, y)) {
            board[y][x].deflag();
        }
    }

    /**
     * Returns a grid of newline-separated (\n) rows of space space-separated
     * characters, representing the board's state with exactly one character per
     * each square. The mapping of characters is as follows:
     * <ul>
     * <li>"-" denotes an untouched square.</li>
     * <li>"F" denotes a flagged square.</li>
     * <li>" " (space) denotes a dug square with 0 neighbors that have a bomb.
     * </li>
     * <li>an integer between 1 and 8 denotes a dug square with that many
     * neighbor that have a bomb.</li>
     * </ul>
     */
    @Override
    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < sizeY; j++) {
            for (int i = 0; i < sizeX; i++) {
                switch (board[j][i].state()) {
                case Dug:
                    final int count = count(i, j);
                    sb.append(count == 0 ? " " : count);
                    break;
                case Flagged:
                    sb.append("F");
                    break;
                case Untouched:
                    sb.append("-");
                    break;
                default:
                    break;
                }

                sb.append(i == sizeX - 1 ? "\n" : " ");
            }
        }
        
        sb.deleteCharAt(sb.length()-1); //Delete the last new-line char (\n)

        return sb.toString();
    }
    
    /**
     * @return Length of the board along x-axis--width.
     */
    public int sizeX() {
        return sizeX;
    }
    
    /**
     * @return Length of the board along y-axis--height.
     */
    public int sizeY() {
        return sizeY;
    }

}
