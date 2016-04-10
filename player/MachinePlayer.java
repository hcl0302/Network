/* MachinePlayer.java */

package player;

import java.util.*;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {
  
  private int machineColor;
  private int humanColor;
  private int searchDepth;
  private Board currentBoard;
  
  public final int MACHINE_WIN = 9999;
  public final int HUMAN_WIN = -9999;

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    this.machineColor = color;
    this.humanColor = color==Board.BLACK? Board.WHITE:Board.BLACK;
    currentBoard = new Board();
  }

  // Creates a machine player with the given color and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
    this(color);
    this.searchDepth = searchDepth;
  }

  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
    List<Move> moves = this.currentBoard.movesGenerator(machineColor);
    ListIterator<Move> ite = moves.listIterator();
    int bestScore = HUMAN_WIN;
    Move bestMove = null;
    while (ite.hasNext()) {
      Move m = ite.next();
      currentBoard.move(m, machineColor);
      int score;
      if (this.searchDepth > 1) {
        score = treeSearch(humanColor, currentBoard, m, 2, bestScore);
      } else {
        score = boardEvaluation(currentBoard);
      }
      if (score > bestScore) {
        bestMove = m;
        bestScore = score;
      }
      currentBoard.retractMove(m);
    }
    if (forceMove(bestMove)) {
      return bestMove;
    } else {
      System.out.println("The chosed move is not valid.");
      return null;
    }
  } 

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.
  public boolean opponentMove(Move m) {
    if (this.currentBoard.isValidMove(humanColor, m)) {
      this.currentBoard.move(m, humanColor);
      return true;
    }
    return false;
  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
    if (this.currentBoard.isValidMove(this.machineColor, m)) {
      this.currentBoard.move(m, this.machineColor);
      return true;
    }
    return false;
  }
  
  //compute an evaluation function for a board
  //return MACHINE_WIN if the Machine player wins, HUMAN_WIN if losing
  //return difference of connected chip numbers if a draw
  public int boardEvaluation(Board board) {
    if (board.success(machineColor)) {
      return MACHINE_WIN;
    } else if (board.success(humanColor)) {
      return HUMAN_WIN;
    } else {
      return board.connectedChipsNum(machineColor) 
          - board.connectedChipsNum(humanColor);
    }
  }
  
  //perform minimax tree search
  private int treeSearch(int color, Board board, Move move, int depth, int oppBest) {
    int opponentColor = color==machineColor? humanColor:machineColor;
    int bestScore = color==machineColor? HUMAN_WIN:MACHINE_WIN;
    List<Move> moves = board.movesGenerator(color);
    ListIterator<Move> ite = moves.listIterator();
    while (ite.hasNext()) {
      Move m = ite.next();
      board.move(m, color);
      int score;
      if (this.searchDepth > depth) {
        score = treeSearch(opponentColor, board, m, 2, bestScore);
      } else {
        score = boardEvaluation(board);
      }
      if (color == machineColor && score > bestScore) {
        bestScore = score;
        if (bestScore >= oppBest) {
          break;
        }
      } else if (color == humanColor && score < bestScore) {
        bestScore = score;
        if (bestScore <= oppBest) {
          break;
        }
      }
      board.retractMove(m);
    }
    return bestScore;
  }

}
