package com.nqueens;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Solver {
  public int n;
  public int[] queens;
  public int[] solution;
  public int[] rowConflicts;
  public int[] d1Conflicts;
  public int[] d2Conflicts;
  public Random rand = new Random();

  public Solver(int n) {
    this.n = n;
    this.queens = new int[n];
    this.solution = new int[n];
    this.rowConflicts = new int[n];
    this.d1Conflicts = new int[2*n-1];
    this.d2Conflicts = new int[2*n-1];
  }

  public void solve() {
    this.resetData();
    this.generateBoard();

    if (!this.hasConflicts()) {
      this.solution = this.queens;
      return;
    }

    int iter = 0;
    while(iter < this.n) {
      HashMap<String, Integer> colMaxConflictsData = this.getColMaxConflicts();
      int colMaxConflicts = colMaxConflictsData.get("col");
      int currentConflicts = colMaxConflictsData.get("conflicts");

      HashMap<String, Integer> rowMinConflictsData = this.findMinRow(colMaxConflicts, this.queens[colMaxConflicts]);
      int rowMinConflicts = rowMinConflictsData.get("row");
      int minConflicts = rowMinConflictsData.get("conflicts");

      int oldRow = this.queens[colMaxConflicts];
      int newRow = rowMinConflicts;

      if (currentConflicts == minConflicts) {
        int randomRow;
        do {
          randomRow = pickRandom(this.n);
          newRow = randomRow;
        } while(randomRow == this.queens[colMaxConflicts]);

        this.queens[colMaxConflicts] = randomRow;
      } else if (currentConflicts > minConflicts) {
        this.queens[colMaxConflicts] = rowMinConflicts;
      } else {
        break;
      }

      this.updateConflicts(colMaxConflicts, oldRow, newRow);

      if (!this.hasConflicts()) {
        this.solution = this.queens;
        break;
      }

      iter++;
    }

    if (this.hasConflicts()) {
      solve();
    }
  }

  public void updateConflicts(int col, int oldRow, int newRow) {
    this.removeConflicts(col, oldRow);
    this.addConflicts(col, newRow);
  }

  public void removeConflicts(int col, int row) {
    this.rowConflicts[row]--;
    this.d1Conflicts[this.n - 1 + col - row]--;
    this.d2Conflicts[col + row]--;
  }

  public void addConflicts(int col, int row) {
    this.rowConflicts[row]++;
    this.d1Conflicts[this.n - 1 + col - row]++;
    this.d2Conflicts[col + row]++;
  }

  public HashMap<String, Integer> getColMaxConflicts() {
    int maxConflicts = Integer.MIN_VALUE;
    ArrayList<Integer> maxCols = new ArrayList<>();

    for(int i = 0; i < this.n; i++) {
      int currentConflicts = this.getConflictsForQueen(i, this.queens[i]);
      if (currentConflicts == maxConflicts) {
        maxCols.add(i);
      }

      if (currentConflicts > maxConflicts) {
        maxConflicts = currentConflicts;
        maxCols.clear();
        maxCols.add(i);
      }
    }

    int colIndex = 0;
    if (maxCols.size() > 1) {
      colIndex = this.pickRandom(maxCols.size());
    }

    int maxCol = maxCols.get(colIndex);
    HashMap<String, Integer> colMaxConflicts = new HashMap<>();
    colMaxConflicts.put("col", maxCol);
    colMaxConflicts.put("conflicts", maxConflicts);
    return colMaxConflicts;
  }

  public void setConflicts() {
    this.rowConflicts = new int[n];
    this.d1Conflicts = new int[2 * n - 1];
    this.d2Conflicts = new int[2 * n - 1];

    for(int i = 0; i < this.n; i++) {
      this.rowConflicts[queens[i]]++;
      this.d1Conflicts[this.n - 1 + i - this.queens[i]]++;
      this.d2Conflicts[queens[i] + i]++;
    }
  }

  public HashMap<String, Integer> findMinRow(int queenCol, int queenRow) {
    int minConflicts = Integer.MAX_VALUE;
    ArrayList<Integer> minRows = new ArrayList<>();

    for(int row = 0; row < this.n; row++) {
      if (row == queenRow) continue;

      int newRowConflicts = (rowConflicts[row] + 1)/2;
      int newD1Conflicts = (d1Conflicts[this.n - 1 + queenCol - row] + 1)/2;
      int newD2Conflicts = (d2Conflicts[queenCol + row] + 1)/2;
      int newConflicts = newRowConflicts + newD1Conflicts + newD2Conflicts;

      if (minConflicts == newConflicts) {
        minRows.add(row);
      }

      if (newConflicts < minConflicts) {
        minConflicts = newConflicts;
        minRows.clear();
        minRows.add(row);
      }
    }

    int rowIndex = 0;
    if (minRows.size() > 1) {
      rowIndex = this.pickRandom(minRows.size());
    }

    int minRow = minRows.get(rowIndex);
    HashMap<String, Integer> rowMinConflicts = new HashMap<>();
    rowMinConflicts.put("row", minRow);
    rowMinConflicts.put("conflicts", minConflicts);
    return rowMinConflicts;
  }

  public int getConflictsForQueen(int col, int row) {
    int currentQueenOccurrences = 0;
    int rowConflictQueens = this.rowConflicts[row];
    int d1ConflictQueens = this.d1Conflicts[this.n - 1 + row - col];
    int d2ConflictQueens = this.d2Conflicts[row + col];

    int conflicts = rowConflictQueens + d1ConflictQueens + d2ConflictQueens;
    if (rowConflictQueens != 0) {
      currentQueenOccurrences++;
    }
    if (d1ConflictQueens != 0) {
      currentQueenOccurrences++;
    }
    if (d2ConflictQueens != 0) {
      currentQueenOccurrences++;
    }

    conflicts -= currentQueenOccurrences;
    return conflicts;
  }

  public void resetData() {
    this.queens = new int[this.n];
    this.rowConflicts = new int[n];
    this.d1Conflicts = new int[2 * n - 1];
    this.d2Conflicts = new int[2 * n - 1];
  }

  public void generateBoard() {
    for(int i = 0; i < n; i++) {
      this.queens[i] = Integer.MIN_VALUE;
    }

    int randomFirstRow = pickRandom(n);
    this.queens[0] = randomFirstRow;
    addConflicts(0, randomFirstRow);

    for (int i = 1; i < n; i++) {
      var minRowData = findMinRow(i, -1);
      int row = minRowData.get("row");
      this.queens[i] = row;
      addConflicts(i, row);
    }
  }

  public boolean hasConflicts() {
    for(int i = 0; i < this.n; i++) {
      if (i < this.n) {
        if (this.rowConflicts[queens[i]] > 1) {
          return true;
        }
      }

      if (this.d1Conflicts[this.n - 1 + i - this.queens[i]] > 1 || this.d2Conflicts[queens[i] + i] > 1) {
        return true;
      }
    }

    return false;
  }

  public int pickRandom(int max) {
    return rand.nextInt(max);
  }

  public void print(int[] queens) {
    System.out.println();
    for(int row = 0; row < this.n; row++) {
      for(int col = 0; col < this.n; col++) {
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
