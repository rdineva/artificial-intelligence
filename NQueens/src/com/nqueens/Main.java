package com.nqueens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Main {

  public static int[] solution;

  public static void main(String[] args) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    int n = Integer.valueOf(reader.readLine());
    solution = new int[n];

    Instant start = Instant.now();
    solve(n);
    Instant finish = Instant.now();

    long timeElapsed = Duration.between(start, finish).toSeconds();
    System.out.println("Time elapsed: " + timeElapsed);
    if (n <= 25) {
      print(solution);
    }
  }

  public static int[] getConflictsForAllColumns(int[] queens) {
    int[] conflicts = new int[queens.length];

    for(int col = 0; col < queens.length; col++) {
      conflicts[col] = getRowConflicts(queens, col, queens[col]) + getDiagonalConflicts(queens, col, queens[col]);
    }

    return conflicts;
  }

  public static void solve(int n) {
    int[] queens = generateBoard(n);
    if (!hasConflicts(queens)) {
      solution = queens;
      return;
    }

    int iter = 0;

    while(iter <= 3*n) {
      int[] conflicts = getConflictsForAllColumns(queens);
      int colMaxConflicts = getColWithMaxConflicts(conflicts);
      int currentConflicts = conflicts[colMaxConflicts];

      HashMap<String, Integer> rowMinConflictsData = getRowWithMinConflicts(queens, colMaxConflicts, queens[colMaxConflicts]);
      int minConflicts = rowMinConflictsData.get("row");
      int rowMinConflicts = rowMinConflictsData.get("conflicts");

      if (currentConflicts == minConflicts) {
        int randomRow = pickRandom(n);
        queens[colMaxConflicts] = randomRow;
      } else if (currentConflicts > minConflicts) {
        queens[colMaxConflicts] = rowMinConflicts;
      } else {
        break;
      }

      if (!hasConflicts(queens)) {
        solution = queens;
        break;
      }

      iter++;
    }

    if (hasConflicts(queens)) {
      solve(n);
    }
  }

  public static int[] generateBoard(int n) {
    int[] board = new int[n];
    for(int i = 0; i < n; i++) {
      board[i] = Integer.MIN_VALUE;
    }

    board[0] = pickRandom(n);
    for (int i = 1; i < n; i++) {
      int row = getRowWithMinConflicts(board, i, -1).get("row");
      board[i] = row;
    }

    return board;
  }

  public static int pickRandom(int max) {
    Random rand = new Random();
    return rand.nextInt(max);
  }

  public static boolean hasConflicts(int[] queens) {
    int[] conflicts = getConflictsForAllColumns(queens);
    return Arrays.stream(conflicts).anyMatch(q -> q != 0);
  }

  public static HashMap<String, Integer> getRowWithMinConflicts(int[] queens, int queenCol, int queenRow) {
    int minConflicts = Integer.MAX_VALUE;
    ArrayList<Integer> minRows = new ArrayList<>();

    for(int row = 0; row < queens.length; row++) {
      if (row == queenRow) continue;

      int rowConflicts = getConflictsForQueen(queens, queenCol, row);
      if (rowConflicts == minConflicts) {
        minRows.add(row);
      }

      if (rowConflicts < minConflicts) {
        minConflicts = rowConflicts;
        minRows.clear();
        minRows.add(row);
      }
    }

    int rowIndex = 0;
    if (minRows.size() > 1) {
      rowIndex = pickRandom(minRows.size());
    }

    int minRow = minRows.get(rowIndex);
    HashMap<String, Integer> rowMinConflicts = new HashMap<>();
    rowMinConflicts.put("row", minRow);
    rowMinConflicts.put("conflicts", minConflicts);
    return rowMinConflicts;
  }

  public static int getColWithMaxConflicts(int[] conflicts) {
    int max = Integer.MIN_VALUE;
    ArrayList<Integer> maxCols = new ArrayList<>();

    for (int col = 0; col < conflicts.length; col++) {
      if (conflicts[col] == max) {
        maxCols.add(col);
      }

      if (conflicts[col] > max) {
        max = conflicts[col];
        maxCols.clear();
        maxCols.add(col);
      }
    }

    int colIndex = 0;
    if (maxCols.size() > 1) {
      colIndex = pickRandom(maxCols.size());
    }

    return maxCols.get(colIndex);
  }

  public static int getConflictsForQueen(int[] queens, int col, int row) {
    return getDiagonalConflicts(queens, col, row) + getRowConflicts(queens, col, row);
  }

  public static int getDiagonalConflicts(int[] queens, int queenCol, int queenRow) {
    int conflicts = 0;

    for(int col = 0; col < queens.length; col++) {
      if (col == queenCol) continue;
      if (Math.abs(col - queenCol) == Math.abs(queens[col] - queenRow))
        conflicts++;
    }

    return conflicts;
  }

  public static int getRowConflicts(int[] queens, int queenCol, int queenRow) {
    int conflicts = 0;

    for(int col = 0; col < queens.length; col++) {
      if (col == queenCol) continue;
      if (queens[col] == queenRow) conflicts++;
    }

    return conflicts;
  }

  public static void print(int[] queens) {
    for(int row = 0; row < queens.length; row++) {
      for(int col = 0; col < queens.length; col++) {
        if (queens[col] == row) {
          System.out.print("* ");
        } else {
          System.out.print("_ ");
        }
      }

      System.out.println();
    }
  }
}
