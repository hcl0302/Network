package player;

public class Chip {
  public int color;
  public int x;
  public int y;
  
  public Chip(int x, int y, int color) {
    this.x = x;
    this.y = y;
    this.color = color;
  }
  
  public boolean equals(Chip chip) {
    return this.x == chip.x && this.y == chip.y;
  }
  
  public String toString() {
    return "("+x+","+y+")";
  }
}

