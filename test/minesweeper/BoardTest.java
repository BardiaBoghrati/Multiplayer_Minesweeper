/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * TODO: Description
 */
public class BoardTest {
    
    // TODO: Testing strategy
    //  A square may have 0, 1, 2, 3, 5, 8 neighbors
    //  A grid may be a single square, linear (one-dimensional along x or y),
    // rectangular (two dimensional with unequal size of x and y), square (two dimensional with equal sides).
    //  A dig on may expand through any of the untouched neighbors, we can categorize the direction of the expansion 
    // into left, right, top, bottom, top-left, top-right, bottom-left, bottom-right
    //  We can also partition a dig operation based on the distance of the expansion: 0 (no expansion), 1, 2, and so on
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    // TODO: Tests
    @Test
    public void testFlagUntouchedSqaure() {
        Board board = new Board(1, 1, "0\n");
        
        board.flag(0, 0);
        
        assertEquals("F\n", board.toString()); 
    }
    
    @Test
    public void testFlagFlaggedSqaure() {
        Board board = new Board(1, 1, "0\n");
        board.flag(0, 0);
        
        board.flag(0, 0);
        
        assertEquals("F\n", board.toString()); 
    }
    
    @Test
    public void testFlagDugSqaure() {
        Board board = new Board(1, 1, "0\n");
        board.dig(0, 0);
        
        board.flag(0, 0);
        
        assertEquals(" \n", board.toString()); 
    }
    
    @Test
    public void testDeflagUntouchedSqaure() {
        Board board = new Board(1, 1, "0\n");
        
        board.deflag(0, 0);
        
        assertEquals("-\n", board.toString()); 
    }
    
    @Test
    public void testDeflagFlaggedSqaure() {
        Board board = new Board(1, 1, "0\n");
        board.flag(0, 0);
        
        board.deflag(0, 0);
        
        assertEquals("-\n", board.toString()); 
    }
    
    @Test
    public void testDeflagDugSqaure() {
        Board board = new Board(1, 1, "0\n");
        board.dig(0, 0);
        
        board.deflag(0, 0);
        
        assertEquals(" \n", board.toString()); 
    }
    
    @Test
    public void testDigDuggedSqaure() {
        Board board = new Board(2, 1, "0 0\n");
        board.flag(1, 0);
        board.dig(0, 0);
        board.deflag(1, 0);
        
        board.dig(0, 0);
        
        assertEquals("  -\n", board.toString()); 
    }
    
    @Test
    public void testDigFlaggedSquare() {
        Board board = new Board(1, 1, "0\n");
        board.flag(0, 0);
        
        board.dig(0, 0);
        
        assertEquals("F\n", board.toString());  
    }
    
    @Test
    public void testDigSqaureNextToFlaggedMinedSquare() {
        Board board = new Board(3, 1, "1 0 0\n");
        board.flag(0, 0);
        
        board.dig(1, 0);
        
        assertEquals("F 1 -\n", board.toString());  
    }
    
    @Test
    public void testDigSqaureWithNoNeighbors() {
        Board board = new Board(1, 1, "0\n");
        
        board.dig(0, 0);
        
        assertEquals(" \n", board.toString());
    }
    
//    @Test
//    public void testDigSquareWithFlaggedNeighbors() {
//        Board board = new Board("0 0 0\n"+
//                                "0 0 0\n"+
//                                "0 0 0\n");
//        board.flag(0, 1);
//        board.flag(1, 0);
//        board.flag(1, 1);
//        
//        board.dig(2, 2);
//        
//        assertEquals("- F  \n"+
//                     "F F  \n"+
//                     "     \n", board.toString());
//        
//        board.deflag(1, 1);
//        
//        assertEquals("- F  \n"+
//                     "F -  \n"+
//                     "     \n", board.toString());
//        
//        board.dig(1, 1);
//        
//        assertEquals("  F  \n"+
//                     "F    \n"+
//                     "     \n", board.toString());
//    }
    
    @Test
    public void testDigSquareWithFlaggedNeighbors() {
        Board board;
        
        //Expansion walled in by flagged neighbors
        board = new Board(2, 3, "0 0\n"+
                                "0 0\n"+
                                "0 0\n");
        board.flag(0, 1);
        board.flag(1, 1);
        
        board.dig(0, 0);
        
        assertEquals("   \n"+
                     "F F\n"+
                     "- -\n", board.toString());
        
        //Expansion not walled in by flagged neighbors
        board = new Board(2, 3, "0 0\n"+
                                "0 0\n"+
                                "0 0\n");
        board.flag(1, 1);
        
        board.dig(0, 0);
        
        assertEquals("   \n"+
                     "  F\n"+
                     "   \n", board.toString());
    }
    
//    @Test
//    public void testDigSquareWithMinedNeighbor() {
//        Board board = new Board("1 0 0\n"+
//                                "0 0 0\n"+
//                                "0 0 0\n");
//        
//        board.dig(1, 1);
//        
//        assertEquals("- - -\n"+
//                     "- 1 -\n"+
//                     "- - -\n", board.toString());
//        
//        board.dig(2, 2);
//        
//        assertEquals("- 1  \n"+
//                     "1 1  \n"+
//                     "     \n", board.toString());
//    }
    
    @Test
    public void testDigSqaureDistanceFromMinedSquare() {
        Board board;
        
        //Dig square on top of a bomb
        board = new Board(3, 1, "1 0 0\n");
        
        board.dig(0, 0);
        
        assertEquals("     \n", board.toString());
        
        //Dig square one square to a bomb
        board = new Board(3, 1, "1 0 0\n");
        
        board.dig(1, 0);
        
        assertEquals("- 1 -\n", board.toString());
        
        //Dig square two squares from a bomb
        board = new Board(3, 1, "1 0 0\n");
        
        board.dig(2, 0);
        
        assertEquals("- 1  \n", board.toString());
    }
    
    @Test
    public void testDigHorizontalExpansion() {
        Board board = new Board(3, 1, "0 0 0\n");   

        board.dig(1, 0);
        
        assertEquals("     \n", board.toString());
    }
    
    @Test
    public void testDigVerticalExpansion() {
        Board board = new Board(1, 3, "0\n"+
                                      "0\n"+
                                      "0\n");   
        board.dig(0, 1);
        
        assertEquals(" \n"+
                     " \n"+
                     " \n", board.toString());
    }
    
    @Test
    public void testDigDiagonalExpansion() {
        Board board = new Board(3, 3, "0 0 0\n"+
                                      "0 0 0\n"+
                                      "0 0 0\n");   
        board.flag(1, 0);
        board.flag(1, 2);
        board.flag(0, 1);
        board.flag(2, 1);
        
        board.dig(1, 1);
        
        assertEquals("  F  \n"+
                     "F   F\n"+
                     "  F  \n", board.toString());
    }
    
    
    @Test
    public void testDigMinedSquares() {
        Board board = new Board(2, 2, "1 1\n"+
                                      "1 1\n");
        
        board.dig(0, 0);
        
        assertEquals("3 -\n"+
                     "- -\n", board.toString());
        
        board.dig(1, 1);
        
        assertEquals("2 -\n"+
                     "- 2\n", board.toString());
        
        board.dig(1, 0);
        
        assertEquals("1 1\n"+
                     "- 1\n", board.toString());
        
        board.dig(0, 1);
        
        assertEquals("   \n"+
                     "   \n", board.toString());
    }
    
//    @Test
//    public void testDigSquareWithDugNeighbors() {
//        Board board = new Board("1 1 0\n"+
//                                "1 1 0\n"+
//                                "0 0 0\n");
//        
//        board.dig(0, 1);
//        board.dig(1, 0);
//        board.dig(1, 1);
//        
//        assertEquals("- 1 -\n"+
//                     "1 1 -\n"+
//                     "- - -\n", board.toString());
//        
//        board.dig(0, 0);
//        
//        assertEquals("    -\n"+
//                     "    -\n"+
//                     "- - -\n", board.toString());
//    }
    
    @Test
    public void testDigSquareWithDugNeighbor() {
        Board board = new Board(3, 1, "1 1 0\n");
        board.dig(1, 0);
        
        board.dig(0, 0);
        
        assertEquals("    -\n", board.toString());
    }
    
    @Test
    public void testDigMiddleSquare() {
        Board board = new Board(3, 3, "0 0 0\n"+
                                      "0 0 0\n"+
                                      "0 0 0\n");
        
        board.dig(1, 1);
        
        assertEquals("     \n"+
                     "     \n"+
                     "     \n", board.toString());
    }
    
    @Test
    public void testDigBoundarySquares() {
        Board board = new Board(3, 3, "0 0 0\n"+
                                      "0 1 0\n"+
                                      "0 0 0\n");
        
        board.dig(0, 0);
        
        assertEquals("1 - -\n"+
                     "- - -\n"+
                     "- - -\n", board.toString());
        board.dig(1, 0);
        
        assertEquals("1 1 -\n"+
                     "- - -\n"+
                     "- - -\n", board.toString());
        board.dig(2, 0);
        
        assertEquals("1 1 1\n"+
                     "- - -\n"+
                     "- - -\n", board.toString());
        board.dig(2, 1);
        
        assertEquals("1 1 1\n"+
                     "- - 1\n"+
                     "- - -\n", board.toString());
        board.dig(2, 2);
        
        assertEquals("1 1 1\n"+
                     "- - 1\n"+
                     "- - 1\n", board.toString());
        board.dig(1, 2);
        
        assertEquals("1 1 1\n"+
                     "- - 1\n"+
                     "- 1 1\n", board.toString());
        
        board.dig(0, 2);
        
        assertEquals("1 1 1\n"+
                     "- - 1\n"+
                     "1 1 1\n", board.toString());
        
        board.dig(0, 1);
        
        assertEquals("1 1 1\n"+
                     "1 - 1\n"+
                     "1 1 1\n", board.toString());
    }
    
    @Test
    public void testDigWithFlagInTheMiddle() {
        Board board = new Board(3, 3, "0 0 0\n"+
                                      "0 0 0\n"+
                                      "0 0 0\n");
        board.flag(1, 1);
        
        board.dig(2, 2);
        
        assertEquals("     \n"+
                     "  F  \n"+
                     "     \n", board.toString());
    }
    
    @Test
    public void testDigAxisDefinition() {
        Board board = new Board(2, 1, "1 0\n");
        
        board.dig(1, 0);
        
        assertEquals("- 1\n", board.toString());
    }
    
    @Test
    public void testFlagAxisDefinition() {
        Board board = new Board(2, 1, "0 0\n");
        
        board.flag(1, 0);
        
        assertEquals("- F\n", board.toString());
    }
    
    @Test
    public void testDeflagAxisDefinition() {
        Board board = new Board(2, 1, "0 0\n");
        board.flag(1, 0);
        
        board.deflag(1, 0);
        
        assertEquals("- -\n", board.toString());
    }
    
    @Test
    public void testDigOutOfBound() {
        Board board = new Board(1, 1, "0\n");
        
        board.dig(-1, 0);
        
        assertEquals("-\n", board.toString());
        
        board.dig(1, 0);
        
        assertEquals("-\n", board.toString());
        
        board.dig(0, -1);
        
        assertEquals("-\n", board.toString());
        
        board.dig(0, 1);
        
        assertEquals("-\n", board.toString());
    }
    
    @Test
    public void testFlagOutOfBound() {
        Board board = new Board(1, 1, "0\n");
        
        board.flag(-1, 0);
        
        assertEquals("-\n", board.toString());
        
        board.flag(1, 0);
        
        assertEquals("-\n", board.toString());
        
        board.flag(0, -1);
        
        assertEquals("-\n", board.toString());
        
        board.flag(0, 1);
        
        assertEquals("-\n", board.toString());
    }
    
    @Test
    public void testDeflagOutOfBound() {
        Board board = new Board(1, 1, "0\n");
        board.flag(0, 0);
        
        board.deflag(-1, 0);
        
        assertEquals("F\n", board.toString());
        
        board.deflag(1, 0);
        
        assertEquals("F\n", board.toString());
        
        board.deflag(0, -1);
        
        assertEquals("F\n", board.toString());
        
        board.deflag(0, 1);
        
        assertEquals("F\n", board.toString());
    }
    
    @Test
    public void testBoardLineTermination() {
        Board board = new Board(2, 4, "0 0\n"+
                                      "0 0\r"+
                                      "0 0\r\n"+
                                      "0 0\n");
        
        assertEquals("- -\n"+
                     "- -\n"+
                     "- -\n"+
                     "- -\n", board.toString());
    }
    
    @Test
    public void testMineExplosion() {
        Board board;
        
        board = new Board(1, 1, "0\n");
        board.flag(0, 0);
        
        assertFalse(board.dig(0, 0)); //Dig flagged square with no mine
        
        board.deflag(0, 0);
        
        assertFalse(board.dig(0, 0)); //Dig untouched square with no mine
        
        board = new Board(1, 1, "1\n");
        board.flag(0, 0);
        
        assertFalse(board.dig(0, 0)); //Dig flagged mined square
        
        board.deflag(0, 0);
        
        assertTrue(board.dig(0, 0)); //Dig untouched mined square
        assertFalse(board.dig(0, 0)); //Dig square with exploded mine
    }
    
    @Test
    public void testDigExpandAfterExplosion() {
        Board board = new Board(5, 1, "0 0 1 0 1\n");
        
        assertTrue(board.dig(2, 0));
        assertEquals("      1 -\n", board.toString());

    }
}
