package games.calico;

import core.AbstractGameState;
import core.AbstractParameters;
import core.components.*;
import games.GameType;
import games.calico.CalicoTypes.Button;
import games.calico.CalicoTypes.Cat;
import games.calico.CalicoTypes.DesignGoalTile;
import games.calico.components.CalicoBoard;
import games.calico.components.CalicoCatCard;
import games.calico.components.CalicoTile;

import java.util.*;

// import static games.calico.CalicoGameState.TMPhase.CorporationSelect;

//changed from AbstractGameStateWithTurnOrder due to deprecation
public class CalicoGameState extends AbstractGameState {

    // General state info
    int turn;
    long seed;

    //Active Cats - array for easier access
    CalicoCatCard[] activeCats;
    //Tile Locations
    Deck<CalicoTile> tileBag, tileMarket;
    //selected tile to place on board in a turn
    CalicoTile selectedTile;

    // Effects and actions played - to be updated when actions are added
    // HashSet<TMAction>[] playerExtraActions;

    // Player-specific values
    CalicoBoard[] playerBoards;  //are design token points going to be stored here?
    HashMap<Cat, Counter>[] playerCatScore; //number of that cat tokens
    HashMap<Button, Counter>[] playerButtonScore; //number of those buttons
    HashMap<DesignGoalTile, Counter>[] playerGoalScore; //score acheived by each design goal tile: 1 - one goal / 2 - two goals
    Counter[] playerFinalPoints;  // Points calculated at the end of the game
    // Player tiles on hand
    Deck<CalicoTile>[] playerTiles;


    //TODO: Add in button colour hashmap
    /**
     * Constructor. Initialises some generic game state variables.
     *
     * @param gameParameters - game parameters.
     */
    public CalicoGameState(AbstractParameters gameParameters, int nPlayers) {
        super(gameParameters, nPlayers);
    }

    // @Override
    // protected TurnOrder _createTurnOrder(int nPlayers) {
    //     return new TMTurnOrder(nPlayers, ((CalicoGameParameters) gameParameters).nActionsPerPlayer);
    // }

    @Override
    protected GameType _getGameType() {
        return GameType.Calico;
    }

    /* Return all components in game incl nested
     * Decks and Areas have all of their nested components automatically added
     * called after game setup
     */ 
    @Override
    protected List<Component> _getAllComponents() {
        return new ArrayList<Component>() {{
            add(tileBag);
            add(tileMarket);
            add(selectedTile);
            for (int i = 0; i< activeCats.length; i++){
                addAll(Arrays.asList(activeCats[i]));
            }
            for (int i = 0; i < getNPlayers(); i++) {
                addAll(Arrays.asList(playerTiles[i]));
                addAll(Arrays.asList(playerBoards[i]));
                add(playerFinalPoints[i]);
                addAll(playerCatScore[i].values());
                addAll(playerButtonScore[i].values());
                addAll(playerGoalScore[i].values());
            }
        }};
    }

    /*
     * player-specific version of game, only stuff the player can see
     * https://tabletopgames.ai/wiki/conventions/info_hiding
     * All of the components in the observation should be copies of those in the game state
     */
    @Override
    protected AbstractGameState _copy(int playerId) {
        CalicoGameState copy = new CalicoGameState(gameParameters.copy(), getNPlayers());

        // General public info
        copy.turn = turn;
        copy.seed = seed;
        copy.activeCats = new CalicoCatCard[3];
        for (int i = 0; i< 3; i++){
            copy.activeCats[i] = activeCats[i].copy();
        }

        copy.tileBag = tileBag.copy();
        copy.tileMarket = tileMarket.copy();
        copy.selectedTile = selectedTile.copy();
        copy.playerBoards = new CalicoBoard[getNPlayers()];
        copy.playerCatScore = new HashMap[getNPlayers()];
        copy.playerButtonScore = new HashMap[getNPlayers()];
        copy.playerGoalScore = new HashMap[getNPlayers()];
        copy.playerFinalPoints = new Counter[getNPlayers()];
        copy.playerTiles = new Deck[getNPlayers()];

        for (int i = 0; i < getNPlayers(); i++) {
            copy.playerBoards[i] = playerBoards[i].copy();
            copy.playerFinalPoints[i] = playerFinalPoints[i].copy();
            copy.playerTiles[i] = playerTiles[i].copy();

            copy.playerCatScore[i] = new HashMap<Cat, Counter>();
            for (Cat c : playerCatScore[i].keySet()) {
                copy.playerCatScore[i].put(c, playerCatScore[i].get(c).copy());
            }

            copy.playerButtonScore[i] = new HashMap<Button, Counter>();
            for (Button b : playerButtonScore[i].keySet()) {
                copy.playerButtonScore[i].put(b, playerButtonScore[i].get(b).copy());
            }

            copy.playerGoalScore[i] = new HashMap<DesignGoalTile, Counter>();
            for (DesignGoalTile g : playerGoalScore[i].keySet()) {
                copy.playerGoalScore[i].put(g, playerGoalScore[i].get(g).copy());
            }
        }
        return copy;
    }

    /*
     * return estimate of how well a player is doing in range [-1, +1]
     * How can I do this for calico?
     * Not part of implementation - is for rule based player
     * //TODO need to update the countPoints to be within range - what if a move is good but doesnt impact score?
     */
    @Override
    protected double _getHeuristicScore(int playerId) {
//        return new TMHeuristic().evaluateState(this, playerId);
//        return getGameScore(playerId);
        return countPoints(playerId);
    }

    /*
     * return player's scare for current game state
     */
    @Override
    public double getGameScore(int playerId) {
       return countPoints(playerId);
    }

    /*
     * includes all the data in the GameState.
     */
    @Override
    public boolean _equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalicoGameState)) return false;
        CalicoGameState that = (CalicoGameState) o;
        return turn == that.turn
                && seed == that.seed
                && Objects.equals(tileBag, that.tileBag)
                && Objects.equals(tileMarket, that.tileMarket)
                && Objects.equals(selectedTile, that.selectedTile)
                && Arrays.equals(playerTiles, that.playerTiles)
                && Arrays.equals(activeCats, that.activeCats)
                && Arrays.equals(playerBoards, that.playerBoards)
                && Arrays.equals(playerFinalPoints, that.playerFinalPoints)
                && Arrays.equals(playerCatScore, that.playerCatScore)
                && Arrays.equals(playerButtonScore, that.playerButtonScore)
                && Arrays.equals(playerGoalScore, that.playerGoalScore);
    }

    /*
     * return hash code for game state
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), turn, seed, tileBag, tileMarket, selectedTile);
        result = 31 * result + Arrays.hashCode(activeCats);
        result = 31 * result + Arrays.hashCode(playerBoards);
        result = 31 * result + Arrays.hashCode(playerCatScore);
        result = 31 * result + Arrays.hashCode(playerButtonScore);
        result = 31 * result + Arrays.hashCode(playerGoalScore);
        result = 31 * result + Arrays.hashCode(playerFinalPoints);
        result = 31 * result + Arrays.hashCode(playerTiles);
        return result;
    }

    /*
     * TODO: check over this
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int result = Objects.hash(gameParameters);
        sb.append(result).append("|");
        result = Objects.hash(getAllComponents());
        sb.append(result).append("|");
        result = Objects.hash(gameStatus);
        sb.append(result).append("|");
        result = Objects.hash(gamePhase);
        sb.append(result).append("|");
        result = Arrays.hashCode(playerResults);
        sb.append(result).append("|*|");
        result = Objects.hash(turn);
        sb.append(result).append("|");
        result = Objects.hash(seed);
        sb.append(result).append("|2|");
        result = Objects.hash(tileBag);
        sb.append(result).append("|3|");
        result = Objects.hash(tileMarket);
        sb.append(result).append("|4|");
        result = Objects.hash(selectedTile);
        sb.append(result).append("|5|");
        result = 31 * result + Arrays.hashCode(playerBoards);
        sb.append(result).append("|6|");
        result = 31 * result + Arrays.hashCode(playerCatScore);
        sb.append(result).append("|7|");
        result = 31 * result + Arrays.hashCode(playerButtonScore);
        sb.append(result).append("|8|");
        result = 31 * result + Arrays.hashCode(playerGoalScore);
        sb.append(result).append("|9|");
        result = Arrays.hashCode(playerFinalPoints);
        sb.append(result).append("|10|");
        result = Arrays.hashCode(playerTiles);
        sb.append(result);
        return sb.toString();
    }

    /*
     * Public API
     */

    public int getTurn() {
        return turn;
    }

    public void updateTurn() {
        turn+=1;
    }

    public long getSeed() {
        return seed;
    }

    public CalicoCatCard[] getActiveCats() {
        return activeCats;
    }

    public Deck<CalicoTile> getTileBag() {
        return tileBag;
    }

    public Deck<CalicoTile> getTileMarket() {
        return tileBag;
    }

    public CalicoTile getSelectedTile(){
        return selectedTile;
    }

    public void setSelectedTile(CalicoTile pickedTile){
        selectedTile = pickedTile;
    }

    public CalicoBoard[] getPlayerBoards() {
        return playerBoards;
    }

    public HashMap<Cat, Counter>[] getPlayerCatScore() {
        return playerCatScore;
    }

    public HashMap<Button, Counter>[] gePlayerButtonScore() {
        return playerButtonScore;
    }

    public HashMap<DesignGoalTile, Counter>[] getPlayerGoalScore() {
        return playerGoalScore;
    }

    public Counter[] getPlayerFinalPoints() {
        return playerFinalPoints;
    }

    public Deck<CalicoTile>[] getPlayerTiles() {
        return playerTiles;
    }

    /*
    * count up all points for a player and returns the sum 
    * includes butons, cats, and design tiles
    */
    public int countPoints(int player) {
        int points = 0;
        // Add button points
        points += countButtons(player);
        // Add cat points
        points += countCats(player);
        // Add design points
        points += countDesign(player);
        return points;
    }

    private int countButtons(int player) {
        int buttonPoints = 0;
        for (Button b : Button.values()){
            buttonPoints += playerButtonScore[player].get(b).getValueIdx();
        }
        return buttonPoints;
    }

    private int countCats(int player) {
        int catPoints = 0;
        for (int j = 0; j < activeCats.length; j++) {
            Cat c = activeCats[j].getCat();
            catPoints += playerCatScore[player].get(c).getValueIdx();
        }
        return catPoints;
    }

    private int countDesign(int player) {
        int designPoints = 0;
        for (DesignGoalTile g : DesignGoalTile.values()) {
            designPoints += playerGoalScore[player].get(g).getValueIdx();
        }
        return designPoints;
    }

}
