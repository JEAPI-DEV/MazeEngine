package net.simplehardware.engine.viewer;

import net.simplehardware.engine.cells.Cell;
import net.simplehardware.engine.cells.FloorCell;
import net.simplehardware.engine.cells.FinishCell;
import net.simplehardware.engine.cells.WallCell;

import java.io.Serial;
import java.io.Serializable;

/**
 * Snapshot of a cell's state
 */
public class CellSnapshot implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final CellType type;
    private final int x;
    private final int y;
    private final Character form;
    private final Integer formOwner;
    private final boolean hasSheet;
    private final Integer finishPlayerId;

    public enum CellType {
        WALL, FLOOR, FINISH
    }

    private CellSnapshot(CellType type, int x, int y, Character form, Integer formOwner,
            boolean hasSheet, Integer finishPlayerId) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.form = form;
        this.formOwner = formOwner;
        this.hasSheet = hasSheet;
        this.finishPlayerId = finishPlayerId;
    }

    public static CellSnapshot fromCell(Cell cell) {
        if (cell instanceof WallCell) {
            return new CellSnapshot(CellType.WALL, cell.getX(), cell.getY(),
                    null, null, false, null);
        } else if (cell instanceof FinishCell finish) {
            return new CellSnapshot(CellType.FINISH, cell.getX(), cell.getY(),
                    null, null, false, finish.getPlayerId());
        } else if (cell instanceof FloorCell floor) {
            return new CellSnapshot(CellType.FLOOR, cell.getX(), cell.getY(),
                    floor.getForm(), floor.getFormOwner(),
                    floor.hasSheet(), null);
        }
        // Default to floor
        return new CellSnapshot(CellType.FLOOR, cell.getX(), cell.getY(),
                null, null, false, null);
    }

    public CellType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Character getForm() {
        return form;
    }

    public String fetchFormID(){
        return form.toString() + formOwner.toString();
    }

    public Integer getFormOwner() {
        return formOwner;
    }

    public boolean hasSheet() {
        return hasSheet;
    }

    public Integer getFinishPlayerId() {
        return finishPlayerId;
    }
}
