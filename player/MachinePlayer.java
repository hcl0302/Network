/* MachinePlayer.java */

package player;

import java.util.*;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */
public class MachinePlayer extends Player {

  public final int BLACK = 0;
  public final int WHITE = 1;
  
  private int color;
  private int searchDepth;
  private Board currentBoard;
  private Stack<Move> tryMoves;
  
  public final int MACHINE_WIN = 9999;
  public final int HUMAN_WIN = 9999;

  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    this.color = color;
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
    Move bestMove = treeSearch(this.color, this.currentBoard);
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
    int opponentColor = color==BLACK? WHITE:BLACK;
    if (this.currentBoard.isValidMove(opponentColor, m)) {
      this.currentBoard.move(m, opponentColor);
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
    if (this.currentBoard.isValidMove(this.color, m)) {
      this.currentBoard.move(m, this.color);
      return true;
    }
    return false;
  }
  
  // generating a list of all valid moves
  private Move[] movesGenerator(int color, Board board) {
    Move[] moves = {new Move()};
    return moves;
  }
  
  
  //compute an evaluation function for a board
  //return 1 if the Machine player wins, -1 if losing
  //return connected chip numbers if a draw
  public int evaluation(int color) {
    if (this.currentBoard.formNetwork(color)) {
      return color==this.color? MACHINE_WIN:HUMAN_WIN;
    } else {
      int opponentColor = color==BLACK? WHITE:BLACK;
      return this.currentBoard.connectedChipsNum(color) 
          - this.currentBoard.connectedChipsNum(opponentColor);
    }
  }
  
  //perform minimax tree search
  private Move treeSearch(int color, Board board) {
    return new Move();
  }

}
