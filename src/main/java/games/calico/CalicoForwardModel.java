package games.calico;

import core.AbstractGameState;
import core.CoreConstants;
import core.StandardForwardModel;
import core.CoreConstants.VisibilityMode;
import core.actions.AbstractAction;
import core.components.Counter;
import core.components.Deck;

import games.calico.CalicoTypes.BoardTypes;
import games.calico.CalicoTypes.Button;
import games.calico.CalicoTypes.Cat;
import games.calico.CalicoTypes.TileColour;
import games.calico.CalicoTypes.TilePattern;
import games.calico.actions.PickFromMarket;
import games.calico.actions.TurnActions;
import games.calico.components.CalicoBoard;
import games.calico.components.CalicoTile;

import java.util.*;

//changed from StandardForwardModelWithTurnOrder due to deprecated?
public class CalicoForwardModel extends StandardForwardModel {

    //set up begining game state
    @Override
    protected void _setup(AbstractGameState firstState) {
        CalicoGameState gs = (CalicoGameState) firstState;
        CalicoGameParameters params = (CalicoGameParameters) firstState.getGameParameters();

        gs.turn = 1;
        gs.seed = params.getRandomSeed();

        gs.activeCats = params.loadCats();

        gs.tileBag = params.loadTiles();
        gs.tileBag.shuffle(new Random(gs.seed));

        gs.tileMarket = new Deck<CalicoTile>("tile market", VisibilityMode.VISIBLE_TO_ALL);
        //add 3 to the tile market
        gs.tileMarket.add(gs.tileBag.draw());
        gs.tileMarket.add(gs.tileBag.draw());
        gs.tileMarket.add(gs.tileBag.draw());

        gs.tileBag.shuffle(new Random(params.getRandomSeed()));

        gs.selectedTile = new CalicoTile(TileColour.Null, TilePattern.Null);
        
        gs.playerBoards = new CalicoBoard[gs.getNPlayers()];

        gs.playerCatScore = new HashMap[gs.getNPlayers()];
        gs.playerButtonScore = new HashMap[gs.getNPlayers()];
        gs.playerFinalPoints = new Counter[gs.getNPlayers()];

        gs.playerTiles = new Deck[gs.getNPlayers()];

        ArrayList<BoardTypes> allBoardTypes = params.getShuffledBoardTypes();

        for (int i = 0; i< gs.getNPlayers(); i++){

            gs.playerBoards[i] = new CalicoBoard(params.boardSize, allBoardTypes.get(i));
            gs.playerBoards[i] = params.setupBoard(gs.playerBoards[i], allBoardTypes.get(i));

            //fill in point hashmaps
            gs.playerCatScore[i] = new HashMap<Cat, Counter>();
            for (int j = 0; j < gs.activeCats.length; j++) {
                Cat c = gs.activeCats[j].getCat();
                gs.playerCatScore[i].put(c, new Counter(100,"player" + i +"'s cat points for " + c.getName()));
            }

            gs.playerButtonScore[i] = new HashMap<Button, Counter>();
            for (Button b : Button.values()) {
                gs.playerButtonScore[i].put(b, new Counter(100,"player" + i +"'s button points for " + b));
            }

            gs.playerTiles[i] = new Deck<CalicoTile>("player" + i +"'s tiles on hand", i, CoreConstants.VisibilityMode.VISIBLE_TO_OWNER);
            gs.playerTiles[i].add(gs.tileBag.draw());
            gs.playerTiles[i].add(gs.tileBag.draw());
            gs.playerTiles[i].add(gs.tileBag.draw());

            gs.playerFinalPoints[i] = new Counter(100, "point counter for player " + i);

        }
    }

    //This is called every time an action is taken by one of the players, human or AI.
    //called after action is applied to state
    //use _beforeAction if needed to add logic before this
    @Override
    protected void _afterAction(AbstractGameState currentState, AbstractAction action) {
        if (currentState.isActionInProgress()) return;
        CalicoGameState gs = (CalicoGameState) currentState;

        // Check game end after each turn
        //no actions at turn 23 -> game should end
        if (action instanceof PickFromMarket) {
            if (gs.getTurn() == 23) {
                //count up designToken points for all boards and then end
                endGame(gs);
                return;
            }
            System.out.println("end player turn");
            //testing
            gs.evaluateBoard(((PickFromMarket)action).playerId);
            super.endPlayerTurn(gs);
        }
    }

    //return a list with all actions available for the current player, in the context of the game state object
    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        ArrayList<AbstractAction> actions = new ArrayList<>();
        CalicoGameState gs = (CalicoGameState) gameState;

        // If there is an action in progress (see IExtendedSequence), then delegate to that
        // Regular game loop calls will not reach here, but external calls (e.g. GUI, agents) will and need correct info
        if (gs.isActionInProgress()) {
            return gs.getActionsInProgress().peek()._computeAvailableActions(gs);
        }

        int player = gs.getCurrentPlayer();

        //use TurnActions to return available actions for the player
        actions.add(new TurnActions(player));

        return actions;
    }
}
