package games.calico;

import core.AbstractGameState;
import core.AbstractParameters;
import core.components.*;
import games.GameType;
import games.calico.CalicoTypes.Button;
import games.calico.CalicoTypes.Cat;
import games.calico.CalicoTypes.TileColour;
import games.calico.CalicoTypes.TilePattern;
import games.calico.components.CalicoBoard;
import games.calico.components.CalicoBoardTile;
import games.calico.components.CalicoCatCard;
import games.calico.components.CalicoTile;
import games.calico.types.CalicoLookForCatReturn;

import java.util.*;


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

    // Player-specific values
    CalicoBoard[] playerBoards;  //are design token points going to be stored here?
    HashMap<Cat, Counter>[] playerCatScore; //number of that cat tokens
    HashMap<Button, Counter>[] playerButtonScore; //number of those buttons
    Counter[] playerFinalPoints;  // Points calculated at the end of the game
    // Player tiles on hand
    Deck<CalicoTile>[] playerTiles;

    /**
     * Constructor. Initialises some generic game state variables.
     *
     * @param gameParameters - game parameters.
     */
    public CalicoGameState(AbstractParameters gameParameters, int nPlayers) {
        super(gameParameters, nPlayers);
    }

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
            }
        }};
    }

    /*
     * player-specific version of game, only stuff the player can see
     * https://tabletopgames.ai/wiki/conventions/info_hiding
     * All of the components in the observation should be copies of those in the game state
     */
    @SuppressWarnings("unchecked")
    @Override
    protected AbstractGameState _copy(int playerId) {
        CalicoGameState copy = new CalicoGameState(gameParameters.copy(), getNPlayers());
        CalicoGameParameters params = (CalicoGameParameters) copy.getGameParameters();

        // General public info
        copy.turn = turn;
        copy.seed = seed;
        copy.activeCats = new CalicoCatCard[3];
        for (int i = 0; i< 3; i++){
            copy.activeCats[i] = activeCats[i].copy();
        }

        copy.tileBag = tileBag.copy();
        //shuffle hidden information
        copy.tileBag.shuffle(new Random(params.getRandomSeed()));
        copy.tileMarket = tileMarket.copy();
        copy.selectedTile = selectedTile.copy();
        copy.playerBoards = new CalicoBoard[getNPlayers()];
        copy.playerCatScore = new HashMap[getNPlayers()];
        copy.playerButtonScore = new HashMap[getNPlayers()];
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

        }
        return copy;
    }

    /*
     * return estimate of how well a player is doing in range [-1, +1]
     */
    @Override
    protected double _getHeuristicScore(int playerId) {
        return evaluateBoard(playerId);
    }


    //Evaluate how good the board is for the player
    public double evaluateBoard(int playerId) {
        CalicoBoard board = playerBoards[playerId];
        CalicoGameParameters params = (CalicoGameParameters) gameParameters;
        double totalScore = 0;
        double maxPossibleScore = 0;  // Used for normalization 

        //calcuate how useful a Tile is on the board for each type of Tile
        for (int x = 1; x < params.boardSize - 1; x++) {
            for (int y = 1; y < params.boardSize -1; y++) {
                CalicoBoardTile tile = board.getElement(x, y);
                if (!tile.isEmpty()){
                    float tileScore = 0;
                    if (!tile.isDesignTile()) {

                        //buttons
                        if (tile.hasButton()){
                            tileScore+=params.fullButtonModifier;
                        }
                        else {
                            if (board.lookForButton(x, y) > 1){
                                tileScore+=params.partButtonModifier;
                            }
                        }
                        maxPossibleScore+=params.fullButtonModifier;
                        //cats
                        if (tile.hasCat()) {
                            Cat cat = findCatForPattern(tile.getTilePattern());
                            double addScore = (cat.getPoints()/cat.getSize()) * (double) Math.pow(cat.getSize(), params.catModifier);
                            tileScore+= addScore;
                            maxPossibleScore+=addScore;
                        }
                        else {
                            CalicoLookForCatReturn lookReturn = board.lookForCat(x, y, activeCats);
                            if (lookReturn.getsizeFound() > 1){
                                Cat cat = lookReturn.getCatCard().getCat();
                                double addScore = (cat.getPoints() / cat.getSize()) * (double) Math.pow(lookReturn.getsizeFound(), params.catModifier);
                                tileScore +=addScore;
                                maxPossibleScore+=addScore;
                            }
                            else if (lookReturn.getsizeFound() == 1){
                                Cat cat = lookReturn.getCatCard().getCat();
                                double addScore = cat.getPoints()/cat.getSize();
                                maxPossibleScore+=addScore;
                            }
                            //if there is a cat for the pattern and there is no valid arrangement on the board, discourage from placing tile there
                            if (lookReturn.getCatCard() != null && !lookReturn.getvalidPlacement()){
                                Cat cat = lookReturn.getCatCard().getCat();
                                double removeScore = cat.getPoints()/(cat.getSize() / 2);
                                tileScore -=removeScore;
                            }
                        }
                    }   //use calculateDesignTokenPoints for evaluating these
                    else {
                        int maxDesignScore = board.getElement(x, y).getDesignGoal().getGoalTwo();
                        double designScore = board.calculateDesignTokenPoints(x, y, params.designTokenMultiplier);
                        tileScore += designScore;
                        maxPossibleScore += maxDesignScore * (2.0 - params.designTokenMultiplier);
                    }
                    totalScore +=tileScore;
                }
            }
        }

        //add in points for rainbow buttons - increase to total is same as maxPossibleScore
        int rainbowNum = playerButtonScore[playerId].get(Button.Rainbow).getValueIdx();
        totalScore += (rainbowNum * 3);
        maxPossibleScore += (rainbowNum * 3);


        // System.out.println("FINAL totalScore is: " + totalScore);
        // System.out.println("FINAL maxPossibleScore is: " + maxPossibleScore);
        //System.out.println((totalScore / maxPossibleScore) * 2.0 - 1.0);
        return (totalScore / maxPossibleScore) * 2.0 - 1.0;
    }

    /*
     * return player's scare for current game state
     */
    @Override
    public double getGameScore(int playerId) {
        //System.out.println("getting player score");
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
                && Arrays.equals(playerButtonScore, that.playerButtonScore);
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
        result = 31 * result + Arrays.hashCode(playerFinalPoints);
        result = 31 * result + Arrays.hashCode(playerTiles);
        return result;
    }

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
        result = Arrays.hashCode(playerFinalPoints);
        sb.append(result).append("|9|");
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
        return tileMarket;
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

    public HashMap<Button, Counter>[] getPlayerButtonScore() {
        return playerButtonScore;
    }

    public Counter[] getPlayerFinalPoints() {
        return playerFinalPoints;
    }

    public Deck<CalicoTile>[] getPlayerTiles() {
        return playerTiles;
    }

    //returns the cat associated with a pattern if found
    public Cat findCatForPattern(TilePattern pattern) {
        for (CalicoCatCard catCard : activeCats){
            TilePattern[] catPatterns = catCard.getPatches();
            if (pattern == catPatterns[0] || pattern == catPatterns[1]) return catCard.getCat();
        }
        return null;
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

    public int countButtons(int player) {
        int buttonPoints = 0;
        for (Button b : Button.values()){
            buttonPoints += playerButtonScore[player].get(b).getValueIdx() * 3;
        }
        return buttonPoints;
    }

    public int countCats(int player) {
        int catPoints = 0;
        for (int j = 0; j < activeCats.length; j++) {
            Cat c = activeCats[j].getCat();
            catPoints += playerCatScore[player].get(c).getValueIdx() * c.getPoints();
        }
        return catPoints;
    }

    //calculate design points in a player's board
    public int countDesign(int player) {
        return playerBoards[player].getDesignPoints(CalicoTypes.designLoc);
    }

    //add button to player and add in additional rainbow if applicable
    public void addButtonPoint(int player, TileColour colour){
        HashMap<Button, Counter> playerButtons = playerButtonScore[player];
        playerButtons.get(colour.button).increment();
        //check if a rainbow can be added
        int rainbowNum = playerButtons.get(Button.Rainbow).getValueIdx();
        for (Button b : Button.values()){
            if (b != Button.Rainbow && playerButtons.get(b).getValueIdx() <= rainbowNum){
                return;
            }
        }
        //System.out.println("ADDING RAINBOW");
        playerButtons.get(Button.Rainbow).increment();
    }

    public void addCatPoint(int player, Cat cat){
        //System.out.println("ADDING CAT POINT " + cat.getName());
        playerCatScore[player].get(cat).increment();
    }

    //get the design String to be output in the GUI
    public String[] getPlayerDesignGoalReached(int player, int locNum) {
        return playerBoards[player].getBoardDesignGoalReached(CalicoTypes.designLoc[locNum]);
    }

}
