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
    // Abstract function:
    // Rep Exposure:
    
    // TODO: Specify, test, and implement in problem 2
    
    private enum SquareState {
        Untouched,
        Flagged,
        Dug
    }

    private class Square {
        private int x;
        private int y;
        private boolean mined;
        private SquareState state;
        
        Square(int x, int y, boolean mined) {
            this.x = x;
            this.y = y;
            this.mined = mined;
            this.state = SquareState.Untouched;
        }
        
        /**
         * @return x-coordinate of this square the in board
         */
        int x() {
            return x;
        }
        
        /**
         * @return y-coordinate of this square in the board
         */
        int y() {
            return y;
        }
        
        /**
         * Digs this square exploding its mine (if there is one)
         * 
         * @return true if this dig exploded a mine.
         */
        boolean dig() {
            if (state == SquareState.Untouched) {
                state = SquareState.Dug;
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
            if(state == SquareState.Untouched) {
                state = SquareState.Flagged;
            }
        }
        
        /**
         * Deflag this square
         */
        void deflag() {
            if(state == SquareState.Flagged) {
                state = SquareState.Untouched;
            }
        }
        
        /**
         * @return The sate of this square
         */
        SquareState state() {
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
    private boolean mined(int x, int y){
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
                    board[j][i] = new Square(i, j, true);
                } else if (value == 0) {
                    board[j][i] = new Square(i, j, false);
                } else {
                    throw new IllegalArgumentException("value %d,%d in the grid provided is neither a 1 nor a 0");
                }
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
    public boolean dig(int x, int y) {
        if (!inBound(x, y) || board[y][x].state() != SquareState.Untouched) {
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
    
//    public void dig(int x, int y) {
//        try {
//            if(board[x][y].dig()) {
//                expand(x,y);  
//            }
//        } catch (ExplosionException ee) {
//            expand(x,y);
//            throw ee;
//        } catch (IndexOutOfBoundsExcpetion ie) {
//            return;
//        }
//    }

    /**
     * If x,y are out of bounds, the boards' state remains unchanged. If x,y are
     * in bounds, flag square x,y if untouched, otherwise leave the state as is.
     * 
     * @param x
     *            the x-coordinate of the square in the board.
     * @param y
     *            the y-coordinate of the square in the board.
     */
    public void flag(int x, int y) {
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
    public void deflag(int x, int y) {
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
    public String toString() {
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

        return sb.toString();
    }

}
