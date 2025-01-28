package games.calico.actions;

import java.util.Objects;

import core.AbstractGameState;
import core.actions.AbstractAction;
import games.calico.CalicoGameState;
import games.calico.components.CalicoBoard;
import games.calico.components.CalicoTile;

public class PlaceTile extends AbstractAction {

    public final int x;
    public final int y;
    public final CalicoTile placedTile;
    public final int playerId;

    public PlaceTile(int x, int y, CalicoTile placedTile, int playerId) {
        this.x = x;
        this.y = y;
        this.placedTile = placedTile;
        this.playerId = playerId;
    }

    /*
     * Replace the BoardTile with the Calico Tile for the given player
     */
    @Override
    public boolean execute(AbstractGameState gs) {
        CalicoGameState cgs = (CalicoGameState) gs;
        CalicoBoard calicoBoard = cgs.getPlayerBoards()[playerId];
        calicoBoard.setBoardTilePatch(x, y, placedTile);
        return true;
    }

    @Override
    public PlaceTile copy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlaceTile)) return false;
        PlaceTile that = (PlaceTile) o;
        return x == that.x && y == that.y && placedTile == that.placedTile && playerId == that.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, placedTile, playerId);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return String.format("p%d places a tile (colour=%s pattern=%s) at board tile (x=%d y=%d)",playerId, placedTile.getColour(), placedTile.getPattern(), x, y);
    }
    
}
