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

    // TODO: Abstraction function, rep invariant, rep exposure, thread safety

    // TODO: Specify, test, and implement in problem 2
    
    /**
     * Initialize a board from a bombs grid. A board of equals size to bombs
     * will be created with all squares untouched. An square x,y will contain a
     * bomb if only if bombs contains a 1 in the same location.
     * 
     * @param bombs
     *            A grid of newline-separated rows of space-separated 1s and 0s
     *            representing the location of bombs in a board. A location
     *            contains a bomb if and only if it contains a 1.
     */
    public Board(String bombs) {
        
    }

    /**
     * If x,y are out of bounds, the board's state remains unchanged. If x,y are
     * in bounds, dig square x,y if untouched, otherwise leave the state as is.
     * <p>
     * Digging square x,y expands recursively through all neighboring squares if
     * none of those squares contain a bomb.
     * <p>
     * A dug square will have a count of it's neighboring squares with bombs.
     * 
     * @param x
     *            the x-coordinate of the square in the board.
     * @param y
     *            the y-coordinate of the square in the board.
     */
    public void dig(int x, int y) {
        throw new RuntimeException("not implemented");
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
    public void flag(int x, int y) {
        throw new RuntimeException("not implemented");
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
        throw new RuntimeException("not implemented");
    }

    /**
     * Returns a grid of newline-separated rows of space space-separated
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
        throw new RuntimeException("not implemented");
    }

}
