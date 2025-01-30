package games.calico.actions;

import java.util.List;
import java.util.Objects;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IExtendedSequence;
import games.calico.CalicoActionFactory;
import games.calico.CalicoGameState;

/*
 * IExtendedSequence Action to run every turn - players pick a tile from their deck
 * place that tile onto a place on the board,
 * then pick a tile from the market,
 * and then place the tile into their deck
 * the market is then updated with a new tile
 */
public class TurnActions extends AbstractAction implements IExtendedSequence {

    public final int playerId;

    public TurnActions(int playerId) {
        this.playerId = playerId;
    }


    //TODO: is the false here needed?
    public boolean pickedTilefromHand = false;
    public boolean putTileOnBoard = false;
    public boolean pickedTilefromMarket = false;

    @Override
    public boolean execute(AbstractGameState gs) {
        if (gs.getCurrentPlayer() != playerId) {
            throw new AssertionError("Attempting to play an action out of turn : " + this);
        }
        return gs.setActionInProgress(this);
    }

    @Override
    public List<AbstractAction> _computeAvailableActions(AbstractGameState gs) {
        CalicoGameState state = (CalicoGameState) gs;
        if (!putTileOnBoard) {
            if (!pickedTilefromHand) {
                //choose tile from player hand
                return CalicoActionFactory.getPlayerHandActions(state, playerId);
                //^ tile cannot be re-chosen after this
            }
            else {
                //place tile on board
                return CalicoActionFactory.getTilePlaceActions(state, playerId);
            }
        } else {
            //get tile from market
            return CalicoActionFactory.getTileMarketActions(state, playerId);
        }
    }

    @Override
    public int getCurrentPlayer(AbstractGameState state) {
        return playerId;
    }

    @Override
    public void _afterAction(AbstractGameState state, AbstractAction action) {
        if (action instanceof PickFromHand && ((PickFromHand) action).playerId == playerId)
            pickedTilefromHand = true;
        else if (action instanceof PlaceTile && ((PlaceTile) action).playerId == playerId)
            putTileOnBoard = true;
        else if (action instanceof PickFromMarket && ((PickFromMarket) action).playerId == playerId)
            pickedTilefromMarket = true;
    }

    @Override
    public boolean executionComplete(AbstractGameState state) {
        return pickedTilefromHand && putTileOnBoard && pickedTilefromMarket;
    }

    @Override
    public TurnActions copy() {
        TurnActions retValue = new TurnActions(playerId);
        retValue.pickedTilefromHand = pickedTilefromHand;
        retValue.putTileOnBoard = putTileOnBoard;
        retValue.pickedTilefromMarket = pickedTilefromMarket;
        return retValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TurnActions) {
            TurnActions other = (TurnActions) obj;
            return other.playerId == playerId && other.pickedTilefromHand == pickedTilefromHand && other.putTileOnBoard == putTileOnBoard && other.pickedTilefromMarket == pickedTilefromMarket; 
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, pickedTilefromHand, putTileOnBoard, pickedTilefromMarket);
    }

    @Override
    public String getString(AbstractGameState gameState) {
        return String.format("p%d actions (pickHand=%b, placeBoard=%b, pickMarket=%b)",playerId, pickedTilefromHand, putTileOnBoard, pickedTilefromMarket);
    }
}

