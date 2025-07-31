package view.utils;

// CellClickEvent.java
public class CellClickEvent {
    private final int x;
    private final int y;

    public CellClickEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // Returns the value x and y of the Cell respectively
    public int getX() { return x; }
    public int getY() { return y; }
}
