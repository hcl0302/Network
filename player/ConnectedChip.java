package player;

import java.util.LinkedList;
import java.util.ListIterator;

public class ConnectedChip extends Chip {
  
  protected LinkedList<Chip> connectedChips;
  
  public ConnectedChip(int x, int y, int color) {
    super(x, y, color);
    this.connectedChips = new LinkedList<>();
  }
  
  public boolean equals(Chip chip) {
    return this.x == chip.x && this.y == chip.y;
  }
  
  public int connectionsNum() {
    return connectedChips.size();
  }
  
  public int neighboursNum() {
    int count = 0;
    for (Chip c : connectedChips) {
      if (Math.abs(c.x-this.x) <= 1 && Math.abs(c.y-this.y) <= 1) {
        count ++;
      }
    }
    return count;
  }
  
  //connect this chip with another chip
  public boolean addConnectedChip(Chip chip) {
    return this.connectedChips.add(chip);
  }
  
  public boolean removeConnectedChip(Chip chip) {
    ListIterator<Chip> ite = connectedChips.listIterator();
    while (ite.hasNext()) {
      Chip c = ite.next();
      if (c.equals(chip)) {
        ite.remove();
        return true;
      }
    }
    return false;
  }
  
  public void print() {
    System.out.print("(" + x + ", " + y +") connections: ");
    for (Chip c : connectedChips) {
      System.out.print(c.x + "," + c.y + ";");
    }
    System.out.println();
  }

}
