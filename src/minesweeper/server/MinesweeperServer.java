/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper.server;

import java.io.*;
import java.net.*;
import java.util.*;

import minesweeper.Board;

/**
 * Multiplayer Minesweeper server.
 */
public class MinesweeperServer {

    // System thread safety argument
    //   TODO Problem 5

    /** Default server port. */
    private static final int DEFAULT_PORT = 4444;
    /** Maximum port number as defined by ServerSocket. */
    private static final int MAXIMUM_PORT = 65535;
    /** Default square board size. */
    private static final int DEFAULT_SIZE = 10;
    
    /**
     * Format of the hello message returned on client connection. First integer
     * argument is the size of the board along x-axis. Second argument is the
     * size of the board along y-axis. Third argument is the number of
     * players connected to the server
     */
    static final String HELLO_MESSAGE_FORMAT = "Welcome to Minesweeper. Board: %1$d columns by %2$d rows. Players: %3$d including you. Type 'help' for help.";
    /** Help message */
    static final String HELP_MESSAGE = "Usage: 'look' | 'help' | 'bye' | '(dig | flag | deflag) X Y' where X Y are integers";
    /** BOOM message*/
    static final String BOOM_MESSAGE = "BOOM!";

    /** Socket for receiving incoming connections. */
    private final ServerSocket serverSocket;
    /** True if the server should *not* disconnect a client after a BOOM message. */
    private final boolean debug;
    /** Minesweeper board*/
    private final Board board;

    // TODO: Abstraction function, rep invariant, rep exposure
    
    /**
     * Make a MinesweeperServer, initialized with a random board of sizeX by
     * sizeY, that listens for connections on port.
     * 
     * @param port
     *            port number, requires 0 <= port <= 65535
     * @param debug
     *            debug mode flag
     * @param sizeX
     *            width of the board, requires sizeX > 0
     * @param sizeY
     *            height of the board, requires sizeY > 0
     * @throws IOException
     *             if an error occurs opening the server socket
     */
    public MinesweeperServer(int port, boolean debug, int sizeX, int sizeY) throws IOException {
        serverSocket = new ServerSocket(port);
        this.debug = debug;
        board = null; //new Board(sizeX, sizeY);
    }
    
    /**
     * Make a MinesweeperServer, initialized with a board loaded from a file,
     * listening on the specified port for connections.
     * 
     * @param port
     *            port number, requires 0 <= port <= 65535. Specifying port
     *            number 0 will assign any available port.
     * @param debug
     *            debug mode flag
     * @param file
     *            file from which the the board is initialized
     * @throws IOException
     *             if an error occurs loading the file or opening the server
     *             socket
     */
    public MinesweeperServer(int port, boolean debug, File file) throws IOException {
        serverSocket = new ServerSocket(port);
        this.debug = debug;

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String[] dimensions = in.readLine().split(" ");

            if (dimensions.length != 2) {
                throw new RuntimeException("Improper file format");
            }

            StringBuilder sb = new StringBuilder();

            for (int character = in.read(); character != -1; character = in.read()) {
                sb.append((char) character);
            }

            board = new Board(Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]), sb.toString());
        } catch (IllegalArgumentException iae) {
            throw new RuntimeException("Improper file format", iae);
        }
    }

    /**
     * Run the server, listening for client connections and handling them.
     * Never returns unless an exception is thrown.
     * 
     * @throws IOException if the main server socket is broken or the server has been terminated
     *                     (IOExceptions from individual clients do *not* terminate serve())
     */
    public void serve() throws IOException {
        while (true) {
            // block until a client connects
            final Socket socket = serverSocket.accept();

            new Thread(new Runnable() {
                public void run() {
                    // handle the client
                    try {
                        handleConnection(socket);
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }).start();
        }
    }
    
    /**
     * Terminates this server. The socket on which the server is listening is
     * closed, and any thread running serve() will throw IOException indicating
     * the termination of the server. Once the server is terminated, it can no
     * longer be restarted; any attempt to do so with throw an IOException.
     * Individual client connections are *not* terminated.
     * 
     * @throws IOException
     *             if an I/O error occurs when terminating the server.
     */
    public void terminate() throws IOException {
        serverSocket.close();
    }

    /**
     * Handle a single client connection. Returns when client disconnects.
     * 
     * @param socket
     *            socket where the client is connected
     * @throws IOException
     *             if the connection encounters an error or terminates
     *             unexpectedly
     */
    private void handleConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println("Welcome"); // Send hello message immediately after connection.

        try {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                String output = handleRequest(line);

                if (output == null) {
                    // TODO: Consider improving spec of handleRequest to avoid use of null
                    break;
                }
                
                out.println(output);
                
                if (output.equals(BOOM_MESSAGE) && !debug) {
                    break;
                }
            }
        } finally {
            out.close();
            in.close();
            socket.close();
        }
    }

    /**
     * Handler for client input, performing requested operations and returning an output message.
     * 
     * @param input message from client
     * @return message to client, or null if none
     */
    private String handleRequest(String input) {
        String regex = "(look)|(help)|(bye)|"
                     + "(dig -?\\d+ -?\\d+)|(flag -?\\d+ -?\\d+)|(deflag -?\\d+ -?\\d+)";
        if ( ! input.matches(regex)) {
            // invalid input
            return HELP_MESSAGE;
        }
        String[] tokens = input.split(" ");
        if (tokens[0].equals("look")) {
            // 'look' request
            return board.toString().replaceAll("\n$", "");
        } else if (tokens[0].equals("help")) {
            // 'help' request
            return HELP_MESSAGE;
        } else if (tokens[0].equals("bye")) {
            // 'bye' request
            return null;
        } else {
            int x = Integer.parseInt(tokens[1]);
            int y = Integer.parseInt(tokens[2]);
            if (tokens[0].equals("dig")) {
                // 'dig x y' request
                return board.dig(x, y) ? BOOM_MESSAGE : board.toString().replaceAll("\n$", "");
            } else if (tokens[0].equals("flag")) {
                // 'flag x y' request
                board.flag(x, y);
                return board.toString().replaceAll("\n$", "");
            } else if (tokens[0].equals("deflag")) {
                // 'deflag x y' request
                board.deflag(x, y);
                return board.toString().replaceAll("\n$", "");
            }
        }
        // TODO: Should never get here, make sure to return in each of the cases above
        throw new UnsupportedOperationException();
    }

    /**
     * @return the port assigned to the server socket
     */
    public int port() {
        return serverSocket.getLocalPort();
    }
    
    /**
     * Start a MinesweeperServer using the given arguments.
     * 
     * <br> Usage:
     *      MinesweeperServer [--debug | --no-debug] [--port PORT] [--size SIZE_X,SIZE_Y | --file FILE]
     * 
     * <br> The --debug argument means the server should run in debug mode. The server should disconnect a
     *      client after a BOOM message if and only if the --debug flag was NOT given.
     *      Using --no-debug is the same as using no flag at all.
     * <br> E.g. "MinesweeperServer --debug" starts the server in debug mode.
     * 
     * <br> PORT is an optional integer in the range 0 to 65535 inclusive, specifying the port the server
     *      should be listening on for incoming connections.
     * <br> E.g. "MinesweeperServer --port 1234" starts the server listening on port 1234.
     * 
     * <br> SIZE_X and SIZE_Y are optional positive integer arguments, specifying that a random board of size
     *      SIZE_X*SIZE_Y should be generated.
     * <br> E.g. "MinesweeperServer --size 42,58" starts the server initialized with a random board of size
     *      42*58.
     * 
     * <br> FILE is an optional argument specifying a file pathname where a board has been stored. If this
     *      argument is given, the stored board should be loaded as the starting board.
     * <br> E.g. "MinesweeperServer --file boardfile.txt" starts the server initialized with the board stored
     *      in boardfile.txt.
     * 
     * <br> The board file format, for use with the "--file" option, is specified by the following grammar:
     * <pre>
     *   FILE ::= BOARD LINE+
     *   BOARD ::= X SPACE Y NEWLINE
     *   LINE ::= (VAL SPACE)* VAL NEWLINE
     *   VAL ::= 0 | 1
     *   X ::= INT
     *   Y ::= INT
     *   SPACE ::= " "
     *   NEWLINE ::= "\n" | "\r" "\n"?
     *   INT ::= [0-9]+
     * </pre>
     * 
     * <br> If neither --file nor --size is given, generate a random board of size 10x10.
     * 
     * <br> Note that --file and --size may not be specified simultaneously.
     * 
     * @param args arguments as described
     */
    public static void main(String[] args) {
        // Command-line argument parsing is provided. Do not change this method.
        boolean debug = false;
        int port = DEFAULT_PORT;
        int sizeX = DEFAULT_SIZE;
        int sizeY = DEFAULT_SIZE;
        Optional<File> file = Optional.empty();

        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        try {
            while ( ! arguments.isEmpty()) {
                String flag = arguments.remove();
                try {
                    if (flag.equals("--debug")) {
                        debug = true;
                    } else if (flag.equals("--no-debug")) {
                        debug = false;
                    } else if (flag.equals("--port")) {
                        port = Integer.parseInt(arguments.remove());
                        if (port < 0 || port > MAXIMUM_PORT) {
                            throw new IllegalArgumentException("port " + port + " out of range");
                        }
                    } else if (flag.equals("--size")) {
                        String[] sizes = arguments.remove().split(",");
                        sizeX = Integer.parseInt(sizes[0]);
                        sizeY = Integer.parseInt(sizes[1]);
                        file = Optional.empty();
                    } else if (flag.equals("--file")) {
                        sizeX = -1;
                        sizeY = -1;
                        file = Optional.of(new File(arguments.remove()));
                        if ( ! file.get().isFile()) {
                            throw new IllegalArgumentException("file not found: \"" + file.get() + "\"");
                        }
                    } else {
                        throw new IllegalArgumentException("unknown option: \"" + flag + "\"");
                    }
                } catch (NoSuchElementException nsee) {
                    throw new IllegalArgumentException("missing argument for " + flag);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("unable to parse number for " + flag);
                }
            }
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.err.println("usage: MinesweeperServer [--debug | --no-debug] [--port PORT] [--size SIZE_X,SIZE_Y | --file FILE]");
            return;
        }

        try {
            runMinesweeperServer(debug, file, sizeX, sizeY, port);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Start a MinesweeperServer running on the specified port, with either a random new board or a
     * board loaded from a file.
     * 
     * @param debug The server will disconnect a client after a BOOM message if and only if debug is false.
     * @param file If file.isPresent(), start with a board loaded from the specified file,
     *             according to the input file format defined in the documentation for main(..).
     * @param sizeX If (!file.isPresent()), start with a random board with width sizeX
     *              (and require sizeX > 0).
     * @param sizeY If (!file.isPresent()), start with a random board with height sizeY
     *              (and require sizeY > 0).
     * @param port The network port on which the server should listen, requires 0 <= port <= 65535.
     * @throws IOException if a network error occurs
     */
    public static void runMinesweeperServer(boolean debug, Optional<File> file, int sizeX, int sizeY, int port) throws IOException {
        
        // TODO: Continue implementation here in problem 4
        
        MinesweeperServer server = new MinesweeperServer(port, debug, sizeX, sizeY);
        server.serve();
    }
}
