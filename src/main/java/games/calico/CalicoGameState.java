package games.calico;

import core.AbstractGameState;
import core.AbstractGameStateWithTurnOrder;
import core.AbstractParameters;
import core.components.*;
import core.interfaces.IGamePhase;
import core.turnorders.TurnOrder;
import games.GameType;
import games.calico.CalicoTypes.Button;
import games.calico.CalicoTypes.Cat;
import games.calico.CalicoTypes.DesignGoalTile;
import games.calico.components.CalicoBoard;
import games.calico.components.CalicoBoardTile;
import games.calico.components.CalicoCatCard;
import games.calico.components.CalicoTile;
import games.terraformingmars.TMGameState.ResourceMapping;
import games.terraformingmars.TMTypes;
import games.terraformingmars.actions.PlaceTile;
import games.terraformingmars.actions.TMAction;
import games.terraformingmars.components.Award;
import games.terraformingmars.components.Milestone;
import games.terraformingmars.components.TMCard;
import games.terraformingmars.rules.Discount;
import games.terraformingmars.rules.effects.Bonus;
import games.terraformingmars.rules.requirements.ActionTypeRequirement;
import games.terraformingmars.rules.requirements.TagsPlayedRequirement;
import utilities.Pair;
import utilities.Utils;
import utilities.Vector2D;

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

    // Effects and actions played - to be updated when actions are added
    // HashSet<TMAction>[] playerExtraActions;

    // Player-specific values
    CalicoBoard[] playerBoards;  //are design token points going to be stored here?
    HashMap<Cat, Counter>[] playerCatScore; //number of that cat tokens
    HashMap<Button, Counter>[] playerButtonScore; //number of those buttons
    HashMap<DesignGoalTile, Counter>[] playerGoalScore; //score acheived by each design goal tile
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
            addAll(Arrays.asList(playerTiles));
            addAll(Arrays.asList(activeCats));
            addAll(Arrays.asList(playerBoards));
            addAll(Arrays.asList(playerFinalPoints));
            for (int i = 0; i < getNPlayers(); i++) {
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
     * //TODO
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
        return playerResources[playerId].get(TMTypes.Resource.TR).getValue();
//        return countPoints(playerId);
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
        int result = Objects.hash(super.hashCode(), turn, seed, tileBag, tileMarket);
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
        result = 31 * result + Arrays.hashCode(playerBoards);
        sb.append(result).append("|5|");
        result = 31 * result + Arrays.hashCode(playerCatScore);
        sb.append(result).append("|6|");
        result = 31 * result + Arrays.hashCode(playerButtonScore);
        sb.append(result).append("|7|");
        result = 31 * result + Arrays.hashCode(playerGoalScore);
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

    public int discountActionTypeCost(TMAction action, int player) {
        // Apply tag discount effects
        int discount = 0;
        if (player == -1) player = getCurrentPlayer();
        for (Map.Entry<Requirement, Integer> e : playerDiscountEffects[player].entrySet()) {
            if (e.getKey() instanceof ActionTypeRequirement) {
                if (e.getKey().testCondition(action)) {
                    discount += e.getValue();
                }
            }
        }
        return discount;
    }

    public int discountCardCost(TMCard card, int player) {
        // Apply tag discount effects
        int discount = 0;
        if (player == -1) player = getCurrentPlayer();
        for (TMTypes.Tag t : card.tags) {
            for (Map.Entry<Requirement, Integer> e : playerDiscountEffects[player].entrySet()) {
                if (e.getKey() instanceof TagsPlayedRequirement) {
                    boolean found = false;
                    for (CalicoTypes.Tag tt : ((TagsPlayedRequirement) e.getKey()).tags) {
                        if (tt == t) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        discount += e.getValue();
                    }
                }
            }
        }
        return discount;
    }

    public boolean isCardFree(TMCard card, int player) {
        return isCardFree(card, 0, player);
    }

    public boolean isCardFree(TMCard card, int amountPaid, int player) {
        return card.cost - discountCardCost(card, player) - amountPaid <= 0;
    }

    public Counter stringToGPCounter(String s) {
        TMTypes.GlobalParameter p = Utils.searchEnum(TMTypes.GlobalParameter.class, s);
        if (p != null) return globalParameters.get(p);
        return null;
    }

    public Counter stringToGPOrPlayerResCounter(String s, int player) {
        if (player == -1) player = getCurrentPlayer();
        Counter which = stringToGPCounter(s);

        if (which == null) {
            // A resource or production instead
            TMTypes.Resource res = TMTypes.Resource.valueOf(s.split("prod")[0]);
            if (s.contains("prod")) {
                which = playerProduction[player].get(res);
            } else {
                which = playerResources[player].get(res);
            }
        }
        return which;
    }

    public static TMTypes.GlobalParameter counterToGP(Counter c) {
        return Utils.searchEnum(TMTypes.GlobalParameter.class, c.getComponentName());
    }

    public boolean canPlayerPay(int player, TMTypes.Resource res, int amount) {
        // Production check
        return canPlayerPay(player, null, null, res, amount, true);
    }

    public boolean canPlayerPay(int player, TMCard card, HashSet<TMTypes.Resource> from, TMTypes.Resource to, int amount) {
        return canPlayerPay(player, card, from, to, amount, false);
    }

    public boolean canPlayerPay(int player, TMCard card, HashSet<TMTypes.Resource> from, TMTypes.Resource to, int amount, boolean production) {
        if (player == -3) return true;  // In solo play, this is the neutral player

        if (production) {
            Counter c = playerProduction[player].get(to);
            if (c.getMinimum() < 0) return c.getValue() + Math.abs(c.getMinimum()) >= amount;
            return c.getValue() >= amount;
        }

        int sum = playerResourceSum(player, card, from, to, true);
        return card != null ? isCardFree(card, sum, -1) : sum >= amount;
    }

    public int playerResourceSum(int player, TMCard card, HashSet<TMTypes.Resource> from, TMTypes.Resource to, boolean itself) {
        if (from == null || from.size() > 0) {
            int sum = 0;
            if (itself || from != null && from.contains(to))
                sum = playerResources[player].get(to).getValue();  // All resources can be exchanged for themselves at rate 1.0

            // Add resources that this player can use as the "to" resource for this action
            for (ResourceMapping resMap : playerResourceMap[player]) {
                if ((from == null || from.contains(resMap.from))
                        && resMap.to == to
                        && (resMap.requirement == null || resMap.requirement.testCondition(card))) {
                    int n = playerResources[player].get(resMap.from).getValue();
                    sum += n * resMap.rate;
                }
            }
            return sum;
        }
        return 0;
    }

    /**
     * Check if player can transform one resource into another, when buying a card
     *
     * @param card - card to buy; can be null, and resource mappings that require card tags will be skipped
     * @param from - resource to transform from; can be null, then all resources in the player's mapping will be checked
     * @param to   - resource to transform to
     * @return all resources that can be transformed into given res
     */
    public HashSet<TMTypes.Resource> canPlayerTransform(int player, TMCard card, TMTypes.Resource from, TMTypes.Resource to) {
        HashSet<TMTypes.Resource> resources = new HashSet<>();
        for (ResourceMapping resMap : playerResourceMap[player]) {
            if ((from == null || resMap.from == from) && resMap.to == to && (resMap.requirement == null || resMap.requirement.testCondition(card))) {
                if (playerResources[player].get(resMap.from).getValue() > 0) {
                    resources.add(resMap.from);
                }
            }
        }
        return resources;
    }

    public void playerPay(int player, TMTypes.Resource resource, int amount) {
        playerResources[player].get(resource).decrement(Math.abs(amount));
    }

    public double getResourceMapRate(TMTypes.Resource from, TMTypes.Resource to) {
        double rate = 1.;
        for (ResourceMapping rm : playerResourceMap[getCurrentPlayer()]) {
            if (rm.from == from && rm.to == to) {
                rate = rm.rate;
                break;
            }
        }
        return rate;
    }

    public void addDiscountEffects(LinkedList<Discount> discounts) {
        int player = getCurrentPlayer();
        for(Discount d : discounts){
            Requirement r = d.a;
            int amount = d.b;
            if (playerDiscountEffects[player].containsKey(r)) {
                playerDiscountEffects[player].put(r, playerDiscountEffects[player].get(r) + amount);
            } else {
                playerDiscountEffects[player].put(r, amount);
            }
        }
    }

    public void addPersistingEffects(Effect[] effects) {
        int player = getCurrentPlayer();
        playerPersistingEffects[player].addAll(Arrays.asList(effects));
    }

    // if add is false, replace instead
    public void addResourceMappings(HashSet<ResourceMapping> maps, boolean add) {
        int player = getCurrentPlayer();
        HashSet<ResourceMapping> toRemove = new HashSet<>();
        HashSet<ResourceMapping> toAdd = new HashSet<>();
        for (ResourceMapping resMapNew : maps) {
            boolean added = false;
            for (ResourceMapping resMap : playerResourceMap[player]) {
                if (resMap.from == resMapNew.from && resMap.to == resMapNew.to) {
                    if (resMapNew.requirement == null || resMapNew.requirement.equals(resMap.requirement)) {
                        if (add) {
                            resMap.rate += resMapNew.rate;
                        } else {
                            toRemove.add(resMap);
                            toAdd.add(resMapNew);
                        }
                        added = true;
                    }
                }
            }
            if (!added) toAdd.add(resMapNew);
        }
        playerResourceMap[player].removeAll(toRemove);
        playerResourceMap[player].addAll(toAdd);
    }

    public boolean hasPlacedTile(int player) {
        for (TMTypes.Tile t : playerTilesPlaced[player].keySet()) {
            if (t.canBeOwned() && playerTilesPlaced[player].get(t).getValue() > 0) return true;
        }
        return false;
    }

    public boolean anyTilesPlaced() {
        for (int i = 0; i < getNPlayers(); i++) {
            for (Counter c : playerTilesPlaced[i].values()) {
                if (c.getValue() > 0) return true;
            }
        }
        return getNPlayers() == 1;
    }

    public boolean anyTilesPlaced(TMTypes.Tile type) {
        for (int i = 0; i < getNPlayers(); i++) {
            if (playerTilesPlaced[i].get(type).getValue() > 0) return true;
        }
        return getNPlayers() == 1 && (type == TMTypes.Tile.City || type == TMTypes.Tile.Greenery);
    }

    public int countPoints(int player) {
        // Add TR
        int points = playerResources[player].get(TMTypes.Resource.TR).getValue();
        // Add milestones
        points += countPointsMilestones(player);
        // Add awards
        points += countPointsAwards(player);
        // Add points from board
        points += countPointsBoard(player);
        // Add points on cards
        points += countPointsCards(player);
        return points;
    }

    public int countPointsMilestones(int player) {
        CalicoGameParameters params = (CalicoGameParameters) gameParameters;
        int points = 0;
        for (Milestone m : milestones) {
            if (m.isClaimed() && m.claimed == player) {
                points += params.nPointsMilestone;
            }
        }
        return points;
    }

    public int countPointsAwards(int player) {
        CalicoGameParameters params = (CalicoGameParameters) gameParameters;
        int points = 0;
        for (Award a : awards) {
            Pair<HashSet<Integer>, HashSet<Integer>> winners = awardWinner(a);
            if (winners != null) {
                if (winners.a.contains(player)) points += params.nPointsAwardFirst;
                if (winners.b.contains(player) && winners.a.size() == 1)
                    points += params.nPointsAwardSecond;
            }
        }
        return points;
    }

    public Pair<HashSet<Integer>, HashSet<Integer>> awardWinner(Award a) {
        if (a.isClaimed()) {
            int best = -1;
            int secondBest = -1;
            HashSet<Integer> bestPlayer = new HashSet<>();
            HashSet<Integer> secondBestPlayer = new HashSet<>();
            for (int i = 0; i < getNPlayers(); i++) {
                int playerPoints = a.checkProgress(this, i);
                if (playerPoints >= best) {
                    if (playerPoints > best) {
                        secondBestPlayer = new HashSet<>(bestPlayer);
                        secondBest = best;
                        bestPlayer.clear();
                        bestPlayer.add(i);
                        best = playerPoints;
                    }
                } else if (playerPoints > secondBest) {
                    secondBestPlayer.clear();
                    secondBestPlayer.add(i);
                    secondBest = playerPoints;
                }
            }
            for (int i = 0; i < getNPlayers(); i++) {
                int playerPoints = a.checkProgress(this, i);
                if (playerPoints == best) {
                    bestPlayer.add(i);
                } else if (playerPoints == secondBest) {
                    secondBestPlayer.add(i);
                }
            }
            if (getNPlayers() <= 2 || bestPlayer.size() > 1)
                secondBestPlayer.clear();  // No second-best awarded unless there are 3 or more players, and only 1 got first place
            return new Pair<>(bestPlayer, secondBestPlayer);
        }
        return null;
    }

    public int countPointsBoard(int player) {
        int points = 0;
        // Greeneries
        points += playerTilesPlaced[player].get(TMTypes.Tile.Greenery).getValue();
        // Add cities on board
        for (int i = 0; i < board.getHeight(); i++) {
            for (int j = 0; j < board.getWidth(); j++) {
                CalicoMapTile mt = board.getElement(j, i);
                if (mt != null && mt.getTilePlaced() == TMTypes.Tile.City) {
                    // Count adjacent greeneries
                    points += PlaceTile.nAdjacentTiles(this, mt, TMTypes.Tile.Greenery);
                }
            }
        }
        return points;
    }

    public int countPointsCards(int player) {
        int points = 0;

        // Normal points
        points += playerCardPoints[player].getValue();
        // Complicated points
        for (TMCard card : playerComplicatedPointCards[player].getComponents()) {
            if (card == null) {
                continue;
            }
            if (card.pointsThreshold != null) {
                if (card.pointsResource != null) {
                    if (card.nResourcesOnCard >= card.pointsThreshold) {
                        points += card.nPoints;
                    }
                }
            } else {
                if (card.pointsResource != null) {
                    points += card.nPoints * card.nResourcesOnCard;
                } else if (card.pointsTag != null) {
                    points += card.nPoints * playerCardsPlayedTags[player].get(card.pointsTag).getValue();
                } else if (card.pointsTile != null) {
                    if (card.pointsTileAdjacent && card.mapTileIDTilePlaced >= 0) {  // TODO: mapTileIDPlaced should have been set in this case, bug
                        // only adjacent tiles count
                        CalicoMapTile mt = (CalicoMapTile) getComponentById(card.mapTileIDTilePlaced);
                        List<Vector2D> neighbours = PlaceTile.getNeighbours(new Vector2D(mt.getX(), mt.getY()));
                        for (Vector2D n : neighbours) {
                            CalicoMapTile e = board.getElement(n.getX(), n.getY());
                            if (e != null && e.getTilePlaced() == card.pointsTile) {
                                points += card.nPoints;
                            }
                        }
                    } else {
                        points += card.nPoints * playerTilesPlaced[player].get(card.pointsTile).getValue();
                    }
                } else if (card.getComponentName().equalsIgnoreCase("capital")) {
                    // x VP per Ocean adjacent
                    int position = card.mapTileIDTilePlaced;
                    CalicoMapTile mt = (CalicoMapTile) getComponentById(position);
                    points += card.nPoints * PlaceTile.nAdjacentTiles(this, mt, TMTypes.Tile.Ocean);
                }
            }
        }
        return points;
    }
}
