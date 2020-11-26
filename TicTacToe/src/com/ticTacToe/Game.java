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

  public void start() throws IOException {
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
    return this.checkWinner(this.computer) || this.checkWinner(this.human);
  }

  private void humanMove() throws IOException {
    if (this.checkWinner(this.computer)) {
      return;
    }

    int x, y;
    do {
      System.out.print("Your turn: ");
      int[] move = this.readMove();
      x = move[0];
      y = move[1];
    } while(this.board[x - 1][y - 1] != EMPTY_CELL);

    this.board[x - 1][y - 1] = this.human;
    this.takenBoardCells++;
    this.printBoard();
  }

  private int[] readMove() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    String input = reader.readLine();
    return Arrays.stream(input.split(" ")).mapToInt(Integer::parseInt).toArray();
  }

  private void computerMove() {
    if (this.checkWinner(this.human)) {
      return;
    }

    int depth = TOTAL_BOARD_CELLS - this.takenBoardCells;
    int[] result = this.minimax(this.board, depth, this.computer);
    int xMove =  result[1];
    int yMove = result[2];

    this.board[xMove][yMove] = this.computer;
    this.takenBoardCells++;
    System.out.println("Computer:");
    this.printBoard();
  }

  public int[] minimax(int[][] board, int depth, int player) {
    if (this.gameOver() || depth == 0) {
      int score = this.evaluateBoard();
      return new int[] { score, -1, -1 };
    }

    int[] best;
    if (player == MAX) {
      best = new int[] { Integer.MIN_VALUE, -1, -1 };
    } else {
      best = new int[] { Integer.MAX_VALUE, -1, -1 };
    }

    ArrayList<Cell> emptyCells = this.getEmptyCells();

    for(Cell cell : emptyCells) {
      int x = cell.row;
      int y = cell.col;

      board[x][y] = player;
      int[] result = minimax(board, depth - 1, -player);
      board[x][y] = EMPTY_CELL;
      int move = result[0];
      int bestMove = best[0];

      if (player == MAX) {
        if (move > bestMove) {
          bestMove = move;
          best = new int[] { bestMove, x, y };
        }
      } else {
        if (move < bestMove) {
          bestMove = move;
          best = new int[] { bestMove, x, y };
        }
      }
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
        System.out.print(this.board[row][col] + " ");
      }

      System.out.println();
    }

    System.out.println();
  }
}
