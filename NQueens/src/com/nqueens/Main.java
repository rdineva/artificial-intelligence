package com.nqueens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;

public class Main {

  public static void main(String[] args) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    int n = Integer.valueOf(reader.readLine());

    Solver solver = new Solver(n);
    Instant start = Instant.now();
    solver.solve();
    Instant finish = Instant.now();

    long timeElapsed = Duration.between(start, finish).toSeconds();
    System.out.println("Time elapsed: " + timeElapsed);
    System.out.println("Moves: " + solver.moves);
    if (n <= 50) {
      solver.print(solver.solution);
    }
  }
}