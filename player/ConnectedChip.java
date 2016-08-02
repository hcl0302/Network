package player;

import java.util.LinkedList;
import java.util.ListIterator;

public class ConnectedChip extends Chip {
  
  //a list of all connected chips
  protected LinkedList<ConnectedChip> connectedChips;
  //its neighbor chip
  //a chip can have no more than one neighbour chip
  protected ConnectedChip neighbourChip;
  
  //constructor
  public ConnectedChip(int x, int y, int color) {
    super(x, y, color);
    connectedChips = new LinkedList<>();
    neighbourChip = null;
  }
  
  //check whether it equals to another chip
  public boolean equals(Chip chip) {
    return this.x == chip.x && this.y == chip.y;
  }
  
  //return the number of its connected chips
  public int connectionsNum() {
    return connectedChips.size();
  }
  
  //return list of its neighbour chips
  public ConnectedChip getNeighbourChip() {
    return this.neighbourChip;
  }
  
  //check whether it is connected with another chip
  public boolean isConnectedWith(ConnectedChip c) {
    return this.connectedChips.contains(c);
  }
  
  //connect this chip with another chip
  //return false if the chip will have two neighbours after connection
  public boolean addConnectedChip(ConnectedChip chip) {
    this.connectedChips.add(chip);
    if (Math.abs(chip.x-this.x) <= 1 && Math.abs(chip.y-this.y) <= 1) {
      if (this.neighbourChip != null) {
        return false;
      }
      this.neighbourChip = chip;
    }
    return true;
  }
  
  //remove a chip from the list of its connected chips
  public boolean removeConnectedChip(Chip chip) {
    ListIterator<ConnectedChip> ite = connectedChips.listIterator();
    while (ite.hasNext()) {
      Chip c = ite.next();
      if (c.equals(chip)) {
        ite.remove();
        if (c == neighbourChip) {
          neighbourChip = null;
        }
        return true;
      }
    }
    return false;
  }
  
  public void removeConnection(ConnectedChip c) {
    this.connectedChips.remove(c);
    if (c == neighbourChip) {
      neighbourChip = null;
    }
  }
  
  public void print() {
    System.out.print("(" + x + ", " + y +") connections: ");
    for (Chip c : connectedChips) {
      System.out.print(c.x + "," + c.y + ";");
    }
    System.out.println();
  }

}

