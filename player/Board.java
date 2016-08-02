package player;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

public class Board {
  
  public final int SIZE = 8;
  
  public static final int BLACK = 0;
  public static final int WHITE = 1;
  public final int EMPTY = -1;
  public final int DEAD = -2;
  
  //represent a board
  private int[][] board;

  //save chips as a list, chips in the goal area are expected to 
  //be in the front of the list
  private LinkedList<ConnectedChip> blackChips;
  private LinkedList<ConnectedChip> whiteChips;
  private LinkedList<ConnectedChip> allChips;
  
  //TODO: save all adds and removes of chips in it
  protected Stack<LinkedList<MoveEffect>> moveEffects;
  
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
    allChips = new LinkedList<>();
    moveEffects = new Stack<>();
  }
  
  //Execute a move on the board
  //return false if a group of three chips is formed after the move
  public boolean move(Move m, int color) {
    ConnectedChip addedChip = null;
    switch(m.moveKind) {
      case Move.ADD:
        board[m.x1][m.y1] = color;
        addedChip = new ConnectedChip(m.x1, m.y1, color);
        if (!addChip(addedChip)) {
          this.moveEffects.push(null);
          return false;
        }
        //After an add-kind move, some connections may be broken
        checkConnections(addedChip);
        break;
      case Move.STEP:
        board[m.x2][m.y2] = EMPTY;
        removeChip(new Chip(m.x2, m.y2, color));
        board[m.x1][m.y1] = color;
        addedChip = new ConnectedChip(m.x1, m.y1, color);
        if (!addChip(addedChip)) {
          this.moveEffects.push(null);
          return false;
        }
        //After a step-kind move, some connections may be broken and some may be added
        updateConnections(addedChip);
        break;
      case Move.QUIT:
        break;
    }
    return true;
  }
  
  //retract a move
  public void retractMove(Move m) {
    switch(m.moveKind) {
      case Move.ADD:
        removeChip(new Chip(m.x1, m.y1, board[m.x1][m.y1]));
        board[m.x1][m.y1] = EMPTY;
        break;
      case Move.STEP:
        int color = board[m.x1][m.y1];
        board[m.x2][m.y2] = color;
        addChip(new ConnectedChip(m.x2, m.y2, color));
        board[m.x1][m.y1] = EMPTY;
        removeChip(new Chip(m.x1, m.y1, color));
        break;
      default:
          return;
    }
    restoreConnections();
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
      return board[m.x2][m.y2] == color && isEmpty(m.x1, m.y1) 
      && isGoalArea(m.x1, m.y1, oppositeColor)==0;
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
    int count = 0;
    for (ConnectedChip c : chips) {
      count += c.connectionsNum();
    }
    return count;
  }
  
  //find the number of adjacent chips 
  //before add a new chip at a position
  //need to do: now it may count duplicate chips
  public void findAdjacentChips(int x, int y, int color) {
//    LinkedList<ConnectedChip> chips = (color==BLACK? this.blackChips:this.whiteChips);
//    LinkedList<ConnectedChip> neighbours = new LinkedList<>();
//    for (ConnectedChip c : chips) {
//      if (Math.abs(c.x-x) <= 1 && Math.abs(c.y-y) <= 1) {
//        count = count + 1 + c.neighboursNum();
//      }
//    }
//    return neighbours;
  }
  
  //check whether two chips are connected
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
  
  //Check whether a connection between chip1 and chip2 is broken after a new chip3 is added.
  private boolean connectionBroken(Chip c1, Chip c2, Chip c3) {
    if (c1.x == c2.x) {
      return c3.x==c1.x && c3.y>Math.min(c1.y, c2.y) && c3.y<Math.max(c1.y, c2.y);
    } else if (c1.y == c2.y) {
      return c3.y==c1.y && c3.x>Math.min(c1.x, c2.x) && c3.x<Math.max(c1.x, c2.x);
    } else if (c3.x == c1.x || c3.x == c2.x) {
      return false;
    } else {
      return (c3.y-c1.y)/(c3.x-c1.x) == (c2.y-c1.y)/(c2.x-c1.x) 
          && (c3.y-c2.y)/(c3.x-c2.x) == (c1.y-c2.y)/(c1.x-c2.x);
    }
  }
  
  //After an add-kind move, some connections may be broken
  //Fix bug: both black chips and white chips can be affected
  private void checkConnections(Chip addedChip) {
    LinkedList<MoveEffect> meList = new LinkedList<>();
    for (ConnectedChip chip : this.allChips) {
      if (chip == addedChip) {
        continue;
      }
      for (ConnectedChip c: chip.connectedChips) {
        if (c!=addedChip && connectionBroken(chip, c, addedChip)) {
          //System.out.println("Unconnected: " + chip.toString() + c.toString());
          chip.removeConnection(c);
          c.removeConnection(chip);
          meList.add(new MoveEffect(chip, c, false));
          break;
        }
      }
    }
    this.moveEffects.push(meList);
  }
  
  //After a step-kind move, some connections may be broken and some may be added
  //Fix bug: both black chips and white chips can be affected
  private void updateConnections(Chip addedChip) {
    LinkedList<MoveEffect> meList = new LinkedList<>();
    for (ConnectedChip chip : this.allChips) {
      if (chip == addedChip) {
        continue;
      }
      for (ConnectedChip otherChip: this.allChips) {
        if (otherChip == addedChip) {
          continue;
        }
        if (otherChip != chip) {
          if (chip.isConnectedWith(otherChip)) {
            if (connectionBroken(chip, otherChip, addedChip)) {
              chip.removeConnection(otherChip);
              otherChip.removeConnection(chip);
              meList.add(new MoveEffect(chip, otherChip, false));
            }
          } else if (isConnectedChips(chip, otherChip)) {
            chip.addConnectedChip(otherChip);
            otherChip.addConnectedChip(chip);
            meList.add(new MoveEffect(chip, otherChip, true));
          }
        }
      }
    }
    this.moveEffects.push(meList);
  }
  
  private void restoreConnections() {
    LinkedList<MoveEffect> meList = this.moveEffects.pop();
    if (meList != null && !meList.isEmpty()) {
      for (MoveEffect me: meList) {
        me.restoreMoveEffect();
      }
    }
  }
  
  //add a new chip to the board
  //update connections of the new chip
  //return false if one chip have two neighbours after adding the chip
  public boolean addChip(ConnectedChip chip) {
    LinkedList<ConnectedChip> chips = (chip.color == BLACK? this.blackChips:this.whiteChips);
    for  (ConnectedChip otherChip : chips) {
      if (isConnectedChips(chip, otherChip)) {
        if (!chip.addConnectedChip(otherChip) || !otherChip.addConnectedChip(chip)) {
          //the chip will have two neighbours after connection
          return false;
        }
      }
    }
    if (isGoalArea(chip.x, chip.y, chip.color)!=0) {
      chips.addFirst(chip);;
    } else {
      chips.add(chip);
    }
    allChips.add(chip);
    return true;
  }
  
  public void removeChip(Chip chip) {
    LinkedList<ConnectedChip> chips = (chip.color == BLACK? this.blackChips:this.whiteChips);
    //System.out.println("removeChip: " + chip.x + ","+chip.y+","+ chip.color);
    ListIterator<ConnectedChip> ite = chips.listIterator();
    while (ite.hasNext()) {
      ConnectedChip c = ite.next();
      //c.print();
      if (c.equals(chip)) {
        //System.out.println("find and remove");
        ite.remove();
        allChips.remove(c);
      } else {
        c.removeConnectedChip(chip);
        //c.print();
      }
    }
  }
  
  //check whether a player wins
  public boolean success(int color) {
    LinkedList<ConnectedChip> chips = color==BLACK?blackChips:whiteChips;
    if (chips.size() < 6) {
      return false;
    }
    for (ConnectedChip c : chips) {
      if (isGoalArea(c.x, c.y, color) == 1) {
        LinkedList<ConnectedChip> network = new LinkedList<ConnectedChip>();
        network.add(c);
        if (formNetwork(c, network)) {
          return true;
        }
      }
    }
    return false;
  }
  
  //the network starts from a up or left goal area and ends in the other goal area
  //return true if a valid network can be formed, otherwise false
  private boolean formNetwork(ConnectedChip start, LinkedList<ConnectedChip> network) {
    LinkedList<ConnectedChip> neighbours = start.connectedChips;
    for (Chip c : neighbours) {
      if (isGoalArea(c.x, c.y, c.color) == -1) {
        network.add((ConnectedChip) c);
//        System.out.print("Network: ");
//        for (Chip a:network) {
//          System.out.print(a.x + "," + a.y +";");
//        }
//        System.out.println();
        if (isValidNetwork(network)) {
          return true;
        } else {
          network.removeLast();
        }
      } else if (isGoalArea(c.x, c.y, c.color)!=1 && !network.contains((ConnectedChip) c)) {
        //only head and end of the network can be in the goal area
        //not allowed to pass a chip more than once
        network.add((ConnectedChip) c);
        if (formNetwork((ConnectedChip) c, network)) {
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
          //modify: don't check adjacent chips here
          //instead, check that after the move is made
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
            for (Chip c : chips) {
              moves.add(new Move(i, j, c.x, c.y));
            }
          }
        }
      }
    }
    return moves;
  }
  
  public void print() {
    for (int j = 0; j < SIZE; j++) {
      for (int i = 0; i < SIZE; i++) {
        switch(board[i][j]) {
        case BLACK:
          System.out.print("X");
          break;
        case WHITE:
          System.out.print("O");
          break;
        default:
          System.out.print(".");
        }
      }
      System.out.println();
    }
    System.out.println("Black Chips: ");
    for (ConnectedChip c : this.blackChips) {
      c.print();
    }
    System.out.println("White Chips: ");
    for (ConnectedChip c : this.whiteChips) {
      c.print();
    }
  }
}

class MoveEffect {
  //used to save the effect of a move on the connection of two chips
  
  public ConnectedChip chip1;
  public ConnectedChip chip2;
  //indicate the change of the connection between chip1 and chip2 
  //true if a new connection is created
  //false if an old connection is broken
  public boolean makeConnection;
  
  public MoveEffect(ConnectedChip c1, ConnectedChip c2, boolean mc) {
    chip1 = c1;
    chip2 = c2;
    makeConnection = mc;
  }
  
  public void restoreMoveEffect() {
    if (makeConnection) {
      //need to remove the new created connection
      chip1.removeConnection(chip2);
      chip2.removeConnection(chip1);
    } else {
      //need to add the removed connection
      chip1.addConnectedChip(chip2);
      chip2.addConnectedChip(chip1);
    }
  }
}

