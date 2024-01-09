/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        
        assertEquals("F", board.toString()); 
    }
    
    @Test
    public void testFlagFlaggedSqaure() {
        Board board = new Board(1, 1, "0\n");
        board.flag(0, 0);
        
        board.flag(0, 0);
        
        assertEquals("F", board.toString()); 
    }
    
    @Test
    public void testFlagDugSqaure() {
        Board board = new Board(1, 1, "0\n");
        board.dig(0, 0);
        
        board.flag(0, 0);
        
        assertEquals(" ", board.toString()); 
    }
    
    @Test
    public void testDeflagUntouchedSqaure() {
        Board board = new Board(1, 1, "0\n");
        
        board.deflag(0, 0);
        
        assertEquals("-", board.toString()); 
    }
    
    @Test
    public void testDeflagFlaggedSqaure() {
        Board board = new Board(1, 1, "0\n");
        board.flag(0, 0);
        
        board.deflag(0, 0);
        
        assertEquals("-", board.toString()); 
    }
    
    @Test
    public void testDeflagDugSqaure() {
        Board board = new Board(1, 1, "0\n");
        board.dig(0, 0);
        
        board.deflag(0, 0);
        
        assertEquals(" ", board.toString()); 
    }
    
    @Test
    public void testDigDuggedSqaure() {
        Board board = new Board(2, 1, "0 0\n");
        board.flag(1, 0);
        board.dig(0, 0);
        board.deflag(1, 0);
        
        board.dig(0, 0);
        
        assertEquals("  -", board.toString()); 
    }
    
    @Test
    public void testDigFlaggedSquare() {
        Board board = new Board(1, 1, "0\n");
        board.flag(0, 0);
        
        board.dig(0, 0);
        
        assertEquals("F", board.toString());  
    }
    
    @Test
    public void testDigSqaureNextToFlaggedMinedSquare() {
        Board board = new Board(3, 1, "1 0 0\n");
        board.flag(0, 0);
        
        board.dig(1, 0);
        
        assertEquals("F 1 -", board.toString());  
    }
    
    @Test
    public void testDigSqaureWithNoNeighbors() {
        Board board = new Board(1, 1, "0\n");
        
        board.dig(0, 0);
        
        assertEquals(" ", board.toString());
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
                     "- -", board.toString());
        
        //Expansion not walled in by flagged neighbors
        board = new Board(2, 3, "0 0\n"+
                                "0 0\n"+
                                "0 0\n");
        board.flag(1, 1);
        
        board.dig(0, 0);
        
        assertEquals("   \n"+
                     "  F\n"+
                     "   ", board.toString());
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
        
        assertEquals("     ", board.toString());
        
        //Dig square one square to a bomb
        board = new Board(3, 1, "1 0 0\n");
        
        board.dig(1, 0);
        
        assertEquals("- 1 -", board.toString());
        
        //Dig square two squares from a bomb
        board = new Board(3, 1, "1 0 0\n");
        
        board.dig(2, 0);
        
        assertEquals("- 1  ", board.toString());
    }
    
    @Test
    public void testDigHorizontalExpansion() {
        Board board = new Board(3, 1, "0 0 0\n");   

        board.dig(1, 0);
        
        assertEquals("     ", board.toString());
    }
    
    @Test
    public void testDigVerticalExpansion() {
        Board board = new Board(1, 3, "0\n"+
                                      "0\n"+
                                      "0\n");   
        board.dig(0, 1);
        
        assertEquals(" \n"+
                     " \n"+
                     " ", board.toString());
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
                     "  F  ", board.toString());
    }
    
    
    @Test
    public void testDigMinedSquares() {
        Board board = new Board(2, 2, "1 1\n"+
                                      "1 1\n");
        
        board.dig(0, 0);
        
        assertEquals("3 -\n"+
                     "- -", board.toString());
        
        board.dig(1, 1);
        
        assertEquals("2 -\n"+
                     "- 2", board.toString());
        
        board.dig(1, 0);
        
        assertEquals("1 1\n"+
                     "- 1", board.toString());
        
        board.dig(0, 1);
        
        assertEquals("   \n"+
                     "   ", board.toString());
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
        
        assertEquals("    -", board.toString());
    }
    
    @Test
    public void testDigMiddleSquare() {
        Board board = new Board(3, 3, "0 0 0\n"+
                                      "0 0 0\n"+
                                      "0 0 0\n");
        
        board.dig(1, 1);
        
        assertEquals("     \n"+
                     "     \n"+
                     "     ", board.toString());
    }
    
    @Test
    public void testDigBoundarySquares() {
        Board board = new Board(3, 3, "0 0 0\n"+
                                      "0 1 0\n"+
                                      "0 0 0\n");
        
        board.dig(0, 0);
        
        assertEquals("1 - -\n"+
                     "- - -\n"+
                     "- - -", board.toString());
        board.dig(1, 0);
        
        assertEquals("1 1 -\n"+
                     "- - -\n"+
                     "- - -", board.toString());
        board.dig(2, 0);
        
        assertEquals("1 1 1\n"+
                     "- - -\n"+
                     "- - -", board.toString());
        board.dig(2, 1);
        
        assertEquals("1 1 1\n"+
                     "- - 1\n"+
                     "- - -", board.toString());
        board.dig(2, 2);
        
        assertEquals("1 1 1\n"+
                     "- - 1\n"+
                     "- - 1", board.toString());
        board.dig(1, 2);
        
        assertEquals("1 1 1\n"+
                     "- - 1\n"+
                     "- 1 1", board.toString());
        
        board.dig(0, 2);
        
        assertEquals("1 1 1\n"+
                     "- - 1\n"+
                     "1 1 1", board.toString());
        
        board.dig(0, 1);
        
        assertEquals("1 1 1\n"+
                     "1 - 1\n"+
                     "1 1 1", board.toString());
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
                     "     ", board.toString());
    }
    
    @Test
    public void testDigAxisDefinition() {
        Board board = new Board(2, 1, "1 0\n");
        
        board.dig(1, 0);
        
        assertEquals("- 1", board.toString());
    }
    
    @Test
    public void testFlagAxisDefinition() {
        Board board = new Board(2, 1, "0 0\n");
        
        board.flag(1, 0);
        
        assertEquals("- F", board.toString());
    }
    
    @Test
    public void testDeflagAxisDefinition() {
        Board board = new Board(2, 1, "0 0\n");
        board.flag(1, 0);
        
        board.deflag(1, 0);
        
        assertEquals("- -", board.toString());
    }
    
    @Test
    public void testDigOutOfBound() {
        Board board = new Board(1, 1, "0\n");
        
        board.dig(-1, 0);
        
        assertEquals("-", board.toString());
        
        board.dig(1, 0);
        
        assertEquals("-", board.toString());
        
        board.dig(0, -1);
        
        assertEquals("-", board.toString());
        
        board.dig(0, 1);
        
        assertEquals("-", board.toString());
    }
    
    @Test
    public void testFlagOutOfBound() {
        Board board = new Board(1, 1, "0\n");
        
        board.flag(-1, 0);
        
        assertEquals("-", board.toString());
        
        board.flag(1, 0);
        
        assertEquals("-", board.toString());
        
        board.flag(0, -1);
        
        assertEquals("-", board.toString());
        
        board.flag(0, 1);
        
        assertEquals("-", board.toString());
    }
    
    @Test
    public void testDeflagOutOfBound() {
        Board board = new Board(1, 1, "0\n");
        board.flag(0, 0);
        
        board.deflag(-1, 0);
        
        assertEquals("F", board.toString());
        
        board.deflag(1, 0);
        
        assertEquals("F", board.toString());
        
        board.deflag(0, -1);
        
        assertEquals("F", board.toString());
        
        board.deflag(0, 1);
        
        assertEquals("F", board.toString());
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
                     "- -", board.toString());
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
        assertEquals("      1 -", board.toString());

    }
    
    @Test
    public void testBoardSize() {
        Board board;
        
        board = new Board(1, 1, "0\n");
        
        assertEquals(1, board.sizeX());
        assertEquals(1, board.sizeY());
        assertEquals("-", board.toString());
        
        board = new Board(2, 1, "0 0\n");
        
        assertEquals(2, board.sizeX());
        assertEquals(1, board.sizeY());
        assertEquals("- -", board.toString());
        
        board = new Board(1, 2, "0\n"+
                                "0\n");
        
        assertEquals(1, board.sizeX());
        assertEquals(2, board.sizeY());
        assertEquals("-\n"+
                     "-", board.toString());
        
        board = new Board(2, 2, "0 0\n"+
                                "0 0\n");
        
        assertEquals(2, board.sizeX());
        assertEquals(2, board.sizeY());
        assertEquals("- -\n"+
                     "- -", board.toString());

    }
    
    /**
     * Run test n times repeatedly
     * 
     * @param test a test to run repeatedly
     * @param n number of times to run the test
     */
    private static void repeat(Runnable test, int n) {
        for (int i =0; i <= n; i++) {
            test.run();
        }
    }
    
    
    @Test
    public void testConcurrentDigFlag() {
        repeat(() -> concurrentDigFlag(), 10000);
    }
    
//    private void concurrentDigFlag() {
//        try {
//            Board board = new Board(1, 1, "1\n");
//            
//            Thread t1 = new Thread(() -> board.dig(0, 0));
//            Thread t2 = new Thread(() -> board.flag(0, 0));
//
//            t1.start();
//            t2.start();
//            t1.join();
//            t2.join();
//            
//            assertTrue("Unexpected outcome", board.toString().equals(" \n") && !board.mined(0, 0)
//                    || board.toString().equals("F\n") && board.mined(0, 0));
//        } catch (InterruptedException ie) {
//            throw new AssertionError("Test interrupted");
//        }
//    }
    
//    private void concurrentDigFlag() {
//        try {
//            Board board = new Board(2, 1, "1 0\n");
//            board.dig(1, 0);
//            
//            Thread t1 = new Thread(() -> board.dig(0, 0));
//            Thread t2 = new Thread(() -> board.flag(0, 0));
//
//            t1.start();
//            t2.start();
//            t1.join();
//            t2.join();
//            
//            assertTrue(String.format("Unexpected outcome:%n'%s'", board), 
//                    board.toString().equals("   \n") || board.toString().equals("F 1\n"));
//        } catch (InterruptedException ie) {
//            throw new AssertionError("Test interrupted");
//        }
//    }
    
    private void concurrentDigFlag() {
        try {
            Board board = new Board(1, 1, "1\n");
            List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
            
            Thread t1 = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        final boolean explosion = board.dig(0, 0);

                        assertTrue("T1-Unexpected outcome:\n" +
                                    "State: " + board + "\n" +
                                    "Mined: " + !explosion, 
                                   board.toString().equals(" ") && explosion
                                || board.toString().equals("F") && !explosion);
                    } catch (Throwable t) {
                        exceptions.add(t);
                    }
                }
                
            });
            
            Thread t2 = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        board.flag(0, 0);

                        assertTrue("T2-Unexpected outcome:\n" + board,
                                board.toString().equals(" ") 
                             || board.toString().equals("F"));
                    } catch (Throwable t) {
                        exceptions.add(t);
                    }
                }
                
            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();
            
            assertTrue("Exceptions thrown: " + exceptions, exceptions.isEmpty());
        } catch (InterruptedException ie) {
            throw new AssertionError("Test interrupted");
        }
    }
    
    @Test
    public void testConcurrentDigLook() {
        repeat(() -> concurrentDigLook(), 10000);
    }
    
    public void concurrentDigLook() {
        try {
            Board board = new Board(3, 3, "0 0 0\n"+
                                          "0 0 0\n"+
                                          "0 0 0\n");
            List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());
            
            Thread t1 = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        board.dig(0, 0);

                        assertEquals("T1-Unexpected outcome:\n" + board,
                                     "     \n"+
                                     "     \n"+
                                     "     ", board.toString());
                    } catch (Throwable t) {
                        exceptions.add(t);
                    }
                }
                
            });
            
            Thread t2 = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        assertTrue("T2-Unexpected outcome:\n" + board,
                                   board.toString().equals("- - -\n"+
                                                           "- - -\n"+
                                                           "- - -")
                                || board.toString().equals("     \n"+
                                                           "     \n"+
                                                           "     "));
                    } catch (Throwable t) {
                        exceptions.add(t);
                    }
                }
                
            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();
            
            assertTrue("Exceptions thrown: " + exceptions, exceptions.isEmpty());
        } catch (InterruptedException ie) {
            throw new AssertionError("Test interrupted");
        }
    }
}
