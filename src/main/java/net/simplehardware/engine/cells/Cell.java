package net.simplehardware.engine.cells;

import net.simplehardware.engine.players.Player;

/**
 * Base class for all maze cells
 */
public abstract class Cell {
    protected final int x;
    protected final int y;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Check if this cell is walkable
     */
    public abstract boolean isWalkable();

    /**
     * Get the cell type name for protocol output
     */
    public abstract String getCellType();

    /**
     * Get detailed cell information for protocol output
     */
    public String getCellDetails() {
        return "";
    }

    /**
     * Called when a player enters this cell
     */
    public void onPlayerEnter(Player player) {
        // Override in subclasses if needed
    }

    /**
     * Called when a player leaves this cell
     */
    public void onPlayerLeave(Player player) {
        // Override in subclasses if needed
    }
}
