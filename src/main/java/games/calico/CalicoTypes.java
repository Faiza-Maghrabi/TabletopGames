package games.calico;

import core.components.Counter;
import core.components.Deck;
import core.components.GridBoard;
import games.calico.components.CalicoBoardTile;
import games.terraformingmars.TMGameParameters;
import games.terraformingmars.actions.TMAction;
import games.terraformingmars.components.Award;
import games.terraformingmars.components.Milestone;
import games.terraformingmars.components.TMCard;
import games.terraformingmars.rules.effects.Bonus;

// import games.terraformingmars.actions.TMAction;
// import games.terraformingmars.components.*;
// import games.terraformingmars.rules.effects.Bonus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utilities.Utils;
import utilities.Vector2D;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;

// import static games.terraformingmars.components.TMMapTile.parseMapTile;

public class CalicoTypes {

    // Odd r: (odd rows offset to the right)
    public static Vector2D[][] neighbor_directions = new Vector2D[][] {
        {
            new Vector2D(1, 0),
            new Vector2D(0, -1),
            new Vector2D(-1, -1),
            new Vector2D(-1, 0),
            new Vector2D(-1, 1),
            new Vector2D(0, 1)
        },
        {
            new Vector2D(1, 0),
            new Vector2D(1, -1),
            new Vector2D(0, -1),
            new Vector2D(-1, 0),
            new Vector2D(0, 1),
            new Vector2D(1, 1)
        }
    };

    // Enums

    //update with appropriate actions when made
    public enum ActionType {
        PlayCard,
        StandardProject,
        ClaimMilestone,
        FundAward,
        ActiveAction,
        BasicResourceAction,
        BuyProject  // TODO ignore in GUI
    }

    public enum  DesignGoalTile{
        AAA_BBB ("data/calico/images/tiles/aaa_bbb.png"),
        AA_BB_CC ("data/calico/images/tiles/aa_bb_cc.png"),
        NOT ("data/calico/images/tiles/not.png"),
        AAAA_BB ("data/calico/images/tiles/aaaa_bb.png"),
        AA_BB_C_D ("data/calico/images/tiles/aa_bb_c_d.png");

        String imagePath;

        DesignGoalTile(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

    public enum TileColour{
        DBlue,
        Green,
        LBlue,
        Yellow,
        Magenta,
        Purple,
        Null
    }

    public enum TilePattern{
        Flowers ("data/calico/images/patches/flowers.png"),
        Dots ("data/calico/images/patches/dots.png"),
        Vines ("data/calico/images/patches/vines.png"),
        Stripes ("data/calico/images/patches/stripes.png"),
        Quatrefoil ("data/calico/images/patches/quatrefoil.png"),
        Ferns ("data/calico/images/patches/ferns.png"),
        Null ("null"); //no patch tile for null

        String imagePath;

        TilePattern(String imagePath){
            this.imagePath = imagePath;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

    public enum Button{
        Blueberry,
        Drop,
        Leaf,
        Flower,
        Moon,
        Mushroom,
        Rainbow
    }

    public enum Cat{
        Millie ("data/calico/images/cats/millie.png", 3, 3),
        Tibbit ("data/calico/images/cats/tibbit.png", 5, 4),
        Coconut ("data/calico/images/cats/coconut.png", 7, 5),
        Tecolote ("data/calico/images/cats/tecolote.png", 7, 4), //in a straightline
        Callie ("data/calico/images/cats/callie.png", 3, 3), //in a T shape
        Rumi ("data/calico/images/cats/rumi.png", 5, 3), //in a stright line
        Gwen ("data/calico/images/cats/gwen.png", 11, 7),
        Cira ("data/calico/images/cats/cira.png", 9, 6),
        Leo ("data/calico/images/cats/leo.png", 11, 5), //in a stright line
        Almond ("data/calico/images/cats/almond.png", 9, 5); //pyramid

        String imagePath;
        int points;
        int arrangement; //TODO: figure out how the more unique arrangements are going to work

        Cat(String imagePath, int points, int arrangement) {
            this.imagePath = imagePath;
            this.points = points;
            this.arrangement = arrangement;
        }

        public String getImagePath() {
            return imagePath;
        }

        public String getName() {
            return this.toString();
        }

        public int getPoints(){
            return points;
        }

        public int getArrangement() {
            return arrangement;
        }
    }


    public enum Resource {
        MegaCredit("data/terraformingmars/images/megacredits/megacredit.png", true, false),
        Steel("data/terraformingmars/images/resources/steel.png", true, false),
        Titanium("data/terraformingmars/images/resources/titanium.png", true, false),
        Plant("data/terraformingmars/images/resources/plant.png", true, false),
        Energy("data/terraformingmars/images/resources/power.png", true, false),
        Heat("data/terraformingmars/images/resources/heat.png", true, false),
        Card("data/terraformingmars/images/resources/card.png", false, false),
        TR("data/terraformingmars/images/resources/TR.png", false, false),
        Microbe("data/terraformingmars/images/resources/microbe.png", false, true),
        Animal("data/terraformingmars/images/resources/animal.png", false, true),
        Science("data/terraformingmars/images/resources/science.png", false, true),
        Fighter("data/terraformingmars/images/resources/fighter.png", false, true),
        Floater("data/terraformingmars/images/resources/floater.png", false, true);

        String imagePath;
        boolean playerBoardRes;
        boolean canGoOnCard;
        static int nPlayerBoardRes = -1;

        Resource(String imagePath, boolean playerBoardRes, boolean canGoOnCard) {
            this.imagePath = imagePath;
            this.playerBoardRes = playerBoardRes;
        }

        public String getImagePath() {
            return imagePath;
        }

        public boolean isPlayerBoardRes() {
            return playerBoardRes;
        }

        public static int nPlayerBoardRes() {
            if (nPlayerBoardRes == -1) {
                nPlayerBoardRes = 0;
                for (Resource res : values()) {
                    if (res.isPlayerBoardRes()) nPlayerBoardRes++;
                }
            }
            return nPlayerBoardRes;
        }

        public boolean canGoOnCard() {
            return canGoOnCard;
        }

        public static Resource[] getPlayerBoardResources() {
            ArrayList<Resource> res = new ArrayList<>();
            for (Resource r: values()) {
                if (r.isPlayerBoardRes()) res.add(r);
            }
            return res.toArray(new Resource[0]);
        }
    }

    public enum Tag {
        Plant("data/terraformingmars/images/tags/plant.png"),
        Microbe("data/terraformingmars/images/tags/microbe.png"),
        Animal("data/terraformingmars/images/tags/animal.png"),
        Science("data/terraformingmars/images/tags/science.png"),
        Earth("data/terraformingmars/images/tags/earth.png"),
        Space("data/terraformingmars/images/tags/space.png"),
        Event("data/terraformingmars/images/tags/event.png"),
        Building("data/terraformingmars/images/tags/building.png"),
        Power("data/terraformingmars/images/tags/power.png"),
        Jovian("data/terraformingmars/images/tags/jovian.png"),
        City("data/terraformingmars/images/tags/city.png"),
        Venus("data/terraformingmars/images/tags/venus.png"),
        Wild("data/terraformingmars/images/tags/wild.png");

        String imagePath;

        Tag(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

    public enum CardType {
        Automated("data/terraformingmars/images/cards/card-automated.png", true, Color.green),
        Active("data/terraformingmars/images/cards/card-active.png", true, Color.cyan),
        Event("data/terraformingmars/images/cards/card-event.png", true, Color.orange),
        Corporation("data/terraformingmars/images/cards/corp-card-bg.png", false, Color.gray),
        Prelude("data/terraformingmars/images/cards/proj-card-bg.png", false, Color.pink),
        Colony("data/terraformingmars/images/cards/proj-card-bg.png", false, Color.lightGray),
        GlobalEvent("data/terraformingmars/images/cards/proj-card-bg.png", false, Color.blue);

        String imagePath;
        Color color;
        boolean isPlayableStandard;

        CardType(String imagePath, boolean isPlayableStandard, Color color) {
            this.imagePath = imagePath;
            this.isPlayableStandard = isPlayableStandard;
            this.color = color;
        }

        public String getImagePath() {
            return imagePath;
        }

        public boolean isPlayableStandard() {
            return isPlayableStandard;
        }

        public Color getColor() {
            return color;
        }
    }

    public enum GlobalParameter {
        Oxygen ("data/terraformingmars/images/global-parameters/oxygen.png", Color.lightGray, true, "O2"),
        Temperature ("data/terraformingmars/images/global-parameters/temperature.png", Color.white, true, "°C"),
        OceanTiles ("data/terraformingmars/images/tiles/ocean.png", Color.yellow, true, "Ocean"),
        Venus("data/terraformingmars/images/global-parameters/venus.png", Color.blue, false, "Venus");

        String imagePath;
        String shortString;
        Color color;
        boolean countsForEndGame;

        GlobalParameter(String imagePath, Color color, boolean countsForEndGame, String shortString) {
            this.imagePath = imagePath;
            this.color = color;
            this.countsForEndGame = countsForEndGame;
            this.shortString = shortString;
        }

        public String getImagePath() {
            return imagePath;
        }

        public Color getColor() {
            return color;
        }

        public boolean countsForEndGame() {
            return countsForEndGame;
        }

        public String getShortString() {
            return shortString;
        }

        public static ArrayList<GlobalParameter> getDrawOrder(TMGameParameters params) {
            ArrayList<GlobalParameter> order = new ArrayList<>();
            if (params.expansions.contains(Expansion.Venus)) {
                order.add(Venus);
            }
            order.add(Temperature);
            order.add(Oxygen);
            order.add(OceanTiles);
            return order;
        }
    }

    public enum Expansion {
        Base,
        CorporateEra,
        Prelude,
        Venus,
        Turmoil,
        Colonies,
        Promo,
        Hellas,
        Elysium;

        public String getBoardPath() {
            return "data/terraformingmars/boards/" + this.name().toLowerCase() + ".json";
        }
        public String getCorpCardsPath() {
            return "data/terraformingmars/corporationCards/" + this.name().toLowerCase() + ".json";
        }
        public String getProjectCardsPath() {
            return "data/terraformingmars/projectCards/" + this.name().toLowerCase() + ".json";
        }
        public String getOtherCardsPath() {
            return "data/terraformingmars/otherCards/" + this.name().toLowerCase() + ".json";
        }

        /* custom loading info from json */

        public void loadBoard(GridBoard<CalicoMapTile> board, HashSet<CalicoMapTile> extraTiles, HashSet<Bonus> bonuses,
                              HashSet<Milestone> milestones, HashSet<Award> awards, HashMap<GlobalParameter, games.terraformingmars.components.GlobalParameter> globalParameters) {
            JSONParser jsonParser = new JSONParser();
            try (FileReader reader = new FileReader(getBoardPath())) {
                JSONObject data = (JSONObject) jsonParser.parse(reader);

                // Process main map
                if (data.get("board") != null) {
                    JSONArray b = (JSONArray) data.get("board");
                    int y = 0;
                    for (Object g : b) {
                        JSONArray row = (JSONArray) g;
                        int x = 0;
                        for (Object o1 : row) {
                            board.setElement(x, y, parseMapTile((String) o1, x, y));
                            x++;
                        }
                        y++;
                    }
                }

                // Process extra tiles not on regular board
                if (data.get("extra") != null) {
                    JSONArray extra = (JSONArray) data.get("extra");
                    for (Object o : extra) {
                        extraTiles.add(parseMapTile((String) o));
                    }
                }

                // Process milestones and awards
                if (data.get("milestones") != null) {
                    JSONArray milestonesStr = (JSONArray) data.get("milestones");
                    for (Object o : milestonesStr) {
                        String[] split = ((String) o).split(":");
                        milestones.add(new Milestone(split[0], Integer.parseInt(split[2]), split[1]));
                    }
                }
                if (data.get("awards") != null) {
                    JSONArray awardsStr = (JSONArray) data.get("awards");
                    for (Object o : awardsStr) {
                        String[] split = ((String) o).split(":");
                        awards.add(new Award(split[0], split[1]));
                    }
                }

                // Process global parameters enabled
                if (data.get("globalParameters") != null) {
                    JSONArray gps = (JSONArray) data.get("globalParameters");
                    for (Object o : gps) {
                        JSONObject gp = (JSONObject) o;
                        GlobalParameter p = GlobalParameter.valueOf((String) gp.get("name"));
                        JSONArray valuesJSON = (JSONArray) gp.get("range");
                        int[] values = new int[valuesJSON.size()];
                        for (int i = 0; i < valuesJSON.size(); i++) {
                            values[i] = (int)(long)valuesJSON.get(i);
                        }
                        globalParameters.put(p, new games.terraformingmars.components.GlobalParameter(values, p.name()));

                        // Process bonuses for this game when counters reach specific points
                        if (gp.get("bonus") != null) {
                            JSONArray bonus = (JSONArray) gp.get("bonus");
                            for (Object o2 : bonus) {
                                JSONObject b = (JSONObject) o2;
                                String effectString = (String) b.get("effect");
                                int threshold = (int)(long) b.get("threshold");
                                bonuses.add(new Bonus(p, threshold, TMAction.parseActionOnCard(effectString, null, true)));
                            }
                        }
                    }
                }
            } catch (IOException ignored) {
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public void loadProjectCards(Deck<TMCard> deck) {
            loadCards(deck, getProjectCardsPath());
        }

        public void loadCorpCards(Deck<TMCard> deck) {
            loadCards(deck, getCorpCardsPath());
        }

        private void loadCards(Deck<TMCard> deck, String path) {
            JSONParser jsonParser = new JSONParser();
            try (FileReader reader = new FileReader(path)) {
                JSONArray data = (JSONArray) jsonParser.parse(reader);
                for (Object o: data) {
                    TMCard card;
                    if (deck.getComponentName().equalsIgnoreCase("corporations")) {
                        card = TMCard.loadCorporation((JSONObject)o);
                    } else {
//                        card = TMCard.loadCardHTML((JSONObject) o);
                        card = TMCard.loadCardJSON((JSONObject) o);
                    }
                    deck.add(card);
                }
            } catch (IOException ignored) {
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

}
