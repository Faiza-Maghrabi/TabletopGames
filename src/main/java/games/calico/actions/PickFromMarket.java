package games.calico.actions;

import java.util.Objects;

import core.AbstractGameState;
import core.actions.AbstractAction;
import games.calico.CalicoGameState;
import games.calico.components.CalicoTile;

public class PickFromMarket extends AbstractAction {
    
    public final int index;
    public final int playerId;

    public PickFromMarket(int index, int playerId) {
        this.index = index;
        this.playerId = playerId;
    }

    /*
     * pick tile from deck
     * add picked tile to player's hand
     * replace market spot with new tile
     */
    @Override
    public boolean execute(AbstractGameState gs) {
        CalicoGameState cgs = (CalicoGameState) gs;

        CalicoTile pickedTile = cgs.getTileMarket().pick(index);
        cgs.getPlayerTiles()[playerId].add(pickedTile);

        CalicoTile tileFromBag = cgs.getTileBag().draw();
        cgs.getTileMarket().add(tileFromBag);

        //if this is the last player then update the turn count
        int noPlayers = gs.getNPlayers() - 1;
        if (playerId == noPlayers) {
            cgs.updateTurn();
        }

        return true;
    }

    @Override
    public PickFromMarket copy() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PickFromMarket)) return false;
        PickFromMarket that = (PickFromMarket) o;
        return index == that.index && playerId == that.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, playerId);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        CalicoGameState cgs = (CalicoGameState) gameState;
        CalicoTile pickedTile = cgs.getTileMarket().get(index);

        return String.format("p%d picks the tile %d (colour=%s pattern=%s) from the tile market",playerId, index, pickedTile.getColour(), pickedTile.getPattern());
    }
}
