package games.calico;

import core.AbstractGameState;
import core.CoreConstants;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.Counter;
import core.components.Deck;
import core.components.GridBoard;
import games.calico.CalicoTypes.Button;
import games.calico.CalicoTypes.Cat;
import games.calico.CalicoTypes.DesignGoalTile;
import games.calico.components.CalicoBoard;
import games.calico.components.CalicoTile;
import games.terraformingmars.TMGameState;
import games.terraformingmars.TMTurnOrder;
import games.terraformingmars.actions.ClaimAwardMilestone;
import games.terraformingmars.actions.CompoundAction;
import games.terraformingmars.actions.ModifyGlobalParameter;
import games.terraformingmars.actions.ModifyPlayerResource;
import games.terraformingmars.actions.PayForAction;
import games.terraformingmars.actions.PlaceTile;
import games.terraformingmars.actions.SellProjects;
import games.terraformingmars.actions.TMAction;
import games.terraformingmars.components.Award;
import games.terraformingmars.components.Milestone;
import games.terraformingmars.components.TMCard;
import games.terraformingmars.rules.requirements.TagOnCardRequirement;
// import games.terraformingmars.TMTurnOrder;
// import games.terraformingmars.actions.ClaimAwardMilestone;
// import games.terraformingmars.actions.CompoundAction;
// import games.terraformingmars.actions.ModifyGlobalParameter;
// import games.terraformingmars.actions.ModifyPlayerResource;
// import games.terraformingmars.actions.PayForAction;
// import games.terraformingmars.actions.PlaceTile;
// import games.terraformingmars.actions.SellProjects;
// import games.terraformingmars.actions.TMAction;
// import games.terraformingmars.components.Award;
// import games.terraformingmars.components.Milestone;
// import games.terraformingmars.components.TMCard;
// import games.terraformingmars.rules.requirements.TagOnCardRequirement;
// import games.terraformingmars.TMGameState;
// import games.terraformingmars.TMTurnOrder;
// import games.terraformingmars.TMTypes;
// import games.terraformingmars.actions.ClaimAwardMilestone;
// import games.terraformingmars.actions.CompoundAction;
// import games.terraformingmars.actions.ModifyGlobalParameter;
// import games.terraformingmars.actions.ModifyPlayerResource;
// import games.terraformingmars.actions.PayForAction;
// import games.terraformingmars.actions.PlaceTile;
// import games.terraformingmars.actions.SellProjects;
// import games.terraformingmars.actions.TMAction;
// import games.terraformingmars.components.Award;
// import games.terraformingmars.components.Milestone;
// import games.terraformingmars.components.TMCard;
// import games.terraformingmars.components.TMMapTile;
// import games.terraformingmars.rules.requirements.TagOnCardRequirement;
// import games.terraformingmars.TMGameState;
// import games.terraformingmars.TMTurnOrder;
// import games.terraformingmars.TMTypes;
// import games.terraformingmars.actions.ClaimAwardMilestone;
// import games.terraformingmars.actions.CompoundAction;
// import games.terraformingmars.actions.ModifyGlobalParameter;
// import games.terraformingmars.actions.ModifyPlayerResource;
// import games.terraformingmars.actions.PayForAction;
// import games.terraformingmars.actions.PlaceTile;
// import games.terraformingmars.actions.SellProjects;
// import games.terraformingmars.actions.TMAction;
// import games.terraformingmars.components.Award;
// import games.terraformingmars.components.Milestone;
// import games.terraformingmars.components.TMCard;
// import games.terraformingmars.components.TMMapTile;
// import games.terraformingmars.rules.requirements.TagOnCardRequirement;
// import games.terraformingmars.actions.*;
// import games.terraformingmars.components.Award;
// import games.terraformingmars.components.Milestone;
// import games.terraformingmars.components.TMCard;
// import games.terraformingmars.components.TMMapTile;
// import games.terraformingmars.rules.requirements.TagOnCardRequirement;
import utilities.Vector2D;

import java.util.*;

// import static games.terraformingmars.TMGameState.TMPhase.*;
// import static games.terraformingmars.TMTypes.Resource.MegaCredit;
// import static games.terraformingmars.TMTypes.Resource.TR;
// import static games.terraformingmars.TMTypes.StandardProject.*;
// import static games.terraformingmars.TMTypes.ActionType.*;

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

        //add 3 to the tile market
        gs.tileMarket.add(gs.tileBag.draw());
        gs.tileMarket.add(gs.tileBag.draw());
        gs.tileMarket.add(gs.tileBag.draw());

        gs.tileBag.shuffle(new Random(params.getRandomSeed()));
        
        gs.playerBoards = new CalicoBoard[gs.getNPlayers()];

        gs.playerCatScore = new HashMap[gs.getNPlayers()];
        gs.playerButtonScore = new HashMap[gs.getNPlayers()];
        gs.playerGoalScore = new HashMap[gs.getNPlayers()];
        gs.playerFinalPoints = new Counter[gs.getNPlayers()];

        gs.playerTiles = new Deck[gs.getNPlayers()];

        for (int i = 0; i< gs.getNPlayers(); i++){
            //design tiles to be used in playerBoard
            DesignGoalTile[] goalTiles = params.getRandomDesignTile();

            gs.playerBoards[i] = new CalicoBoard(params.boardSize);
            // TODO assign a board to each player and fill up board

            //fill in point hashmaps
            gs.playerCatScore[i] = new HashMap<Cat, Counter>();
            for (int j = 0; j < gs.activeCats.size(); j++) {
                Cat c = gs.activeCats.get(j).getCat();
                gs.playerCatScore[i].put(c, new Counter(0,"player" + i +"'s cat points for " + c.getName()));
            }

            gs.playerButtonScore[i] = new HashMap<Button, Counter>();
            for (Button b : Button.values()) {
                gs.playerButtonScore[i].put(b, new Counter(0,"player" + i +"'s button points for " + b));
            }

            gs.playerGoalScore[i] = new HashMap<DesignGoalTile, Integer>();
            for (DesignGoalTile g : DesignGoalTile.values()) {
                gs.playerGoalScore[i].put(g, 0);
            }

            gs.playerTiles[i] = new Deck<CalicoTile>("player" + i +"'s tiles on hand", i, CoreConstants.VisibilityMode.VISIBLE_TO_OWNER);
            gs.playerTiles[i].add(gs.tileBag.draw());
            gs.playerTiles[i].add(gs.tileBag.draw());
            gs.playerTiles[i].add(gs.tileBag.draw());
        }

//        TMCard cccc = null;
//        try {
//            GsonBuilder gsonBuilder = new GsonBuilder()
//                    .registerTypeAdapter(Requirement.class, new SimpleSerializer<Requirement>())
//                    .registerTypeAdapter(Effect.class, new SimpleSerializer<Effect>())
//                    .registerTypeAdapter(Discount.class, new Discount())
//                    .registerTypeAdapter(TMAction.class, new SimpleSerializer<TMAction>())
//                    ;
//
////            GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter(Requirement.class, new RequirementJSONSerializer());
//            Gson gson = gsonBuilder.setPrettyPrinting().create();
//
//            FileWriter fw = new FileWriter("data/terraformingmars/projectCards/jsonCardsCORP.json");
//            fw.write("[");
//            for (TMCard c: gs.projectCards.getComponents()) {
//                cccc = c;
//                TMCard cCopy = c.copySerializable();
//                String jsonString = gson.toJson(cCopy);
//                fw.write(jsonString + ",");
//                System.out.println(jsonString);
//                fw.flush();
//            }
//            fw.write("]");
//            fw.flush();
//            fw.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println(cccc.toString());
//        }

        if (gs.getNPlayers() == 1) {
            // Disable milestones and awards for solo play
            gs.milestones = new HashSet<>();
            gs.awards = new HashSet<>();
        }

        // Shuffle dekcs
        gs.projectCards.shuffle(gs.getRnd());
        gs.corpCards.shuffle(gs.getRnd());

        HashMap<CalicoTypes.Tag, Counter>[] playerCardsPlayedTags;
        HashSet<AbstractAction>[] playerCardsPlayedEffects;
        HashSet<AbstractAction>[] playerCardsPlayedActions;
        HashMap<CalicoTypes.CardType, Counter>[] playerCardsPlayedTypes;
        HashMap<CalicoTypes.Tile, Counter>[] tilesPlaced;

        gs.playerCorporations = new TMCard[gs.getNPlayers()];
        gs.playerCardChoice = new Deck[gs.getNPlayers()];
        gs.playerHands = new Deck[gs.getNPlayers()];
        gs.playerComplicatedPointCards = new Deck[gs.getNPlayers()];
        gs.playedCards = new Deck[gs.getNPlayers()];
        gs.playerCardPoints = new Counter[gs.getNPlayers()];
        for (int i = 0; i < gs.getNPlayers(); i++) {
            gs.playerHands[i] = new Deck<>("Hand of p" + i, i, CoreConstants.VisibilityMode.VISIBLE_TO_OWNER);
            gs.playerCardChoice[i] = new Deck<>("Card Choice for p" + i, i, CoreConstants.VisibilityMode.VISIBLE_TO_OWNER);
            gs.playerComplicatedPointCards[i] = new Deck<>("Resource or Points Cards Played by p" + i, i, CoreConstants.VisibilityMode.VISIBLE_TO_ALL);
            gs.playedCards[i] = new Deck<>("Other Cards Played by p" + i, i, CoreConstants.VisibilityMode.VISIBLE_TO_ALL);
            gs.playerCardPoints[i] = new Counter(0, 0, params.maxPoints, "Points of p" + i);
        }

        gs.playerTilesPlaced = new HashMap[gs.getNPlayers()];
        gs.playerCardsPlayedTypes = new HashMap[gs.getNPlayers()];
        gs.playerCardsPlayedTags = new HashMap[gs.getNPlayers()];
        gs.playerExtraActions = new HashSet[gs.getNPlayers()];
        gs.playerPersistingEffects = new HashSet[gs.getNPlayers()];
        for (int i = 0; i < gs.getNPlayers(); i++) {
            gs.playerTilesPlaced[i] = new HashMap<>();
            for (CalicoTypes.Tile t : CalicoTypes.Tile.values()) {
                gs.playerTilesPlaced[i].put(t, new Counter(0, 0, params.maxPoints, t.name() + " tiles placed player " + i));
            }
            gs.playerCardsPlayedTypes[i] = new HashMap<>();
            for (CalicoTypes.CardType t : CalicoTypes.CardType.values()) {
                gs.playerCardsPlayedTypes[i].put(t, new Counter(0, 0, params.maxPoints, t.name() + " cards played player " + i));
            }
            gs.playerCardsPlayedTags[i] = new HashMap<>();
            for (CalicoTypes.Tag t : CalicoTypes.Tag.values()) {
                gs.playerCardsPlayedTags[i].put(t, new Counter(0, 0, params.maxPoints, t.name() + " cards played player " + i));
            }
            gs.playerExtraActions[i] = new HashSet<>();
            gs.playerPersistingEffects[i] = new HashSet<>();
        }

        gs.nAwardsFunded = new Counter(0, 0, params.nCostAwards.length, "Awards funded");
        gs.nMilestonesClaimed = new Counter(0, 0, params.nCostMilestone.length, "Milestones claimed");

        // First thing to do is select corporations
        gs.setGamePhase(CorporationSelect);
        for (int i = 0; i < gs.getNPlayers(); i++) {
            // TODO: remove, used for testing corps
//            for (TMCard c: gs.corpCards.getComponents()) {
//                if (c.getComponentName().equals("UNITED NATIONS MARS INITIATIVE")) {
//                    gs.playerCardChoice[i].add(c);
//                }
//            }
            for (int j = 0; j < params.nCorpChoiceStart; j++) {
                gs.playerCardChoice[i].add(gs.corpCards.pick(0));
            }
        }

        // Solo setup: place X cities randomly, with 1 greenery adjacent each (no oxygen increase)
        if (gs.getNPlayers() == 1) {
            int boardH = gs.board.getHeight();
            int boardW = gs.board.getWidth();
            gs.getTurnOrder().setTurnOwner(1);
            for (int i = 0; i < params.soloCities; i++) {
                // Place city + greenery adjacent
                PlaceTile pt = new PlaceTile(1, CalicoTypes.Tile.City, CalicoTypes.MapTileType.Ground, true);
                List<AbstractAction> actions = pt._computeAvailableActions(gs);
                PlaceTile action = (PlaceTile) actions.get(gs.getRnd().nextInt(actions.size()));
                action.execute(gs);
                CalicoMapTile mt = (CalicoMapTile) gs.getComponentById(action.mapTileID);
                List<Vector2D> neighbours = PlaceTile.getNeighbours(new Vector2D(mt.getX(), mt.getY()));
                boolean placed = false;
                while (!placed) {
                    Vector2D v = neighbours.get(gs.getRnd().nextInt(neighbours.size()));
                    CalicoMapTile mtn = gs.board.getElement(v.getX(), v.getY());
                    if (mtn != null && mtn.getOwnerId() == -1 && mtn.getTileType() == CalicoTypes.MapTileType.Ground) {
                        mtn.setTilePlaced(CalicoTypes.Tile.Greenery, gs);
                        placed = true;
                    }
                }
            }
            gs.getTurnOrder().setTurnOwner(0);
            gs.globalParameters.get(CalicoTypes.GlobalParameter.Oxygen).setValue(0);
        }

        gs.generation = 1;
    }

    //This is called every time an action is taken by one of the players, human or AI.
    //called after action is applied to state
    //use _beforeAction if needed to add logic before this
    @Override
    protected void _afterAction(AbstractGameState currentState, AbstractAction action) {
        TMGameState gs = (TMGameState) currentState;
        CalicoGameParameters params = (CalicoGameParameters) gs.getGameParameters();

        if (gs.getGamePhase() == CorporationSelect) {
            boolean allChosen = true;
            for (TMCard card : gs.getPlayerCorporations()) {
                if (card == null) {
                    allChosen = false;
                    break;
                }
            }
            if (allChosen) {
                gs.setGamePhase(Research);
                gs.getTurnOrder().endRound(gs);
                for (int i = 0; i < gs.getNPlayers(); i++) {
                    for (int j = 0; j < params.nProjectsStart; j++) {
                        gs.playerCardChoice[i].add(gs.drawCard());
                    }
                    // TODO: remove, used for testing specific cards
//                    for (TMCard c: gs.projectCards.getComponents()) {
//                        if (c.getComponentName().equalsIgnoreCase("search for life")) {
//                            gs.playerCardChoice[i].add(c);
//                        }
//                    }
                }
            }
        } else if (gs.getGamePhase() == Research) {
            // Check if finished: no ore cards in card choice decks
            boolean allDone = true;
            for (Deck<TMCard> deck : gs.getPlayerCardChoice()) {
                if (deck.getSize() > 0) {
                    allDone = false;
                    break;
                }
            }
            if (allDone) {
                gs.setGamePhase(Actions);
                gs.getTurnOrder().endRound(gs);
            }
        } else if (gs.getGamePhase() == Actions) {
            // Check if finished: all players passed
            if (((TMTurnOrder) gs.getTurnOrder()).nPassed == gs.getNPlayers()) {
                // Production
                for (int i = 0; i < gs.getNPlayers(); i++) {
                    // First, energy turns to heat
                    gs.getPlayerResources()[i].get(CalicoTypes.Resource.Heat).increment(gs.getPlayerResources()[i].get(CalicoTypes.Resource.Energy).getValue());
                    gs.getPlayerResources()[i].get(CalicoTypes.Resource.Energy).setValue(0);
                    // Then, all production values are added to resources
                    for (CalicoTypes.Resource res : CalicoTypes.Resource.values()) {
                        if (res.isPlayerBoardRes()) {
                            gs.getPlayerResources()[i].get(res).increment(gs.getPlayerProduction()[i].get(res).getValue());
                        }
                    }
                    // TR also adds to mega credits
                    gs.getPlayerResources()[i].get(CalicoTypes.Resource.MegaCredit).increment(gs.playerResources[i].get(TR).getValue());
                }

                // Check game end before next research phase
                if (checkGameEnd(gs)) {

                    if (gs.getNPlayers() == 1) {
                        // If solo, game goes for 14 generations regardless of global parameters
                        CoreConstants.GameResult won = CoreConstants.GameResult.WIN_GAME;
                        for (CalicoTypes.GlobalParameter p : gs.globalParameters.keySet()) {
                            if (p != null && p.countsForEndGame() && !gs.globalParameters.get(p).isMaximum())
                                won = CoreConstants.GameResult.LOSE_GAME;
                        }
                        gs.setGameStatus(CoreConstants.GameResult.GAME_END);
                        gs.setPlayerResult(won, 0);
                    } else {
                        endGame(gs);
                    }

                    return;
                }

                // Move to research phase
                gs.getTurnOrder().endRound(gs);
                gs.setGamePhase(Research);
                for (int j = 0; j < params.nProjectsResearch; j++) {
                    for (int i = 0; i < gs.getNPlayers(); i++) {
                        TMCard c = gs.drawCard();
                        if (c != null) {
                            gs.playerCardChoice[i].add(c);
                        } else {
                            break;
                        }
                    }
                }
                for (int i = 0; i < gs.getNPlayers(); i++) {
                    // Mark player actions unused
                    for (TMCard c : gs.playerComplicatedPointCards[i].getComponents()) {
                        c.actionPlayed = false;
                    }
                    // Reset resource increase
                    for (CalicoTypes.Resource res : CalicoTypes.Resource.values()) {
                        gs.playerResourceIncreaseGen[i].put(res, false);
                    }
                }

                // Next generation
                gs.generation++;
            }
        }
    }

    //return a list with all actions available for the current player, in the context of the game state object
    @Override
    protected List<AbstractAction> _computeAvailableActions(AbstractGameState gameState) {
        // play a card (if valid), standard projects, claim milestone, fund award, card actions, 8 plants -> greenery, 8 heat -> temperature, pass
        // event cards are face-down after played, tags don't apply!
        ArrayList<AbstractAction> actions = new ArrayList<>();
        TMGameState gs = (TMGameState) gameState;
        CalicoGameParameters params = (CalicoGameParameters) gs.getGameParameters();
        int player = gs.getCurrentPlayer();

        List<AbstractAction> possibleActions = getAllActions(gs);

        // Wrap actions that can actually be played and must be paid for
        for (AbstractAction aa : possibleActions) {
            TMAction a = (TMAction) aa;
            if (a != null && a.canBePlayed(gs)) {
                if (a.getCost() != 0) {
                    actions.add(new PayForAction(player, a));
                } else {
                    actions.add(a);
                }
            }
        }

        return actions;
    }

    /**
     * Bypass regular computeActions function call to list all actions possible in the current state, some of which
     * might not be playable at the moment. Requirements list on the action informs of why an action is not playable.
     * Used to display full information in the GUI for unplayable (but possible) actions.
     *
     * @param gs - current state
     * @return - list of all actions available, playable and not playable
     */
    public List<AbstractAction> getAllActions(TMGameState gs) {
        // If there is an action in progress (see IExtendedSequence), then delegate to that
        // Regular game loop calls will not reach here, but external calls (e.g. GUI, agents) will and need correct info
        if (gs.isActionInProgress()) {
            return gs.getActionsInProgress().peek()._computeAvailableActions(gs);
        }

        // Calculate all actions

        CalicoGameParameters params = (CalicoGameParameters) gs.getGameParameters();
        int player = gs.getCurrentPlayer();
        List<AbstractAction> possibleActions = new ArrayList<>();

        if (gs.getGamePhase() == CorporationSelect) {
            // Decide one card at a time, first one player, then the other
            Deck<TMCard> cardChoice = gs.getPlayerCardChoice()[player];
            if (cardChoice.getSize() == 0) {
                possibleActions.add(new TMAction(player));  // Pass
            } else {
                for (int i = 0; i < cardChoice.getSize(); i++) {
                    possibleActions.add(new BuyCard(player, cardChoice.get(i).getComponentID(), 0));
                }
            }
        } else if (gs.getGamePhase() == Research) {
            // Decide one card at a time, first one player, then the other
            Deck<TMCard> cardChoice = gs.getPlayerCardChoice()[player];
            if (cardChoice.getSize() == 0) {
                possibleActions.add(new TMAction(player));  // Pass
            } else {
                cardChoice.get(0).actionPlayed = false;
                BuyCard a = new BuyCard(player, cardChoice.get(0).getComponentID(), params.getProjectPurchaseCost());
                if (a.canBePlayed(gs)) {
                    possibleActions.add(a);
                }
                possibleActions.add(new DiscardCard(player, cardChoice.get(0).getComponentID(), true));
            }
        } else {

            if (gs.generation == 1) {
                // Check if any players have decided first action from corporations
                TMCard corpCard = gs.playerCorporations[player];
                if (corpCard.firstAction != null && !corpCard.firstActionExecuted) {
                    possibleActions.add(corpCard.firstAction);
                    return possibleActions;
                }
            }

            possibleActions.add(new TMAction(player));  // Can always just pass

            // Play a card actions
            for (int i = 0; i < gs.playerHands[player].getSize(); i++) {
                possibleActions.add(new PlayCard(player, gs.playerHands[player].get(i), false));
            }

            // Buy a standard project
            // - Discard cards for MC
            if (gs.playerHands[player].getSize() > 0) {
                possibleActions.add(new SellProjects(player));
            }

            // - Increase energy production 1 step for 11 MC
            possibleActions.add(new ModifyPlayerResource(PowerPlant, params.getnCostSPEnergy(), player, 1, CalicoTypes.Resource.Energy));

            // - Increase temperature 1 step for 14 MC
            possibleActions.add(new ModifyGlobalParameter(StandardProject, CalicoTypes.Resource.MegaCredit, params.getnCostSPTemp(), CalicoTypes.GlobalParameter.Temperature, 1, false));

            // - Place ocean tile for 18 MC
            possibleActions.add(new PlaceTile(Aquifer, params.getnCostSPOcean(), player, CalicoTypes.Tile.Ocean, CalicoTypes.MapTileType.Ocean));

            // - Place greenery tile for 23 MC
            possibleActions.add(new PlaceTile(Greenery, params.getnCostSPGreenery(), player, CalicoTypes.Tile.Greenery, CalicoTypes.MapTileType.Ground));

            // - Place city tile and increase MC prod by 1 for 25 MC
            TMAction a1 = new PlaceTile(player, CalicoTypes.Tile.City, CalicoTypes.MapTileType.Ground, true);
            TMAction a2 = new ModifyPlayerResource(player, params.nSPCityMCGain, CalicoTypes.Resource.MegaCredit, true);
            possibleActions.add(new CompoundAction(StandardProject, player, new TMAction[]{a1, a2}, params.nCostSPCity));

            // - Air Scraping, increase Venus parameter for 15MC, if Venus expansion enabled
            if (params.expansions.contains(CalicoTypes.Expansion.Venus)) {
                possibleActions.add(new ModifyGlobalParameter(StandardProject, MegaCredit, params.nCostVenus, CalicoTypes.GlobalParameter.Venus, 1, false));
            }

            // Claim a milestone
            int nMilestonesClaimed = gs.getnMilestonesClaimed().getValue();
            int milestoneCost = 0;
            if (!gs.getnMilestonesClaimed().isMaximum()) milestoneCost = params.getnCostMilestone()[nMilestonesClaimed];
            for (Milestone m : gs.milestones) {
                possibleActions.add(new ClaimAwardMilestone(player, m, milestoneCost));
            }
            // Fund an award
            int nAwardsFunded = gs.getnAwardsFunded().getValue();
            int awardCost = 0;
            if (!gs.getnAwardsFunded().isMaximum()) awardCost = params.getnCostAwards()[nAwardsFunded];
            for (Award a : gs.awards) {
                possibleActions.add(new ClaimAwardMilestone(player, a, awardCost));
            }

            // Use an active card action  - only 1, mark as used, then mark unused at the beginning of next generation
            possibleActions.addAll(gs.playerExtraActions[player]);

            // 8 plants into greenery tile
            possibleActions.add(new PlaceTile(CalicoTypes.BasicResourceAction.PlantToGreenery, params.getnCostGreeneryPlant(), player, CalicoTypes.Tile.Greenery, CalicoTypes.MapTileType.Ground));
            // 8 heat into temperature increase
            possibleActions.add(new ModifyGlobalParameter(BasicResourceAction, CalicoTypes.Resource.Heat, params.getnCostTempHeat(), CalicoTypes.GlobalParameter.Temperature, 1, false));
        }

        return possibleActions;
    }

    @Override
    protected void illegalActionPlayed(AbstractGameState gameState, AbstractAction action) {
        next(gameState, new TMAction(gameState.getCurrentPlayer()));
    }

    private boolean checkGameEnd(TMGameState gs) {
        boolean ended = true;
        if (gs.getNPlayers() == 1) {
            // If solo, game goes for 14 generations regardless of global parameters
            if (gs.generation < ((CalicoGameParameters) gs.getGameParameters()).soloMaxGen) ended = false;
        } else {
            for (CalicoTypes.GlobalParameter p : gs.globalParameters.keySet()) {
                if (p != null && p.countsForEndGame() && !gs.globalParameters.get(p).isMaximum()) ended = false;
            }
        }
//        if (!ended && gs.generation >= 50) ended = true;  // set max generation threshold
        return ended;
    }
}
