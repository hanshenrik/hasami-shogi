package com.hanshenrik.gronsleth_hasamishogi;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public int[][] board; // not using enums because ints are more efficient!
    public int capturesToWin;
    public int turn = 1;
    public int[] captures = new int[] { 0, 0 };
    public boolean isTouched = false;
    public final static int PLAYER1 = 1;
    public final static int PLAYER2 = 2;
    public final static int BOARD_SIZE = 9;
    public int winner = 0;

    public Board(int numberOfPieces, int capturesToWin) {
        if (numberOfPieces != 9 && numberOfPieces != 18) {
            throw new IllegalArgumentException("Number of pieces must be 9 or 18, cannot be '" +
                    numberOfPieces + "'");
        }

        this.board = new int[BOARD_SIZE][BOARD_SIZE]; // all positions are now 0
        this.capturesToWin = capturesToWin;

        int rowsPerPlayer = numberOfPieces / BOARD_SIZE;
        for (int i = 0; i < rowsPerPlayer; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = PLAYER1;
                board[BOARD_SIZE - 1 - i][j] = PLAYER2;
            }
        }
    }

    public String move(int fromX, int fromY, int toX, int toY) {
        isTouched = true; // TODO: is there a way do only do this first time?
        if (winner != 0) {
            return "Game is over, man!";
        }
        try {
            validateMove(fromX, fromY, toX, toY);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }

        board[toY][toX] = board[fromY][fromX];
        board[fromY][fromX] = 0;
        checkCapture(toX, toY);
        checkWin(); // TODO: this could set a parameter that is later checked somewhere else
        printBoard();
        switchTurn();
        return null;
    }

    private void checkCapture(int toX, int toY) {
        Log.d("checkCapture", "("+toX+", "+toY+")");
        // TODO: actually just need to check 3 directions, not the one where piece comes from, but might be easier to just check all

        // TODO: optimize, compress to single loop
        // N
        List<int[]> capturePositions = new ArrayList<>();
        for (int y = toY - 1; y >= 0; y--) {
            Log.d("N", "y: "+y);
            if (board[y][toX] == 0) break; // no piece
            if (board[y][toX] == turn && capturePositions.size() == 0) break; // self piece
            if (board[y][toX] != 0 && board[y][toX] != turn) { // opponent piece
                capturePositions.add(new int[]{toX, y});
            }
            if (board[y][toX] == turn && capturePositions.size() > 0) { // self piece with opponent piece in between
                performCapture(capturePositions);
            }
        }
        // E
        capturePositions.clear();
        for (int x = toX + 1; x < BOARD_SIZE; x++) {
            Log.d("E", "x: "+x);
            if (board[toY][x] == 0) break; // no piece
            if (board[toY][x] == turn && capturePositions.size() == 0) break; // self piece
            if (board[toY][x] != 0 && board[toY][x] != turn) { // opponent piece
                capturePositions.add(new int[]{x, toY});
            }
            if (board[toY][x] == turn && capturePositions.size() > 0) { // self piece with opponent piece in between
                performCapture(capturePositions);
            }
        }

        // S
        capturePositions.clear();
        for (int y = toY + 1; y < BOARD_SIZE; y++) {
            Log.d("S", "y: "+y);
            if (board[y][toX] == 0) break; // no piece
            if (board[y][toX] == turn && capturePositions.size() == 0) break; // self piece
            if (board[y][toX] != 0 && board[y][toX] != turn) { // opponent piece
                capturePositions.add(new int[]{toX, y});
            }
            if (board[y][toX] == turn && capturePositions.size() > 0) { // self piece with opponent piece in between
                performCapture(capturePositions);
            }
        }

        // W
        capturePositions.clear();
        for (int x = toX - 1; x >= 0; x--) {
            Log.d("W", "x: "+x);
            if (board[toY][x] == 0) break; // no piece
            if (board[toY][x] == turn && capturePositions.size() == 0) break; // self piece
            if (board[toY][x] != 0 && board[toY][x] != turn) { // opponent piece
                capturePositions.add(new int[]{x, toY});
            }
            if (board[toY][x] == turn && capturePositions.size() > 0) { // self piece with opponent piece in between
                performCapture(capturePositions);
            }
        }
    }

    private void performCapture(List<int[]> capturePositions) {
        for (int[] pos : capturePositions) {
            Log.d("performCapture", "("+pos[0]+", "+pos[1]+")");
            board[pos[1]][pos[0]] = 0;
            captures[turn - 1] += 1;
        }
    }

    private void switchTurn() {
        this.turn = (turn == 1) ? 2 : 1;
    }

    private void validateMove(int fromX, int fromY, int toX, int toY) throws IllegalArgumentException {
        // validate correct piece at source position
        if (board[fromY][fromX] != turn) {
            throw new IllegalArgumentException("Can only move own piece!");
        }

        // validate destination is inside board
        if ( toX < 0 || toX > BOARD_SIZE || toY < 0 || toY > BOARD_SIZE ) {
            throw new IllegalArgumentException("Destination must be inside board!");
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
            throw new IllegalArgumentException("Only vertical and horizontal moves are allowed.");
        }

        // validate move is not to same position
        if (fromX == toX && fromY == toY) {
            throw new IllegalArgumentException("Piece must be moved to a new position.");
        }

        // validate no pieces in the way (inc. destination position)
        // TODO: clean up; compress into single while loop
        int x = fromX, y = fromY;
        if (dirX != 0) { // horizontal moves
            while (x != toX) {
                if (board[y][x+dirX] != 0) {
                    throw new IllegalArgumentException("There are pieces in the way");
                }
                x += dirX;
            }
        }
        if (dirY != 0) {
            while (y != toY) {
                if (board[y+dirY][x] != 0) {
                    throw new IllegalArgumentException("There are pieces in the way");
                }
                y += dirY;
            }
        }
    }

    private void checkWin() {
        // only need to check the most recent player
        if (captures[turn-1] >= capturesToWin) {
            Log.d("checkWin", turn + " won!");
            winner = turn;
        }
        // TODO: implement 5 in a row logic, use logic from Connect Four
    }

    public int get(int x, int y) {
        return board[y][x];
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
