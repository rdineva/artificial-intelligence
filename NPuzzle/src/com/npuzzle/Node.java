package com.npuzzle;


import java.util.ArrayList;
import java.util.HashMap;

public class Node {

  public String move;
  private Node parent;
  public Board board, goal;
  public int g;

  public Node(Board board, Node parent, Board goal, int g, String move) {
    this.board = board;
    this.parent = parent;
    this.goal = goal;
    this.g = g;
    this.move = move;
  }

  public int f() {
    return this.g + this.manhattanDistance();
  }

  public int manhattanDistance() {
    int manhattan = 0;

    for(int goalRow = 0; goalRow < this.size(); goalRow++) {
      for (int goalCol = 0; goalCol < this.size(); goalCol++) {
        boolean isFound = false;

        for(int boardRow = 0; boardRow < this.size(); boardRow++) {
          if (isFound) {
            break;
          }

          for (int boardCol = 0; boardCol < this.size(); boardCol++) {
            if (this.board.tileAt(boardRow, boardCol) != 0 && this.board.tileAt(boardRow, boardCol) == this.goal.tileAt(goalRow, goalCol)) {
              manhattan += Math.abs(boardRow - goalRow) + Math.abs(boardCol - goalCol);
              isFound = true;
              break;
            }
          }
        }
      }
    }

    return manhattan;
  }

  public ArrayList<Node> neighbors() {
    ArrayList<Node> neighbors = new ArrayList<>();

    HashMap<String, Board> neighborBoards = this.board.neighbors();
    neighborBoards.forEach((move, neighborBoard) -> {
      Node node = new Node(neighborBoard, this, this.goal, this.g + 1, move);
      neighbors.add(node);
    });

    return neighbors;
  }

  public ArrayList<Node> getPathToRoot() {
    ArrayList<Node> path = new ArrayList<>();
    Node node = this;
    path.add(node);

    while (node.parent != null) {
      path.add(0, node.parent);
      node = node.parent;
    }

    return path;
  }

  private int size() {
    return this.board.size();
  }
}
