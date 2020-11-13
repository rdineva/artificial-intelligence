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
  public int moves;

  public Solver(int n) {
    int moves = 0;
    this.n = n;
    this.queens = new int[n];
    this.solution = new int[n];
    this.rowConflicts = new int[n];
    this.d1Conflicts = new int[2*n-1];
    this.d2Conflicts = new int[2*n-1];
  }

  public void solve() {
    do{
      this.resetData();
      this.generateRandomBoard();
      this.setConflicts();
      if(!this.hasConflicts()){
        this.solution = this.queens;
        break;
      }
      this.search();
    } while(this.hasConflicts());
  }

  public void search() {
    int iter = 0;
    while(iter < this.n) {
      this.moves++;
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
          randomRow = this.pickRandom(this.n);
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
    this.rowConflicts = new int[this.n];
    this.d1Conflicts = new int[2 * this.n - 1];
    this.d2Conflicts = new int[2 * this.n - 1];

    for(int i = 0; i < this.n; i++) {
      this.rowConflicts[this.queens[i]]++;
      this.d1Conflicts[this.n - 1 + i - this.queens[i]]++;
      this.d2Conflicts[this.queens[i] + i]++;
    }
  }

  public HashMap<String, Integer> findMinRow(int queenCol, int queenRow) {
    int minConflicts = Integer.MAX_VALUE;
    ArrayList<Integer> minRows = new ArrayList<>();

    for(int row = 0; row < this.n; row++) {
      if (row == queenRow) continue;

      int newRowConflicts = (this.rowConflicts[row] + 1)/2;
      int newD1Conflicts = (this.d1Conflicts[this.n - 1 + queenCol - row] + 1)/2;
      int newD2Conflicts = (this.d2Conflicts[queenCol + row] + 1)/2;
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
    this.rowConflicts = new int[this.n];
    this.d1Conflicts = new int[2 * this.n - 1];
    this.d2Conflicts = new int[2 * this.n - 1];
  }

  public void generateStaircaseBoard() {
    if (this.n % 6 == 2) {
      this.set2or3RemainderBoard(this.n - 4, this.n - 3);
    } else if (this.n % 6 == 3) {
      this.set2or3RemainderBoard(this.n - 4, this.n - 1);
    } else {
      this.setOtherRemainderBoard();
    }
  }

  public void generateRandomBoard() {
    for(int i = 0; i < this.n; i++) {
      this.queens[i] = Integer.MIN_VALUE;
    }

    int randomFirstRow = pickRandom(n);
    this.queens[0] = randomFirstRow;
    this.addConflicts(0, randomFirstRow);

    for (int i = 1; i < this.n; i++) {
      var minRowData = findMinRow(i, -1);
      int row = minRowData.get("row");
      this.queens[i] = row;
      this.addConflicts(i, row);
    }
  }

  public void set2or3RemainderBoard(int firstCount, int secondCount) {
    for(int i = 0; i < this.n / 2 - 1; i++) {
      this.queens[i] = firstCount;
      firstCount -= 2;
    }

    this.queens[this.n - 1] = this.pickRandom(this.n);

    for(int i = this.n / 2 + 1; i < this.n; i++) {
      this.queens[i] = secondCount;
      secondCount -= 2;
    }

    this.queens[this.n / 2] = this.pickRandom(n);
  }

  public void setOtherRemainderBoard() {
    int c = 0;

    for (int i = 0; i < this.n / 2; i++) {
      this.queens[this.n - 1 - i - this.n / 2] = c;
      this.queens[this.n - i - 1] = c + 1;
      c += 2;
    }
  }

  public boolean hasConflicts() {
    for(int i = 0; i < this.n; i++) {
      if (i < this.n) {
        if (this.rowConflicts[this.queens[i]] > 1) {
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
    return this.rand.nextInt(max);
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
