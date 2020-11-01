package com.npuzzle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import static java.lang.System.in;

public class InputReader {
  public int[][] startBoardTiles, goalBoardTiles;
  private BufferedReader reader;
  private int n, I;

  public InputReader() {
    this.reader = new BufferedReader(new InputStreamReader(in));
  }

  public void read() throws IOException {
    this.n = Integer.parseInt(reader.readLine());
    this.I = Integer.parseInt(reader.readLine());

    if (I >= this.n || I <= 0) {
      I = this.n * this.n;
    }

    this.startBoardTiles = getStartBoardTiles(n);
    this.goalBoardTiles = getGoalBoardTiles(n, I);
  }

  private int[][] getGoalBoardTiles(int n, int I) {
    int[][] boardNumbers = new int[n][n];
    int counter = 1;
    boolean zeroPlaced = false;

    for(int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (counter == I && !zeroPlaced) {
          boardNumbers[i][j] = 0;
          zeroPlaced = true;
          continue;
        }

        boardNumbers[i][j] = counter;
        counter++;
      }
    }

    return boardNumbers;
  }

  private int[][] getStartBoardTiles(int n) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    int[][] boardNumbers = new int[n][n];

    for (int i = 0; i < n; i++) {
      String inputLine = reader.readLine();
      int[] inputNumbers = Arrays.stream(inputLine.split(" ")).mapToInt(Integer::parseInt).toArray();

      System.arraycopy(inputNumbers, 0, boardNumbers[i], 0, n);
    }

    return boardNumbers;
  }
}
