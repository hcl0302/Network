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
  public final int SEARCHDEPTH = 4;

  /**
   * Creates a machine player with the given color.
   * @param color  The chip color of the machine player
   */
  public MachinePlayer(int color) {
    this.machineColor = color;
    this.humanColor = color==Board.BLACK? Board.WHITE:Board.BLACK;
    currentBoard = new Board();
    this.searchDepth = SEARCHDEPTH;
  }

  /**
   * Creates a machine player with the given color.
   * @param color  The chip color of the machine player
   * @param searchDepth  The search depth of the DFS algorithm
   */
  public MachinePlayer(int color, int searchDepth) {
    this(color);
    this.searchDepth = searchDepth;
  }

  /**
   * Find the optimal move by DFS and make the move.
   * @return Move The optimal move for the machine player
   */
  public Move chooseMove() {
    List<Move> moves = this.currentBoard.movesGenerator(machineColor);
    ListIterator<Move> ite = moves.listIterator();
    int bestScore = Integer.MIN_VALUE;
    Move bestMove = null;
    while (ite.hasNext()) {
      Move m = ite.next();
      if (!currentBoard.move(m, machineColor)) {
        currentBoard.retractMove(m);
        continue;
      }
      //currentBoard.print();
      int score = boardEvaluation(currentBoard);
      if (score!=MACHINE_WIN && score!=HUMAN_WIN && this.searchDepth > 1) {
        score = treeSearch(humanColor, currentBoard, 2, bestScore);
      }
      //System.out.println("Score: " + score);
      if (score > bestScore) {
        bestMove = m;
        bestScore = score;
        System.out.println("best score: " + bestScore);
        System.out.println(bestMove.toString());
      }
      currentBoard.retractMove(m);
      //currentBoard.print();
    }
    if (forceMove(bestMove)) {
      return bestMove;
    } else {
      System.out.println("The chosed move is not valid.");
      return null;
    }
  } 

  /**
   * Make the opponent do the given move.
   * This method allows your opponents to inform you of their moves.
   * @param m  A move of the opponent
   * @return true if the move is valid, otherwise false
   */
  public boolean opponentMove(Move m) {
    if (this.currentBoard.isValidMove(humanColor, m)) {
      this.currentBoard.move(m, humanColor);
      return true;
    }
    return false;
  }

  /**
   * Make the machine player do the given move.
   * If the Move m is legal, records the move as a move by "this" player
   * (updates the internal game board) and returns true.  If the move is
   * illegal, returns false without modifying the internal state of "this"
   * player.  
   * This method is used to help set up "Network problems".
   * @param m  A move of the machine player
   */
  public boolean forceMove(Move m) {
    if (this.currentBoard.isValidMove(this.machineColor, m)) {
      this.currentBoard.move(m, this.machineColor);
      currentBoard.print();
      return true;
    }
    return false;
  }
  
  /**
   * compute an evaluation function for a board
   * return MACHINE_WIN if the Machine player wins, HUMAN_WIN if losing
   * return difference of connected chip numbers if a draw
   * @param board  The board to be evaluated.
   * @return the evaluation result
   */
  public int boardEvaluation(Board board) {
    if (board.success(machineColor)) {
      //System.out.println("machine can win");
      return MACHINE_WIN;
    } else if (board.success(humanColor)) {
      //System.out.println("human can win");
      return HUMAN_WIN;
    } else {
      return board.connectedChipsNum(machineColor) 
          - board.connectedChipsNum(humanColor);
    }
  }
  
  /**
   * Perform miniMax tree search
   * @param color  The chip color of the player
   * @param board  The current game board
   * @param depth  Current search depth
   * @param opponentBestScore  The current best score of the opponent 
   * @return The best score of the current player
   */
  private int treeSearch(int color, Board board, int depth, int opponentBestScore) {
    int opponentColor = color==machineColor? humanColor:machineColor;
    int bestScore = color==machineColor? Integer.MIN_VALUE:Integer.MAX_VALUE;
    List<Move> moves = board.movesGenerator(color);
    ListIterator<Move> ite = moves.listIterator();
    while (ite.hasNext()) {
      Move m = ite.next();
      if (!board.move(m, color)) {
        board.retractMove(m);
        continue;
      }
      int score = boardEvaluation(board);
      if (score == MACHINE_WIN) {
        //assign a higher score to a win in smaller moves
        score -= depth;
      } else if (score == HUMAN_WIN) {
        score += depth;
      } else if (this.searchDepth > depth) {
        //no one wins now, search deeper
        score = treeSearch(opponentColor, board, depth+1, bestScore);
      }
      //System.out.println("Tree search Score: " + score);
      if (color == machineColor && score > bestScore) {
        bestScore = score;
        if (bestScore >= opponentBestScore) {
          board.retractMove(m);
          break;
        }
      } else if (color == humanColor && score < bestScore) {
        bestScore = score;
        if (bestScore <= opponentBestScore) {
          board.retractMove(m);
          break;
        }
      }
      board.retractMove(m);
    }
    return bestScore;
  }

}
