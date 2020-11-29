package com.ticTacToe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static java.lang.System.in;

/**
 * This program implements the Tic Tac Toe game.
 *
 * @author Radina Dineva
 * @version 1.0
 * @since 2020-11-28
 */

public class Game {
  public static int MAX = 1;
  public static int MIN = -1;
  public static int EMPTY = 0;
  public static int TOTAL_BOARD_CELLS = 9;

  public int[][] board = new int[3][3];
  public int takenBoardCells = 0;
  public int computer = MIN;
  public int human = MAX;

  /**
   * Starts the game.
   *
   * @throws IOException
   */
  public void start() throws IOException {
    System.out.println("Computer plays with 'o' and you play with 'x'.");
    System.out.println("Who should play first - human or computer? By default human is always first.");
    System.out.print("Insert '-h' for human and '-c' for computer: ");
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    String firstPlayer = reader.readLine();

    if (firstPlayer.equals("-c")) {
      this.computer = MAX;
      this.human = MIN;
      this.computerMove();
    }

    while(!this.gameOver()) {
      this.humanMove();
      this.computerMove();
    }

    this.printWinnerMessage();
  }

  /**
   * Checks if the game is over which happens when a
   * player has won or there are no empty cells in the board.
   *
   * @return boolean whether the game is over
   */
  private boolean gameOver() {
    return this.checkWinner(this.computer) || this.checkWinner(this.human) || this.takenBoardCells == TOTAL_BOARD_CELLS;
  }


  /**
   * This method outputs a message on the console with the winner of the game.
   */
  private void printWinnerMessage() {
    if (this.checkWinner((this.computer))) {
      System.out.println("Computer won!");
    } else if (this.checkWinner(this.human)) {
      System.out.println("You won!");
    } else {
      System.out.println("It's a tie!");
    }
  }

  /**
   * Gets the user move from the input and places it on the board.
   *
   * @throws IOException
   */
  private void humanMove() throws IOException {
    if (this.gameOver()) {
      return;
    }

    int row, col;
    do {
      System.out.print("Your turn: ");
      int[] move = this.readMove();
      row = move[0];
      col = move[1];
    } while(this.board[row - 1][col - 1] != EMPTY);

    this.board[row - 1][col - 1] = this.human;
    this.takenBoardCells++;
    this.printBoard();
  }

  /**
   * Reads a input from the console, splits it by spaces and maps it to integers.
   *
   * @return int[] numbers read from the console
   * @throws IOException
   */
  private int[] readMove() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    String input = reader.readLine();
    return Arrays.stream(input.split(" ")).mapToInt(Integer::parseInt).toArray();
  }

  /**
   * Finds the best move for the computer
   * by using the minimax algorithm and takes that cell.
   */
  private void computerMove() {
    if (this.gameOver()) {
      return;
    }

    int depth = 0;
    int alpha = Integer.MIN_VALUE;
    int beta = Integer.MAX_VALUE;

    int[] result = this.minimax(this.board, depth, this.computer, alpha, beta);
    int row =  result[1];
    int col = result[2];

    this.board[row][col] = this.computer;
    this.takenBoardCells++;
    System.out.println("Computer:");
    this.printBoard();
  }

  /**
   * This method implements the minimax algorithm
   * with alpha-beta pruning and depth search optimization.
   *
   * @param board current state of the game
   * @param depth depth of the state
   * @param player the current player which is either the maximizing or the minimizing
   * @param alpha
   * @param beta
   * @return int[] the best node found - it's score, row and column of it's cell, and it's depth
   */
  public int[] minimax(int[][] board, int depth, int player, int alpha, int beta) {
    if (this.gameOver() || depth == this.maxDepth()) {
      int score = this.evaluateBoard();
      return new int[] { score, -1, -1, depth };
    }

    int[] best;
    if (player == MAX) {
      best = new int[] { Integer.MIN_VALUE, -1, -1, depth };
    } else {
      best = new int[] { Integer.MAX_VALUE, -1, -1, depth };
    }

    ArrayList<Cell> emptyCells = this.getEmptyCells();

    for(Cell cell : emptyCells) {
      board[cell.row][cell.col] = player;
      int[] result = minimax(board, depth + 1, -player, alpha, beta);
      board[cell.row][cell.col] = EMPTY;

      int move = result[0];
      int resultDepth = result[3];
      int bestMove = best[0];
      int bestDepth = best[3];

      if (player == MAX) {
        if (move > bestMove || (move == bestMove && resultDepth < bestDepth)) {
          best = new int[] { move, cell.row, cell.col, resultDepth };
        }

        alpha = Math.max(alpha, best[0]);
      } else {
        if (move < bestMove || (move == bestMove && resultDepth < bestDepth)) {
          best = new int[] { move, cell.row, cell.col, resultDepth };
        }

        beta = Math.min(beta, best[0]);
      }

      if (alpha >= beta) break;
    }

    return best;
  }

  /**
   * Finds the empty cells of the board.
   *
   * @return ArrayList<Cell> the empty cells
   */
  private ArrayList<Cell> getEmptyCells() {
    ArrayList<Cell> emptyCells = new ArrayList<>();

    for(int row = 0; row < 3; row++) {
      for(int col = 0; col < 3; col++) {
        if (this.board[row][col] == EMPTY) {
          emptyCells.add(new Cell(row, col));
        }
      }
    }

    return emptyCells;
  }

  /**
   * Calculates the current max depth of a state.
   *
   * @return int current max depth
   */
  private int maxDepth() {
    return TOTAL_BOARD_CELLS - this.takenBoardCells;
  }


  /**
   * This method is the evaluation (or heuristic) function of the board.
   *
   * @return int the heuristic value
   */
  private int evaluateBoard() {
    if (this.checkWinner(MAX)) {
      return MAX;
    } else if (this.checkWinner(MIN)) {
      return MIN;
    }

    return EMPTY;
  }

  /**
   * Checks if a player is a winner
   * either in any of the rows, columns or diagonals.
   *
   * @param player
   * @return boolean whether a player is a winner
   */
  private boolean checkWinner(int player) {
    return this.rowWinner(player) || this.columnWinner(player) || this.diagonalWinner(player);
  }

  /**
   * Checks if a player is a winner by
   * having all placements in any row.
   *
   * @param player
   * @return boolean whether the player is a winner in any row
   */
  private boolean rowWinner(int player) {
    for(int row = 0; row < 3; row++) {
      if (this.board[row][0] == player
              && this.board[row][0] == this.board[row][1]
              && this.board[row][1] == this.board[row][2]) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks if a player is a winner by
   * having all placements in any column.
   *
   * @param player
   * @return boolean whether the player is a winner in any column
   */
  private boolean columnWinner(int player) {
    for(int col = 0; col < 3; col++) {
      if (this.board[0][col] == player
              && this.board[0][col] == this.board[1][col]
              && this.board[1][col] == this.board[2][col]) {
        return true;
      }
    }

    return false;
  }

  /**
   * Checks if a player is a winner by having
   * all placements in one of two diagonals.
   *
   * @param player
   * @return boolean whether the player is a winner in any diagonal
   */
  private boolean diagonalWinner(int player) {
    boolean firstDiagonalWinner = player == this.board[0][0]
            && this.board[0][0] == this.board[1][1]
            && this.board[1][1] == this.board[2][2];

    boolean secondDiagonalWinner = player == this.board[0][2]
            && this.board[0][2] == this.board[1][1]
            && this.board[1][1] == this.board[2][0];

    return firstDiagonalWinner || secondDiagonalWinner;
  }

  /**
   * Prints in the console the current state of the board.
   **/
  private void printBoard() {
    for(int row = 0; row < 3; row++) {
      for(int col = 0; col < 3; col++) {
        char player = this.board[row][col] == this.computer ? 'o' : 'x';
        char move = this.board[row][col] == EMPTY ? '-' : player;
        System.out.printf(" %s ", move);
        if (col != 2) {
          System.out.print("|");
        }
      }

      System.out.println();
    }

    System.out.println();
  }
}
