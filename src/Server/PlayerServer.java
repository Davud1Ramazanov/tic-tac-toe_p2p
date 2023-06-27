package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class PlayerServer {
    private static final int port = 4444;

    private char [][] board;
    private char currentPlayer;
    private boolean playerEnded;

    public PlayerServer(){
        board = new char[3][3];
        currentPlayer = 'X';
        playerEnded = false;
        initializationBoard();
    }

    public void initializationBoard(){
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board.length; j++){
                board[i][j] = '-';
            }
        }
    }

    public void start() throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is turn. Wait client please...");
            Socket socket = serverSocket.accept();
            System.out.println("Client is connected with Server.");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            playGame(bufferedReader, printWriter);
            socket.close();
            serverSocket.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public void playGame(BufferedReader bufferedReader, PrintWriter printWriter) throws IOException {
        while (!playerEnded){
            if (currentPlayer == 'X'){
                printBoard();
                System.out.println("Enter row and column: ");
                int row = Integer.parseInt(bufferedReader.readLine());
                int column = Integer.parseInt(bufferedReader.readLine());
                if(makeMove(row, column)){
                    printWriter.println(row);
                    printWriter.println(column);
                    printWriter.flush();
                }else {
                    printWriter.println(-1);
                    printWriter.flush();
                }
            } else {
                System.out.println("Opponent's turn...");
                int row = Integer.parseInt(bufferedReader.readLine());
                int col = Integer.parseInt(bufferedReader.readLine());
                if (row == -1 && col == -1) {
                    System.out.println("Invalid move. Please try again.");
                } else {
                    makeMove(row, col);
                }
            }

            if (checkWin()) {
                printBoard();
                System.out.println("Congratulations! Player " + currentPlayer + " wins!");
                playerEnded = true;
            } else if (isBoardFull()) {
                printBoard();
                System.out.println("It's a draw!");
                playerEnded = true;
            } else {
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
            }
        }
    }

    private boolean makeMove(int row, int col) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == '-') {
            board[row][col] = currentPlayer;
            return true;
        } else {
            return false;
        }
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) {
                return true;
            }
        }

        for (int j = 0; j < 3; j++) {
            if (board[0][j] == currentPlayer && board[1][j] == currentPlayer && board[2][j] == currentPlayer) {
                return true;
            }
        }
        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) {
            return true;
        }
        if (board[2][0] == currentPlayer && board[1][1] == currentPlayer && board[0][2] == currentPlayer) {
            return true;
        }

        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    private void printBoard() {
        System.out.println("Board:");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) throws IOException {
        PlayerServer server = new PlayerServer();
        server.start();
    }
}
