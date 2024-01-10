/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import org.junit.Test;

/**
 * TODO
 */
public class MinesweeperServerTest {
    
    // TODO
    private static class MinesweeperClient {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        
        /**
         * Create a client connected to a MinesweeperServer on a loopback
         * address.
         * 
         * @param serverThread
         *            thread on which the server is running
         * @param port
         *            port number on which the server is listening
         * @throws IOException
         *             if connection to the server fails
         */
        MinesweeperClient(Thread serverThread, int port) throws IOException {
            final int MAX_CONNECTION_ATTEMPTS = 10;
            int attempts = 0;
            while (true) {
                try {
                    socket = new Socket((String) null, port);
                    socket.setSoTimeout(3000);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    return;
                } catch (ConnectException ce) {
                    if (!serverThread.isAlive()) {
                        throw new IOException("Server thread not running");
                    }
                    if (++attempts > MAX_CONNECTION_ATTEMPTS) {
                        throw new IOException("Exceeded max connection attempts", ce);
                    }
                    try {
                        Thread.sleep(attempts * 10);
                    } catch (InterruptedException ie) {
                    }
                }
            }
        }
        
        /**
         * Unbuffered write of a message to the client socket's output stream.
         * 
         * @param message
         *            message to be written
         */
        void write(String message) {
            out.print(message);
            out.flush();
        }
        
        /**
         * Read a line of message from client socket's input stream.
         * 
         * @return return message line (excluding any line terminations), or
         *         null if reached end of stream.
         * @throws IOException
         *             if an I/O error occurs
         */
        String readln() throws IOException {
            return in.readLine();
        }
        
        /**
         * Terminates this client releasing all its resources--socket and
         * streams.
         * 
         * @throws IOException
         *             if failed to terminate the client. Failure in termination
         *             may leave client in a partially terminated state.
         */
        void terminate() throws IOException {
            in.close();
            out.close();
            socket.close();
        }
    }
    
    @Test
    public void testInvalidMessageType() throws IOException {
        MinesweeperServer server = new MinesweeperServer(0, true, 10, 10);
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        client.write("view\n");
        client.write("bye\n");

        assertTrue("Expected HELLO message", client.readln().startsWith("Welcome"));
        assertEquals(MinesweeperServer.HELP_MESSAGE, client.readln());
        assertEquals("Expected end of stream", null, client.readln());
        
        server.terminate();
        client.terminate();
    }
    
    @Test
    public void testInvalidMessageArgument() throws IOException {
        MinesweeperServer server = new MinesweeperServer(0, true, 10, 10);
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        client.write("dig x 0\n");
        client.write("dig 0 y\n");
        client.write("bye\n");

        assertTrue("Expected HELLO message", client.readln().startsWith("Welcome"));
        assertEquals(MinesweeperServer.HELP_MESSAGE, client.readln());
        assertEquals(MinesweeperServer.HELP_MESSAGE, client.readln());
        assertEquals("Expected end of stream", null, client.readln());
        
        server.terminate();
        client.terminate();
    }
    
    @Test
    public void testInvalidNumberOfArguments() throws IOException {
        MinesweeperServer server = new MinesweeperServer(0, true, 10, 10);
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        client.write("dig\n");
        client.write("flag 0\n");
        client.write("deflag 0 0 0\n");
        client.write("bye\n");

        assertTrue("Expected HELLO message", client.readln().startsWith("Welcome"));
        assertEquals(MinesweeperServer.HELP_MESSAGE, client.readln());
        assertEquals(MinesweeperServer.HELP_MESSAGE, client.readln());
        assertEquals(MinesweeperServer.HELP_MESSAGE, client.readln());
        assertEquals("Expected end of stream", null, client.readln());
        
        server.terminate();
        client.terminate();
    }
    
    @Test
    public void testCaseSensitivity() throws IOException {
        MinesweeperServer server = new MinesweeperServer(0, true, 10, 10);
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        client.write("Look\n");
        client.write("bye\n");

        assertTrue("Expected HELLO message", client.readln().startsWith("Welcome"));
        assertEquals(MinesweeperServer.HELP_MESSAGE, client.readln());
        assertEquals("Expected end of stream", null, client.readln());
        
        server.terminate();
        client.terminate();
    }
    
    @Test
    public void testTooManySpacesBetweenArguments() throws IOException {
        MinesweeperServer server = new MinesweeperServer(0, true, 10, 10);
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        client.write("dig  0 0\n");
        client.write("flag 0  0\n");
        client.write("deflag 0 0 \n");
        client.write("bye\n");

        assertTrue("Expected HELLO message", client.readln().startsWith("Welcome"));
        assertEquals(MinesweeperServer.HELP_MESSAGE, client.readln());
        assertEquals(MinesweeperServer.HELP_MESSAGE, client.readln());
        assertEquals(MinesweeperServer.HELP_MESSAGE, client.readln());
        assertEquals("Expected end of stream", null, client.readln());
        
        server.terminate();
        client.terminate();
    }
    
    @Test
    public void testDifferentLineTerminations() throws IOException {
        /*
         * Board File:
         * 1 1
         * 0
         */
        MinesweeperServer server = new MinesweeperServer(0, true, new File("boards/1x1.txt"));
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        client.write("look\n");
        client.write("look\r");
        client.write("look\r\n");
        client.write("bye\n");

        assertTrue("Expected HELLO message", client.readln().startsWith("Welcome"));
        assertEquals("-", client.readln());
        assertEquals("-", client.readln());
        assertEquals("-", client.readln());
        assertEquals("Expected end of stream", null, client.readln());
        
        server.terminate();
        client.terminate();
    }
    
    @Test
    public void testOutOfBoundCoordinates() throws IOException {
        MinesweeperServer server = new MinesweeperServer(0, true, new File("boards/1x1.txt"));
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        client.write("dig -1 0\n");
        client.write("flag 1 0\n");
        client.write("deflag 0 -1\n");
        client.write("dig 0 1\n");
        client.write("bye\n");

        assertTrue("Expected HELLO message", client.readln().startsWith("Welcome"));
        assertEquals("-", client.readln());
        assertEquals("-", client.readln());
        assertEquals("-", client.readln());
        assertEquals("-", client.readln());
        assertEquals("Expected end of stream", null, client.readln());
        
        server.terminate();
        client.terminate();
    }
    
    @Test
    public void testHelloMessage() throws IOException {
        /*
         * Board File:
         * 2 1
         * 0 0
         */
        MinesweeperServer server = new MinesweeperServer(0, true, new File("boards/2x1.txt"));
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();

        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());

        assertEquals("expected HELLO message", String.format(MinesweeperServer.HELLO_MESSAGE_FORMAT, 2, 1, 1),
                client.readln());

        server.terminate();
        client.terminate();
    }
    
    @Test
    public void testByeMessage() throws IOException {
        MinesweeperServer server = new MinesweeperServer(0, true, 10, 10);
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());

        assertTrue("Expected HELLO message", client.readln().startsWith("Welcome"));
        
        client.write("bye\n");
        client.write("look\n");
        
        assertEquals("Expected end of stream", null, client.readln());
        
        server.terminate();
        client.terminate();
    }
    
    @Test
    public void testLookMessage() throws IOException {
        /**
         * 2 2
         * 0 0
         * 0 0
         */
        MinesweeperServer server = new MinesweeperServer(0, true, new File("boards/2x2.txt"));
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        client.write("look\n");
        client.write("bye\n");
        
        assertTrue("expected HELLO message", client.readln().startsWith("Welcome"));
        
        assertEquals("- -", client.readln());
        assertEquals("- -", client.readln());
        assertEquals("expected end of stream", null, client.readln());
        
        
        server.terminate();
        client.terminate();
    }
    
    @Test
    public void testHelpMessage() throws IOException {
        MinesweeperServer server = new MinesweeperServer(0, true, 10, 10);
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        client.write("help\n");
        client.write("bye\n");
        
        assertTrue("expected HELLO message", client.readln().startsWith("Welcome"));
        assertEquals(MinesweeperServer.HELP_MESSAGE, client.readln());
        assertEquals("expected end of stream", null, client.readln());
        
        
        server.terminate();
        client.terminate();  
    }
    
    @Test
    public void testFlagMessage() throws IOException {
        /*
         * 2 2
         * 1 0
         * 0 1
         */
        MinesweeperServer server = new MinesweeperServer(0, true, new File("boards/2x2-(0,0)-(1,1).txt"));
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        assertTrue("expected HELLO message", client.readln().startsWith("Welcome"));
        
        client.write("dig 0 0\n");
        assertEquals(MinesweeperServer.BOOM_MESSAGE, client.readln());
        
        client.write("flag 0 0\n"); //flag a dug square
        assertEquals("1 -", client.readln());
        assertEquals("- -", client.readln());
        
        client.write("flag 1 1\n"); //flag a mined square
        assertEquals("1 -", client.readln());
        assertEquals("- F", client.readln());
        
        client.write("flag 1 0\n"); //flag an untouched square without a mine
        assertEquals("1 F", client.readln());
        assertEquals("- F", client.readln());
        
        client.write("flag 0 1\n"); //flag an untouched square without a mine
        assertEquals("1 F", client.readln());
        assertEquals("F F", client.readln());
        
        client.write("flag 1 1\n"); //flag a flagged square
        assertEquals("1 F", client.readln());
        assertEquals("F F", client.readln());
        
        client.write("bye\n");
        assertEquals("Expected end of stream", null, client.readln());
        
        server.terminate();
        client.terminate(); 
    }
    
    @Test
    public void testDeflagMessage() throws IOException {
        /*
         * 2 2
         * 1 0
         * 0 1
         */
        MinesweeperServer server = new MinesweeperServer(0, true, new File("boards/2x2-(0,0)-(1,1).txt"));
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        assertTrue("expected HELLO message", client.readln().startsWith("Welcome"));
        
        client.write("dig 0 0\n");
        client.write("flag 1 1\n");
        client.write("flag 1 0\n");
        client.write("flag 0 1\n");
        
        assertEquals(MinesweeperServer.BOOM_MESSAGE, client.readln());
        
        assertEquals("1 -", client.readln());
        assertEquals("- F", client.readln());
        
        assertEquals("1 F", client.readln());
        assertEquals("- F", client.readln());
        
        assertEquals("1 F", client.readln());
        assertEquals("F F", client.readln());
        
        client.write("deflag 0 0\n"); //deflag a dug square
        assertEquals("1 F", client.readln());
        assertEquals("F F", client.readln());
        
        client.write("deflag 1 1\n"); //deflag a flagged mined square
        assertEquals("1 F", client.readln());
        assertEquals("F -", client.readln());
        
        client.write("deflag 1 1\n"); //deflag an untouched square
        assertEquals("1 F", client.readln());
        assertEquals("F -", client.readln());
        
        client.write("deflag 1 0\n"); //deflag a flagged square without a mine
        assertEquals("1 -", client.readln());
        assertEquals("F -", client.readln());
        
        client.write("deflag 0 1\n");
        assertEquals("1 -", client.readln());
        assertEquals("- -", client.readln());
        
        client.write("bye\n");
        assertEquals("Expected end of stream", null, client.readln());
        
        server.terminate();
        client.terminate(); 
    }
    
    @Test
    public void testDigMessage() throws IOException {
        /*
         * 1 0 0
         * 0 0 0
         * 0 0 0
         */
        MinesweeperServer server = new MinesweeperServer(0, true, new File("boards/3x3-(0,0).txt"));
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        assertTrue("expected HELLO message", client.readln().startsWith("Welcome"));
        
        client.write("flag 0 0\n"); client.readln(); client.readln(); client.readln();
        client.write("flag 1 1\n"); client.readln(); client.readln(); client.readln();
        client.write("flag 1 2\n"); client.readln(); client.readln(); client.readln();
        client.write("flag 2 1\n"); client.readln(); client.readln(); client.readln();
        client.write("flag 2 2\n"); client.readln(); client.readln(); client.readln();
        /*
         * F - -
         * - F F
         * - F F
         */
        client.write("dig 0 0\n"); //dig a flagged mined square
        assertEquals("F - -", client.readln());
        assertEquals("- F F", client.readln());
        assertEquals("- F F", client.readln());
        
        client.write("deflag 0 0\n"); client.readln(); client.readln(); client.readln();
        /*
         * - - -
         * - F F
         * - F F
         */
        client.write("dig 2 0\n"); //dig a square one square away from a mine
        assertEquals("- 1  ", client.readln());
        assertEquals("- F F", client.readln());
        assertEquals("- F F", client.readln());
        
        client.write("dig 0 1\n"); //dig a square next to a mine
        assertEquals("- 1  ", client.readln());
        assertEquals("1 F F", client.readln());
        assertEquals("- F F", client.readln());
        
        client.write("flag 0 2\n"); client.readln(); client.readln(); client.readln();
        client.write("deflag 1 1\n"); client.readln(); client.readln(); client.readln();
        /*
         * - 1 0
         * 1 - F
         * F F F
         */
        client.write("dig 0 0\n"); //dig a mined square
        assertEquals(MinesweeperServer.BOOM_MESSAGE, client.readln());
        
        client.write("look\n");
        assertEquals("     ", client.readln());
        assertEquals("    F", client.readln());
        assertEquals("F F F", client.readln());
        
        client.write("deflag 1 2\n"); client.readln(); client.readln(); client.readln();
        /*
         * 0 0 0
         * 0 0 F
         * F - F
         */
        client.write("dig 1 2\n");
        assertEquals("     ", client.readln());
        assertEquals("    F", client.readln());
        assertEquals("F   F", client.readln());
        
        client.write("deflag 0 2\n"); client.readln(); client.readln(); client.readln();
        client.write("deflag 2 1\n"); client.readln(); client.readln(); client.readln();
        /*
         * 0 0 0
         * 0 0 -
         * - 0 F
         */
        client.write("dig 2 2\n"); //dig a flagged square
        assertEquals("     ", client.readln());
        assertEquals("    -", client.readln());
        assertEquals("-   F", client.readln());
        
        client.write("deflag 2 2\n"); client.readln(); client.readln(); client.readln();
        /*
         * 0 0 0
         * 0 0 -
         * - 0 -
         */
        client.write("dig 1 2\n"); //dig a dug square
        assertEquals("     ", client.readln());
        assertEquals("    -", client.readln());
        assertEquals("-   -", client.readln());
        
        client.write("dig 2 2\n"); //a dig cannot expand through dug squares
        assertEquals("     ", client.readln());
        assertEquals("     ", client.readln());
        assertEquals("-    ", client.readln());
        
    }
    
//    @Test
//    public void testDigLateralExpansion() throws IOException {
//        /*
//         * 0 0 0
//         * 0 0 0
//         * 0 0 0
//         */
//        MinesweeperServer server = new MinesweeperServer(0, true, new File("boards/...."));
//        Thread serverThread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try {
//                    server.serve();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        serverThread.start();
//        
//        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
//        
//        assertTrue("expected HELLO message", client.readln().startsWith("Welcome"));
//        
//        client.write("flag 2 0\n");
//        client.write("flag 0 1\n");
//        client.write("flag 1 1\n");
//        client.write("flag 2 1\n");
//        client.write("flag 0 2\n");
//        client.write("flag 1 2\n");
//        client.write("flag 2 2\n");
//        /*
//         * - - F
//         * F F F
//         * F F F
//         */
//        client.write("dig 0 0\n");//expand right
//        
//        assertEquals("    F", client.readln());
//        assertEquals("F F F", client.readln());
//        assertEquals("F F F", client.readln());
//        
//        client.write("deflag 2 0\n");
//        client.write("deflag 2 1\n");
//        /*
//         * 0 0 -
//         * F F -
//         * F F F
//         */
//        client.write("dig 2 0\n");//expand down
//        
//        assertEquals("     ", client.readln());
//        assertEquals("F F  ", client.readln());
//        assertEquals("F F F", client.readln());
//        
//        client.write("deflag 2 2\n");
//        client.write("deflag 1 2\n");
//        /*
//         * 0 0 0
//         * F F 0
//         * F - -
//         */
//        client.write("dig 2 2\n");//expand left
//        
//        assertEquals("     ", client.readln());
//        assertEquals("F F  ", client.readln());
//        assertEquals("F    ", client.readln());
//        
//        client.write("deflag 0 1\n");
//        client.write("deflag 0 2\n");
//        /*
//         * 0 0 0
//         * - F 0
//         * - 0 0
//         */
//        client.write("dig 0 2\n");//expand up
//        
//        assertEquals("     ", client.readln());
//        assertEquals("  F  ", client.readln());
//        assertEquals("     ", client.readln());
//    }
    
    @Test(expected = RuntimeException.class)
    public void testBoardFileMissingSize() throws IOException {
        new MinesweeperServer(0, true, new File("boards/missing-size.txt"));
    }
    
    @Test(expected = RuntimeException.class)
    public void testBoardFileWithInvalidValue() throws IOException {
        new MinesweeperServer(0, true, new File("boards/invalid-value.txt"));
    }
    
    @Test(expected = RuntimeException.class)
    public void testBoardFileMissingTerminalNewLine() throws IOException {
        new MinesweeperServer(0, true, new File("boards/missing-terminal-new-line.txt"));
    }
    
    @Test(expected = RuntimeException.class)
    public void testBoardFileWithNegativeSize() throws IOException {
        new MinesweeperServer(0, true, new File("boards/negative-size.txt"));
    }
    
    @Test(expected = RuntimeException.class)
    public void testBoardFileWithZeroSize() throws IOException {
        new MinesweeperServer(0, true, new File("boards/0x0.txt"));
    }
    
    @Test(expected = RuntimeException.class)
    public void testBoardFileWithTooManyColumns() throws IOException {
        new MinesweeperServer(0, true, new File("boards/too-many-columns.txt"));
    }
    
    @Test(expected = RuntimeException.class)
    public void testBoardFileWithTooLittleColumns() throws IOException {
        new MinesweeperServer(0, true, new File("boards/too-little-columns.txt"));
    }
    
    @Test(expected = RuntimeException.class)
    public void testBoardFileWithTooManyRows() throws IOException {
        new MinesweeperServer(0, true, new File("boards/too-many-rows.txt"));
    }
    
    @Test(expected = RuntimeException.class)
    public void testBoardFileWithTooLittleRows() throws IOException {
        new MinesweeperServer(0, true, new File("boards/too-little-rows.txt"));
    }
    
    @Test
    public void testOneByOneBoard() throws IOException {
        /*
         * 1 1
         * 0
         */
        MinesweeperServer server = new MinesweeperServer(0, true, new File("boards/1x1.txt"));
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        //assertEquals(String.format(MinesweeperServer.HELLO_MESSAGE_FORMAT,1, 1, 1), client.readln());
        client.readln();
        
        client.write("look\n");
        client.write("bye\n");
        
        assertEquals("-", client.readln());
        assertEquals(null, client.readln());
        
        server.terminate();
        client.terminate(); 
    }
    
    @Test
    public void testOneByTwoBoard() throws IOException {
        /*
         * 1 2
         * 0
         * 0
         */
        MinesweeperServer server = new MinesweeperServer(0, true, new File("boards/1x2.txt"));
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        assertEquals(String.format(MinesweeperServer.HELLO_MESSAGE_FORMAT,1, 2, 1), client.readln());
        
        client.write("look\n");
        client.write("bye\n");
        
        assertEquals("-", client.readln());
        assertEquals("-", client.readln());
        assertEquals(null, client.readln());
        
        server.terminate();
        client.terminate(); 
    }
    
    @Test
    public void testTwoByOneBoard() throws IOException {
        /*
         * 2 1
         * 0 0
         */
        MinesweeperServer server = new MinesweeperServer(0, true, new File("boards/2x1.txt"));
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        assertEquals(String.format(MinesweeperServer.HELLO_MESSAGE_FORMAT,2, 1, 1), client.readln());
        
        client.write("look\n");
        client.write("bye\n");
        
        assertEquals("- -", client.readln());
        assertEquals(null, client.readln());
        
        server.terminate();
        client.terminate(); 
    }
    
    @Test
    public void testTwoByTwoBoard() throws IOException {
        /*
         * 2 2
         * 0 0
         * 0 0
         */
        MinesweeperServer server = new MinesweeperServer(0, true, new File("boards/2x2.txt"));
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        assertEquals(String.format(MinesweeperServer.HELLO_MESSAGE_FORMAT,2, 2, 1), client.readln());
        
        client.write("look\n");
        client.write("bye\n");
        
        assertEquals("- -", client.readln());
        assertEquals("- -", client.readln());
        assertEquals(null, client.readln());
        
        server.terminate();
        client.terminate(); 
    }
    
    @Test
    public void testServerNotInDebugMode() throws IOException {

        MinesweeperServer server = new MinesweeperServer(0, false, new File("boards/1x1-(0,0).txt"));
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client = new MinesweeperClient(serverThread, server.port());
        
        assertTrue(client.readln().startsWith("Welcome"));
        
        client.write("look\n");
        client.write("dig 0 0\n");
        client.write("look\n");
        
        assertEquals("-", client.readln());
        assertEquals(MinesweeperServer.BOOM_MESSAGE, client.readln());
        assertEquals(null, client.readln());
        
        server.terminate();
        client.terminate(); 
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
        repeat(new Runnable() {

            @Override
            public void run() {
                try {
                    concurrentDigFlag();
                } catch (Throwable e) {
                    throw new AssertionError("Test failure", e);
                }
            }
            
        }, 1000);
    }
    
    private void concurrentDigFlag() throws IOException {
        MinesweeperServer server = new MinesweeperServer(0, true, new File("boards/1x1-(0,0).txt"));
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    // e.printStackTrace();
                }
            }
        });
        serverThread.start();

        MinesweeperClient client1 = new MinesweeperClient(serverThread, server.port());
        MinesweeperClient client2 = new MinesweeperClient(serverThread, server.port());

        assertTrue(client1.readln().startsWith("Welcome"));
        assertTrue(client2.readln().startsWith("Welcome"));

        new Thread(() -> client1.write("dig 0 0\n")).start();
        new Thread(() -> client2.write("flag 0 0\n")).start();

        String message1 = client1.readln();
        String message2 = client2.readln();

        assertTrue(String.format("Unexpected outcome:%nMessage to client1:%n%s%nMessage to client 2:%n%s", message1, message2),
                   message1.equals(MinesweeperServer.BOOM_MESSAGE) && message2.equals(" ")
                || message1.equals("F") && message2.equals("F"));

        server.terminate();
        client1.terminate();
        client2.terminate();
    }
    
    @Test
    public void testConcurrentDigLook() {
        repeat(new Runnable() {

            @Override
            public void run() {
                try {
                    concurrentDigLook();
                } catch (Throwable e) {
                    throw new AssertionError("Test failure", e);
                }
            }
            
        }, 1000);
    }
    
    private void concurrentDigLook() throws IOException {
        MinesweeperServer server = new MinesweeperServer(0, true, new File("boards/3x3.txt"));
        Thread serverThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        });
        serverThread.start();
        
        MinesweeperClient client1 = new MinesweeperClient(serverThread, server.port());
        MinesweeperClient client2 = new MinesweeperClient(serverThread, server.port());
        
        assertTrue(client1.readln().startsWith("Welcome"));
        assertTrue(client2.readln().startsWith("Welcome"));
        
        new Thread(() -> client1.write("dig 0 0\n")).start();;
        new Thread(() -> client2.write("look\n")).start();
        
        String message1 = String.format("%s\n%s\n%s", client1.readln(), client1.readln(), client1.readln());
        String message2 = String.format("%s\n%s\n%s", client2.readln(), client2.readln(), client2.readln());

        assertTrue(String.format("Unexpected outcome:%nMessage to client1:%n%s%nMessage to client 2:%n%s", message1, message2),
                   message1.equals("     \n"+
                                   "     \n"+
                                   "     ") && 
                  (message2.equals("     \n"+
                                   "     \n"+
                                   "     ") || 
                   message2.equals("- - -\n"+
                                   "- - -\n"+
                                   "- - -")));
        
        server.terminate();
        client1.terminate();
        client2.terminate();
    }    
}
