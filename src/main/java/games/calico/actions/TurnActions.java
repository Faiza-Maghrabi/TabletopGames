package games.calico.actions;

import java.util.List;
import java.util.Objects;

import core.AbstractGameState;
import core.actions.AbstractAction;
import core.interfaces.IExtendedSequence;
import games.calico.CalicoActionFactory;
import games.calico.CalicoGameState;

//TODO: REMOVE
import games.dominion.DominionConstants.DeckType;
import games.dominion.DominionGameState;
import games.dominion.actions.Artisan;
import games.dominion.actions.GainCard;
import games.dominion.actions.MoveCard;

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


    public boolean pickedTilefromHand;
    public boolean putTileOnBoard;
    public boolean pickedTilefromMarket;

    @Override
    public boolean execute(AbstractGameState gs) {
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
        if (action instanceof GainCard && ((GainCard) action).buyingPlayer == player)
            gainedCard = true;
        if (action instanceof MoveCard && ((MoveCard) action).playerFrom == player)
            putCardOnDeck = true;
    }

    @Override
    public boolean executionComplete(AbstractGameState state) {
        return gainedCard && putCardOnDeck;
    }

    @Override
    public Artisan copy() {
        Artisan retValue = new Artisan(player, dummyAction);
        retValue.putCardOnDeck = putCardOnDeck;
        retValue.gainedCard = gainedCard;
        return retValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Artisan) {
            Artisan other = (Artisan) obj;
            return other.gainedCard == gainedCard && other.putCardOnDeck == putCardOnDeck && super.equals(obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gainedCard, putCardOnDeck) + 31 * super.hashCode();
    }

    @Override
    public String getString(AbstractGameState gameState) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getString'");
    }
}

