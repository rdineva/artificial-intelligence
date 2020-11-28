package com.ticTacToe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static java.lang.System.in;

public class Game {
  public static int MAX = 1;
  public static int MIN = -1;
  public static int TOTAL_BOARD_CELLS = 9;
  public static int EMPTY_CELL = 0;

  public int[][] board = new int[3][3];
  public int takenBoardCells = 0;
  public int computer = MIN;
  public int human = MAX;
  public int maxDepth = TOTAL_BOARD_CELLS - this.takenBoardCells;

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
  }

  private boolean gameOver() {
    return this.checkWinner(this.computer) || this.checkWinner(this.human) || this.takenBoardCells == TOTAL_BOARD_CELLS;
  }

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
    } while(this.board[row - 1][col - 1] != EMPTY_CELL);

    this.board[row - 1][col - 1] = this.human;
    this.takenBoardCells++;
    this.printBoard();
  }

  private int[] readMove() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    String input = reader.readLine();
    return Arrays.stream(input.split(" ")).mapToInt(Integer::parseInt).toArray();
  }

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

  public int[] minimax(int[][] board, int depth, int player, int alpha, int beta) {
    if (this.gameOver() || depth == this.maxDepth) {
      int score = this.evaluateBoard();
      return new int[] { score, -1, -1, this.maxDepth - depth };
    }

    int[] best;
    if (player == MAX) {
      best = new int[] { Integer.MIN_VALUE, -1, -1, this.maxDepth - depth };
    } else {
      best = new int[] { Integer.MAX_VALUE, -1, -1, depth - this.maxDepth };
    }

    ArrayList<Cell> emptyCells = this.getEmptyCells();

    for(Cell cell : emptyCells) {
      int row = cell.row;
      int col = cell.col;

      board[row][col] = player;
      int[] result = minimax(board, depth + 1, -player, alpha, beta);
      board[row][col] = EMPTY_CELL;

      int move = result[0];
      int resultDepth = result[3];
      int bestMove = best[0];
      int bestDepth = best[0];

      if (player == MAX) {
        if (move > bestMove || (move == bestMove && resultDepth < bestDepth)) {
          best = new int[] { move, row, col, resultDepth };
        }

        alpha = Math.max(alpha, best[0]);
      } else {
        if (move < bestMove || (move == bestMove && resultDepth > bestDepth)) {
          best = new int[] { move, row, col, resultDepth };
        }

        beta = Math.min(beta, best[0]);
      }

      if (alpha >= beta) break;
    }

    return best;
  }

  private ArrayList<Cell> getEmptyCells() {
    ArrayList<Cell> emptyCells = new ArrayList<>();

    for(int row = 0; row < 3; row++) {
      for(int col = 0; col < 3; col++) {
        if (this.board[row][col] == EMPTY_CELL) {
          emptyCells.add(new Cell(row, col));
        }
      }
    }

    return emptyCells;
  }

  private int evaluateBoard() {
    if (this.checkWinner(MAX)) {
      return 1;
    } else if (this.checkWinner(MIN)) {
      return -1;
    }

    return 0;
  }

  private boolean checkWinner(int player) {
    return this.rowWinner(player) || this.columnWinner(player) || this.diagonalWinner(player);
  }

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

  private boolean diagonalWinner(int player) {
    boolean firstDiagonalWinner = player == this.board[0][0]
            && this.board[0][0] == this.board[1][1]
            && this.board[1][1] == this.board[2][2];

    boolean secondDiagonalWinner = player == this.board[0][2]
            && this.board[0][2] == this.board[1][1]
            && this.board[1][1] == this.board[2][0];

    return firstDiagonalWinner || secondDiagonalWinner;
  }

  private void printBoard() {
    for(int row = 0; row < 3; row++) {
      for(int col = 0; col < 3; col++) {
        char player = this.board[row][col] == this.computer ? 'o' : 'x';
        char move = this.board[row][col] == EMPTY_CELL ? '-' : player;
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
