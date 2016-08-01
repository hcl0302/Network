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
  public final int SEARCHDEPTH = 5;

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    this.machineColor = color;
    this.humanColor = color==Board.BLACK? Board.WHITE:Board.BLACK;
    currentBoard = new Board();
    this.searchDepth = SEARCHDEPTH;
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
    currentBoard.print();
    while (ite.hasNext()) {
      Move m = ite.next();
      if (!currentBoard.move(m, machineColor)) {
        currentBoard.retractMove(m);
        continue;
      }
      //currentBoard.print();
      int score = boardEvaluation(currentBoard);
      if (score!=MACHINE_WIN && score!=HUMAN_WIN && this.searchDepth > 1) {
        score = treeSearch(humanColor, currentBoard, m, 2, bestScore);
      }
      //System.out.println("Score: " + score);
      if (score > bestScore) {
        bestMove = m;
        bestScore = score;
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
  private int treeSearch(int color, Board board, Move move, int depth, int opponentBestScore) {
    int opponentColor = color==machineColor? humanColor:machineColor;
    int bestScore = color==machineColor? HUMAN_WIN:MACHINE_WIN;
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
        score = treeSearch(opponentColor, board, m, depth+1, bestScore);
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
