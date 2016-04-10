package player;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Board {
  
  public final int SIZE = 8;
  
  public static final int BLACK = 0;
  public static final int WHITE = 1;
  public final int EMPTY = -1;
  public final int DEAD = -2;
  
  //represent a board, -1, 0, 1 indicates black, empty, white;
  private int[][] board;

  //save chips as a list, chips in the goal area are expected to 
  //be in the front of the list
  private LinkedList<ConnectedChip> blackChips;
  private LinkedList<ConnectedChip> whiteChips;
  
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
    blackChips = new LinkedList<>();
    whiteChips = new LinkedList<>();
  }
  
  //Execute a move on the board
  public void move(Move m, int color) {
    switch(m.moveKind) {
    case Move.ADD:
      board[m.x1][m.y1] = color;
      addChip(new ConnectedChip(m.x1, m.y1, color));
      break;
    case Move.STEP:
      board[m.x2][m.y2] = EMPTY;
      removeChip(new Chip(m.x2, m.y2, color));
      board[m.x1][m.y1] = color;
      addChip(new ConnectedChip(m.x1, m.y1, color));
      break;
    case Move.QUIT:
      break;
    }
  }
  
  //retract a move
  public void retractMove(Move m) {
    switch(m.moveKind) {
    case Move.ADD:
      removeChip(new Chip(m.x2, m.y2, board[m.x1][m.y1]));
      board[m.x1][m.y1] = EMPTY;
      break;
    case Move.STEP:
      int color = board[m.x1][m.y1];
      board[m.x2][m.y2] = color;
      addChip(new ConnectedChip(m.x2, m.y2, color));
      board[m.x1][m.y1] = EMPTY;
      removeChip(new Chip(m.x2, m.y2, color));
      break;
    }
  }
  
  // check whether the move is legal for a certain player
  public boolean isValidMove(int color, Move m) {
    if (m == null) {
      return false;
    }
    int oppositeColor = color==BLACK? WHITE:BLACK;
    switch(m.moveKind) {
    case Move.ADD:
      return isEmpty(m.x1, m.y1) && isGoalArea(m.x1, m.y1, oppositeColor)==0;
    case Move.STEP:
      if (board[m.x2][m.y2] == color && isEmpty(m.x1, m.y1) 
      && isGoalArea(m.x1, m.y1, oppositeColor)==0) {
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
  
  //indicate whether a position is in dead area
  //areas that is out of the boarder is also thought to be dead area
  public boolean isDeadArea(int x, int y) {
    if ((x == 0 || x == SIZE-1) && (y == 0 || y == SIZE-1)) { //dead area
      return true;
    } else if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) { //out of the boarder
      return true;
    } else {
      return false;
    }
  }
  
  //indicate whether a position on the board is in goal area
  //return 0 if not in goal area
  //return 1 if in up or left goal area
  //return -1 if in down or right goal area
  public int isGoalArea(int x, int y, int color) {
    if (color == BLACK && (y==0 || y==SIZE-1) && x!=0 && x!=SIZE-1) {
      return y==0? 1:-1;
    } else if (color == WHITE && (x==0 || x==SIZE-1) && y!=0 && y!=SIZE-1) {
      return x==0? 1:-1;
    } else {
      return 0;
    }
  }
  
  //return the number of connected chips for a color
  public int connectedChipsNum(int color) {
    LinkedList<ConnectedChip> chips = (color==BLACK? this.blackChips:this.whiteChips);
    ListIterator<ConnectedChip> ite = chips.listIterator();
    int count = 0;
    while (ite.hasNext()) {
      ConnectedChip c = ite.next();
      count += c.connectionsNum();
    }
    return count;
  }
  
  //find the number of adjacent chips
  public int findAjacentChips(int x, int y, int color) {
    LinkedList<ConnectedChip> chips = (color==BLACK? this.blackChips:this.whiteChips);
    ListIterator<ConnectedChip> ite = chips.listIterator();
    int count = 0;
    while (ite.hasNext()) {
      ConnectedChip c = ite.next();
      if (Math.abs(c.x-x) <= 1 && Math.abs(c.y-y) <= 1) {
        count = count + 1 + c.neighboursNum();
      }
    }
    return count;
  }
  
  public boolean isConnectedChips(Chip c1, Chip c2) {
    if (c1.x == c2.x) {
      int y = Math.min(c1.y, c2.y) + 1;
      for (; y < Math.max(c1.y, c2.y); y++) {
        if (board[c1.x][y] != EMPTY) {
          return false;
        }
      }
      return true;
    } else if (c1.y == c2.y) {
      int x = Math.min(c1.x, c2.x) + 1;
      for (; x < Math.max(c1.x, c2.x); x++) {
        if (board[x][c1.y] != EMPTY) {
          return false;
        }
      }
      return true;
    } else if (Math.abs(c1.y-c2.y) == Math.abs(c1.x-c2.x)) {
      int j = (c2.y-c1.y>0? 1:-1);
      int i = (c2.x-c1.x>0? 1:-1);
      int x = c1.x + i, y = c1.y + j;
      while (x < c2.x) {
        if (board[x][y] != EMPTY) {
          return false;
        }
        x += i;
        y += j;
      }
      return true;
    }
    return false;
  }
  
  public void addChip(ConnectedChip chip) {
    LinkedList<ConnectedChip> chips = (chip.color == BLACK? this.blackChips:this.whiteChips);
    ListIterator<ConnectedChip> ite = chips.listIterator();
    while (ite.hasNext()) {
      ConnectedChip otherChip = ite.next();
      if (isConnectedChips(chip, otherChip)) {
        chip.addConnectedChip(otherChip);
        otherChip.addConnectedChip(chip);
      }
    }
    if (isGoalArea(chip.x, chip.y, chip.color)!=0) {
      chips.addFirst(chip);;
    } else {
      chips.add(chip);
    }
  }
  
  public void removeChip(Chip chip) {
    LinkedList<ConnectedChip> chips = (chip.color == BLACK? this.blackChips:this.whiteChips);
    ListIterator<ConnectedChip> ite = chips.listIterator();
    while (ite.hasNext()) {
      ConnectedChip c = ite.next();
      if (c.equals(chip)) {
        chips.remove(c);
      } else {
        c.removeConnectedChip(chip);
      }
    }
  }
  
  //form a network
  public boolean success(int color) {
    LinkedList<ConnectedChip> chips = color==BLACK?blackChips:whiteChips;
    ListIterator<ConnectedChip> ite = chips.listIterator();
    while (ite.hasNext()) {
      ConnectedChip c = ite.next();
      if (isGoalArea(c.x, c.y, color) == 1) {
        if (formNetwork(c, new LinkedList<ConnectedChip>())) {
          return true;
        }
      }
    }
    return false;
  }
  
  //the network starts from a up or left goal area and ends in the other goal area
  //return true if a valid network can be formed, otherwise false
  private boolean formNetwork(ConnectedChip start, LinkedList<ConnectedChip> network) {
    LinkedList<Chip> neighbours = start.connectedChips;
    ListIterator<Chip> ite = neighbours.listIterator();
    while (ite.hasNext()) {
      ConnectedChip c = (ConnectedChip)ite.next();
      if (isGoalArea(c.x, c.y, c.color) == -1) {
        network.add(c);
        if (isValidNetwork(network)) {
          return true;
        } else {
          network.removeLast();
        }
      } else if (isGoalArea(c.x, c.y, c.color)!=1 && !network.contains(c)) {
        //only head and end of the network can be in the goal area
        //not allowed to pass a chip more than once
        network.add(c);
        if (formNetwork(c, network)) {
          return true;
        }
        network.removeLast();
      }
    }
    return false;
  }
  
  //check whether a network is valid
  private boolean isValidNetwork(LinkedList<ConnectedChip> network) {
    if (network.size() < 6) {
      return false;
    }
    ListIterator<ConnectedChip> ite = network.listIterator();
    int slope1, slope2;
    Chip c1 = ite.next();
    Chip c2 = ite.next();
    if (c2.x == c1.x) {
      slope1 = 100; //infinite
    } else {
      slope1 = (c2.y - c1.y)/(c2.x - c1.x);
    }
    while (ite.hasNext()) {
      Chip c3 = ite.next();
      if (c3.x == c2.x) {
        slope2 = 100; //infinite
      } else {
        slope2 = (c3.y - c2.y)/(c3.x - c2.x);
      }
      if (slope1 == slope2) {
        return false;
      }
      slope1 = slope2;
      c1 = c2;
      c2 = c3;
    }
    return true;
  }
  
  
  // generating a list of all valid moves
  public List<Move> movesGenerator(int color) {
    LinkedList<ConnectedChip> chips = color==BLACK? blackChips:whiteChips;
    int opposite = color==BLACK? WHITE:BLACK;
    List<Move> moves = new LinkedList<>();
    if (chips.size() < 10) {
      //move kind is add
      for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
          if (this.board[i][j]==EMPTY && isGoalArea(i,j,opposite)==0) {
            moves.add(new Move(i, j));
          }
        }
      }
    } else {
      //move kind is step
      for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
          if (this.board[i][j]==EMPTY && isGoalArea(i,j,opposite)==0) {
            ListIterator<ConnectedChip> ite = chips.listIterator();
            while (ite.hasNext()) {
              Chip c = ite.next();
              moves.add(new Move(i, j, c.x, c.y));
            }
          }
        }
      }
    }
    return moves;
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

