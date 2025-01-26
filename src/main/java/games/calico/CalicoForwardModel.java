package games.calico;

import core.AbstractGameState;
import core.CoreConstants;
import core.StandardForwardModel;
import core.actions.AbstractAction;
import core.components.Counter;
import core.components.Deck;
import core.components.GridBoard;

import games.calico.CalicoTypes.BoardTypes;
import games.calico.CalicoTypes.Button;
import games.calico.CalicoTypes.Cat;
import games.calico.CalicoTypes.DesignGoalTile;
import games.calico.components.CalicoBoard;
import games.calico.components.CalicoTile;

import utilities.Vector2D;

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

        ArrayList<BoardTypes> allBoardTypes = params.getShuffledBoardTypes();

        for (int i = 0; i< gs.getNPlayers(); i++){

            gs.playerBoards[i] = new CalicoBoard(params.boardSize, allBoardTypes.get(i));
            gs.playerBoards[i] = params.setupBoard(gs.playerBoards[i], allBoardTypes.get(i));

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
