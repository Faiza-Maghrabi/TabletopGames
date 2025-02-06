package games.calico;

import java.util.ArrayList;
import java.util.List;

import core.actions.AbstractAction;
import games.calico.actions.PickFromHand;
import games.calico.actions.PickFromMarket;
import games.calico.actions.PlaceTile;
import games.calico.components.CalicoBoard;
import games.calico.components.CalicoBoardTile;

public class CalicoActionFactory {
    /*
    * get all actions for tiles that can have a patch tile added to
    */
    public static List<AbstractAction> getTilePlaceActions(CalicoGameState gs, int player) {
        ArrayList<AbstractAction> actions = new ArrayList<>();
        CalicoGameParameters params = (CalicoGameParameters) gs.getGameParameters();
        // get the correct board for the player
        CalicoBoard calicoBoard = gs.getPlayerBoards()[player];

        //start at 1 and end at 5 as there is a border of tiles pre-placed on grid
        for (int x = 1; x < params.boardSize-1; x++) {
            for (int y = 1; y < params.boardSize-1; y++) {
                CalicoBoardTile tile = calicoBoard.getElement(x, y);

                //check if tile is empty
                if (tile.isEmpty()){
                    //add in new tile action
                    actions.add(new PlaceTile(x, y, gs.getSelectedTile(), player));
                }
                //else move onto next board tile
            }
        }

        return actions;
    }

    /*
    * get all actions for selcting a tile from the tile market
    */
    public static List<AbstractAction> getTileMarketActions(CalicoGameState gs, int player) {
        ArrayList<AbstractAction> actions = new ArrayList<>();

        //get an action for each tile in the market
        for (int i = 0; i < 3; i++) {
            actions.add(new PickFromMarket(i, player));
        }

        return actions;
    }

    /*
    * get all actions for selcting a tile from the player's hand
    */
    public static List<AbstractAction> getPlayerHandActions(CalicoGameState gs, int player) {
        ArrayList<AbstractAction> actions = new ArrayList<>();

        //get an action for each tile in the market (2)
        for (int i = 0; i < 3; i++) {
            actions.add(new PickFromHand(i, player));
        }

        return actions;
    }
}
