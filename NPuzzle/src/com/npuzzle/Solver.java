package com.npuzzle;

import java.util.ArrayList;

public class Solver {

  public Node start, goal;

  public Solver(Node start, Node goal) {
    this.start = start;
    this.goal = goal;
  }

  public Node ida() {
    int bound = this.start.f();
    ArrayList<Node> path = new ArrayList<>();
    path.add(0, this.start);

    int nextBound;
    while (true) {
      nextBound = search(path, bound);

      if (nextBound == 0) return path.get(path.size() - 1);

      bound = nextBound;
    }
  }

  public int search(ArrayList<Node> path, int bound) {
    Node currentNode = path.get(path.size() - 1);

    if (currentNode.f() > bound) return currentNode.f();
    if (isGoal(currentNode)) return 0;

    int minF = Integer.MAX_VALUE;
    ArrayList<Node> neighbors = currentNode.neighbors();

    for (Node neighbor: neighbors) {
      if (!path.contains(neighbor)) {
        path.add(neighbor);

        int nextMinF = search(path, bound);
        if (nextMinF == 0) return 0;
        if (nextMinF < minF) minF = nextMinF;

        path.remove(path.size() - 1);
      }
    }

    return minF;
  }

  public boolean isGoal(Node node) {
    for (int row = 0; row < this.size(); row++) {
      for (int col = 0; col < this.size(); col++) {
        if (node.board.tileAt(row, col) != this.goal.board.tileAt(row, col)) return false;
      }
    }

    return true;
  }

  private int size() {
    return this.start.board.size();
  }

  /*
    public boolean isSolvable()
  */
}
