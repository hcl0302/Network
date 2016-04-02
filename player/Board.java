package player;

public class Board {
  
  // a map for a board, -1, 0, 1 indicates black, empty, white;
  private int[][] board;
  
  public final int SIZE = 8;
  
  public final int BLACK = 0;
  public final int WHITE = 1;
  public final int EMPTY = -1;
  public final int DEAD = -2;
  
  
  public Board() {
    board = new int[SIZE][SIZE];
    for (int i = 0; i < SIZE; i++) {
      for (int j = 0; j < SIZE; j++) {
        board[i][j] = EMPTY;
      }
    }
    board[0][0] = DEAD;
    board[0][SIZE - 1] = DEAD;
    board[SIZE - 1][0] = DEAD;
    board[SIZE - 1][SIZE -1] = DEAD;
  }
  
  //Execute a move on the board
  public void move(Move move, int color) {
    
  }
  
  //retract a move
  public void retractMove(Move move) {
    
  }
  
  // check whether the move is legal for a certain player
  public boolean isValidMove(int color, Move m) {
    switch(m.moveKind) {
    case Move.ADD:
      return isEmpty(m.x1, m.y1);
    case Move.STEP:
      if (board[m.x2][m.y2] == color && isEmpty(m.x1, m.y1)) {
        return true;
      } else {
        return false;
      }
    case Move.QUIT:
      return true;
    }
    return false;
  }
  
  //indicate whether a position on the board is empty
  public boolean isEmpty(int x, int y) {
    return board[x][y] == EMPTY;
  }
  
  //indicate whether a position on the board is in dead area
  public boolean isDeadArea(int x, int y) {
    if ((x == 0 || x == SIZE-1) && (y == 0 || y == SIZE-1)) {
      return true;
    } else {
      return false;
    }
  }
  
//indicate whether a position on the board is in goal area
  public boolean isGoalArea(int x, int y, int color) {
    if (color == BLACK && (y==0 || y==SIZE-1) && x!=0 && x!=SIZE-1) {
      return true;
    } else if (color == WHITE && (x==0 || x==SIZE-1) && y!=0 && y!=SIZE-1) {
      return true;
    } else {
      return false;
    }
  }
  
  //finding the chips (of the same color) that form connections with a chip
  private Chip[] findConnectedChips(Chip chip) {
    Chip [] connectedChips = {chip};
    return connectedChips;
  }
  
  //compute an evaluation function for a board
  //return 1 if the Machine player wins, -1 if losing, 0 if a tie
  public int evaluation() {
    return 0;
  }
  
  public void print() {
    for (int i = 0; i < SIZE; i++) {
      for (int j = 0; j < SIZE; j++) {
        switch(board[i][j]) {
        case BLACK:
          System.out.print("X");
          break;
        case WHITE:
          System.out.print("O");
          break;
        default:
          System.out.print(" ");
        }
      }
      System.out.println();
    }
  }
}

class Chip {
  public int color;
  public int x;
  public int y;
  
  public Chip(int x, int y, int color) {
    this.x = x;
    this.y = y;
    this.color = color;
  }
}
