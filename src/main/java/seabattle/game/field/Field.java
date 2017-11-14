package seabattle.game.field;

import seabattle.game.ship.Ship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public final class Field {

    private final Integer fieldSize = 10;
    private List<List<CellStatus>> cells = new ArrayList<>(Collections.nCopies(fieldSize,
            new ArrayList<>(Collections.nCopies(fieldSize, CellStatus.FREE))));

    public Field() {
    }

    public Field(List<Ship> ships) {
        ships.forEach(ship -> ship.getCells().forEach(cell -> setCellStatus(cell, CellStatus.OCCUPIED)));
    }

    public Integer getFieldSize() {
        return fieldSize;
    }

    public CellStatus getCellStatus(Cell cell) {
        if (cellOutOfBounds(cell)) {
            throw new IllegalArgumentException("Given position is out of bounds!");
        }
        return cells.get(cell.getRowPos()).get(cell.getColPos());
    }

    public void setCellStatus(Cell cell, CellStatus status) {
        if (!cellOutOfBounds(cell)) {
            throw new IllegalArgumentException("Given position is out of bounds!");
        }
        cells.get(cell.getRowPos()).set(cell.getColPos(), status);
    }

    private void blockFreeSafeNoExcept(Cell cell) {
        if (!cellOutOfBounds(cell)) {
            if (getCellStatus(cell) == CellStatus.FREE) {
                setCellStatus(cell, CellStatus.BLOCKED);
            }
        }
    }

    public CellStatus fire(Cell cell) {
        switch (getCellStatus(cell)) {
            case FREE:
                setCellStatus(cell, CellStatus.BLOCKED);
                return CellStatus.BLOCKED;
            case OCCUPIED:
                setCellStatus(cell, CellStatus.ON_FIRE);
                return CellStatus.ON_FIRE;
            case ON_FIRE:
                throw new IllegalArgumentException("Given position is already checked!");
            case DESTRUCTED:
                throw new IllegalArgumentException("Given position is already checked!");
            case BLOCKED:
                throw new IllegalArgumentException("Given position is already checked!");
            default:
                throw new RuntimeException("Internal problem!");
        }
    }


    public Boolean shipKilled(Ship ship) {
        for (Cell cell : ship.getCells()) {
            if (getCellStatus(cell) != CellStatus.ON_FIRE) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public void killShip(Ship ship) {
        for (Integer rowPos = ship.getRowPos() - 1; rowPos < ship.getLastCell().getRowPos() + 1; ++rowPos) {
            for (Integer colPos = ship.getColPos() - 1; colPos < ship.getLastCell().getColPos() + 1; ++colPos) {
                if (!cellOutOfBounds(Cell.of(rowPos, colPos))) {
                    setCellStatus(Cell.of(rowPos, colPos), CellStatus.BLOCKED);
                }
            }
        }
        ship.getCells().forEach(cell -> setCellStatus(cell, CellStatus.DESTRUCTED));
    }

    public Boolean cellOutOfBounds(Cell cell) {
        return cell.getRowPos() < 0 || cell.getRowPos() >= fieldSize
                || cell.getColPos() < 0 || cell.getColPos() >= fieldSize;
    }
}
