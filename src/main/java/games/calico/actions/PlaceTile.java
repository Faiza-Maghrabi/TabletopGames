package games.calico.actions;

import java.util.Objects;

import core.AbstractGameState;
import core.actions.AbstractAction;
import games.calico.CalicoGameState;
import games.calico.CalicoTypes.TileColour;
import games.calico.CalicoTypes.TilePattern;
import games.calico.components.CalicoBoard;
import games.calico.components.CalicoCatCard;
import games.calico.components.CalicoTile;

public class PlaceTile extends AbstractAction {

    public final int x;
    public final int y;
    public final TileColour colour;
    public final TilePattern pattern;
    public final int playerId;

    public PlaceTile(int x, int y, TileColour colour, TilePattern pattern, int playerId) {
        this.x = x;
        this.y = y;
        this.colour = colour;
        this.pattern = pattern;
        this.playerId = playerId;
    }

    /*
     * Replace the BoardTile with the Calico Tile for the given player
     */
    @Override
    public boolean execute(AbstractGameState gs) {
        CalicoGameState cgs = (CalicoGameState) gs;
        CalicoBoard calicoBoard = cgs.getPlayerBoards()[playerId];
        CalicoTile newTile = new CalicoTile(colour, pattern);
        calicoBoard.setBoardTilePatch(x, y, newTile);
        boolean buttonPlaced = calicoBoard.lookForButton(x, y);
        if (buttonPlaced){
            //add point to player
            cgs.addButtonPoint(playerId, colour);
        }
        CalicoCatCard catPlaced = calicoBoard.lookForCat(x, y, cgs.getActiveCats());
        System.out.println("result: " + catPlaced);
        if (catPlaced != null){
            System.out.println(catPlaced.getName());
            cgs.addCatPoint(playerId, catPlaced.getCat());
        }
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
        return x == that.x && y == that.y && colour == that.colour && pattern == that.pattern && playerId == that.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, colour, pattern, playerId);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return String.format("p%d places a tile (colour=%s pattern=%s) at board tile (x=%d y=%d)",playerId, colour, pattern, x, y);
    }
    
}
