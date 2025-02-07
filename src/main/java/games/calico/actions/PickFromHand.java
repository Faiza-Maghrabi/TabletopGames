package games.calico.actions;

import java.util.Objects;

import core.AbstractGameState;
import core.actions.AbstractAction;
import games.calico.CalicoGameState;
import games.calico.components.CalicoTile;

public class PickFromHand extends AbstractAction {
    
    public final int index;
    public final int playerId;

    public PickFromHand(int index, int playerId) {
        this.index = index;
        this.playerId = playerId;
    }

    /*
     * pick tile from player's hand
     * update gamestate's selectedTile
     */
    @Override
    public boolean execute(AbstractGameState gs) {
        CalicoGameState cgs = (CalicoGameState) gs;

        CalicoTile pickedTile = cgs.getPlayerTiles()[playerId].pick(index);
        cgs.setSelectedTile(pickedTile);

        return true;
    }

    @Override
    public PickFromHand copy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PickFromHand)) return false;
        PickFromHand that = (PickFromHand) o;
        return index == that.index && playerId == that.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, playerId);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        CalicoGameState cgs = (CalicoGameState) gameState;
        CalicoTile pickedTile = cgs.getPlayerTiles()[playerId].get(index);
        return String.format("p%d picks the tile %d (colour=%s pattern=%s) from their hand",playerId, index, pickedTile.getColour(), pickedTile.getPattern());
    }
}
