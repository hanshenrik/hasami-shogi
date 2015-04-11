package com.hanshenrik.gronsleth_hasamishogi;


import android.util.Log;

public class Board {
    public int[][] board; // not using enums because ints are more efficient!
    public final static int PLAYER1 = 1;
    public final static int PLAYER2 = 2;
    public final static int BOARD_SIZE = 9;

    public Board(int numberOfPieces) {
        if (numberOfPieces != 9 && numberOfPieces != 18) {
            throw new IllegalArgumentException("Number of pieces must be 9 or 18, cannot be '" +
                    numberOfPieces + "'");
        }

        this.board = new int[BOARD_SIZE][BOARD_SIZE]; // all positions are now 0

        int rowsPerPlayer = numberOfPieces / BOARD_SIZE;
        for (int i = 0; i < rowsPerPlayer; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = PLAYER1;
                board[BOARD_SIZE - 1 - i][j] = PLAYER2;
            }
        }
    }

    public boolean move(int fromX, int fromY, int toX, int toY) {
        // validate there is a piece at source position
        if (board[fromY][fromX] == 0) {
            // throw new IllegalArgumentException("Source position must contain piece!");
            return false;
        }

        // validate destination is inside board
        if ( toX < 0 || toX > BOARD_SIZE || toY < 0 || toY > BOARD_SIZE ) {
            // throw new IllegalArgumentException("Destination must be inside board!");
            return false;
        }

        // determine if move is horizontal or vertical
        int dirX = 0;
        int dirY = 0;
        if (toX > fromX) dirX = 1;
        if (toX < fromX) dirX = -1;
        if (toY > fromY) dirY = 1;
        if (toY < fromY) dirY = -1;

        // validate move is horizontal or vertical
        if (dirX != 0 && dirY != 0) {
            // throw new IllegalArgumentException("Only vertical and horizontal moves are allowed.");
            return false;
        }

        // validate move is not to same position
        if (fromX == toX && fromY == toY) {
            // throw new IllegalArgumentException("Piece must be moved to a new position.");
            return false;
        }

        // validate no pieces in the way (inc. destination position)
        // TODO: clean up; compress into single while loop
        int x = fromX, y = fromY;
        if (dirX != 0) { // horizontal moves
            while (x != toX) {
                if (board[y][x+dirX] != 0) {
                    return false;
                }
                x += dirX;
            }
        }
        if (dirY != 0) {
            while (y != toY) {
                if (board[y+dirY][x] != 0) {
                    return false;
                }
                y += dirY;
            }
        }

        board[toY][toX] = board[fromY][fromX];
        board[fromY][fromX] = 0;
        printBoard();
        return true;
    }

    // DEV print board
    public void printBoard() {
        Log.d("###", "################");
        for (int i = 0; i < board.length; i++) {
            String s = "| ";
            for (int j = 0; j < board[i].length; j++) {
                s += board[i][j] + " | ";
            }
            Log.d("BOARD",  s);
        }
        Log.d("###", "################");
    }
}
