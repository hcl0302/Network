package player;

import java.util.LinkedList;
import java.util.ListIterator;

public class ConnectedChip extends Chip {
  
  private LinkedList<Chip> connectedChips;
  
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
    ListIterator<Chip> ite = connectedChips.listIterator();
    int count = 0;
    while (ite.hasNext()) {
      Chip c = ite.next();
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
    return this.connectedChips.remove(chip);
  }

}
