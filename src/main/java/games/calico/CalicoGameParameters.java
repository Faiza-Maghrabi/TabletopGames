package games.calico;

import core.AbstractParameters;
import core.CoreConstants;
import core.components.Counter;
import core.components.Deck;
import games.calico.CalicoTypes.Button;
import games.calico.CalicoTypes.TileColour;
import games.calico.CalicoTypes.TilePattern;
import games.calico.components.CalicoCatCard;
import games.calico.components.CalicoTile;
import games.terraformingmars.TMTypes;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// import static games.terraformingmars.TMTypes.Expansion.*;

public class CalicoGameParameters extends AbstractParameters {

    //should this be here?
    public HashMap<TileColour, Button> colourButtonMap = new HashMap<TileColour, Button>() {{
        put(TileColour.DBlue, Button.Blueberry);
        put(TileColour.Green, Button.Leaf);
        put(TileColour.LBlue, Button.Drop);
        put(TileColour.Yellow, Button.Moon);
        put(TileColour.Magenta, Button.Flower);
        put(TileColour.Purple, Button.Mushroom);
    }};

    public HashMap<TileColour, Button> getColourButtonMap() {
        return colourButtonMap;
    }

    int boardSize = 7;

    public Deck<CalicoTile> loadTiles(){
        JSONParser jsonParser = new JSONParser();
        Deck<CalicoTile> allTiles = new Deck<CalicoTile>("all tiles", CoreConstants.VisibilityMode.HIDDEN_TO_ALL);
        try (FileReader reader = new FileReader("data/files/tileCombos.json")) {
            JSONObject data = (JSONObject) jsonParser.parse(reader);
            JSONArray arr = (JSONArray) data.get("tiles");
            for (Object a : arr) {
                String[] tileVals = a.toString().split(",");
                //add tile in 3 times
                allTiles.add(new CalicoTile(TileColour.valueOf(tileVals[0]), TilePattern.valueOf(tileVals[1])));
                allTiles.add(new CalicoTile(TileColour.valueOf(tileVals[0]), TilePattern.valueOf(tileVals[1])));
                allTiles.add(new CalicoTile(TileColour.valueOf(tileVals[0]), TilePattern.valueOf(tileVals[1])));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return allTiles;
    }

    //load patterns needed for the cat cards
    public ArrayList<TilePattern> loadPatternTiles(){
        ArrayList<TilePattern> patternTiles = new ArrayList<TilePattern>();

        for (TilePattern t : TilePattern.values()) {
            patternTiles.add(t);
        }
        
        Collections.shuffle(patternTiles);
        return patternTiles;
    }

    //chose 3 cats and assign patterns to them
    public ArrayList<CalicoCatCard> loadCats(){
        ArrayList<CalicoCatCard> catsInPlay = new ArrayList<CalicoCatCard>();
        ArrayList<TilePattern> patternTiles = loadPatternTiles();

        ArrayList<CalicoTypes.Cat> allCats = new ArrayList<CalicoTypes.Cat>();
        for (CalicoTypes.Cat c : CalicoTypes.Cat.values()) {
            allCats.add(c);
        }
        Collections.shuffle(allCats);

        int tileCounter = 0;

        for (int i = 0; i < 3; i++){
            catsInPlay.add(new CalicoCatCard(allCats.get(i), patternTiles.get(tileCounter), patternTiles.get(tileCounter)));
            tileCounter+= 2;
        }

        return catsInPlay;
    }

    //TM
    HashSet<CalicoTypes.Expansion> expansions = new HashSet<CalicoTypes.Expansion>() {{ add(CorporateEra); }};  // Elysium, Hellas and Venus compiling, but not fully parsed yet
    int soloTR = 14;
    int soloMaxGen = 14;
    int soloCities = 2;

    HashMap<CalicoTypes.Resource, Integer> minimumProduction = new HashMap<CalicoTypes.Resource, Integer>() {{
        for (CalicoTypes.Resource res: CalicoTypes.Resource.values()) {
            if (res == CalicoTypes.Resource.MegaCredit) put(res, -5);
            else put(res, 0);
        }
    }};
    HashMap<CalicoTypes.Resource, Integer> startingResources = new HashMap<CalicoTypes.Resource, Integer>() {{
        for (CalicoTypes.Resource res: CalicoTypes.Resource.values()) {
            put(res, 0);
        }
        put(CalicoTypes.Resource.TR, 20);
//        put(TMTypes.Resource.MegaCredit, 500);  // TODO Test
    }};
    HashMap<CalicoTypes.Resource, Integer> startingProduction = new HashMap<CalicoTypes.Resource, Integer>() {{
        for (CalicoTypes.Resource res: CalicoTypes.Resource.values()) {
            if (res.isPlayerBoardRes()) {
                put(res, 1);
            }
        }
    }};
    int maxPoints = 500;
    int maxCards = 250;  // TODO based on expansions

    int projectPurchaseCost = 3;
    int nCorpChoiceStart = 2;
    int nProjectsStart = 10;
    int nProjectsResearch = 4;
    int nActionsPerPlayer = 2;
    int nMCGainedOcean = 2;

    // steel and titanium to MC rate
    double nSteelMC = 2;
    double nTitaniumMC = 3;

    // standard projects
    int nGainCardDiscard = 1;
    int nCostSPEnergy = 11;
    int nCostSPTemp = 14;
    int nCostSPOcean = 18;
    int nCostSPGreenery = 23;
    int nCostSPCity = 25;
    int nSPCityMCGain = 1;
    int nCostVenus = 15;

    // Resource actions
    int nCostGreeneryPlant = 8;
    int nCostTempHeat = 8;

    // Milestones, awards
    int[] nCostMilestone = new int[] {8, 8, 8};
    int[] nCostAwards = new int[] {8, 14, 20};
    int nPointsMilestone = 5;
    int nPointsAwardFirst = 5;
    int nPointsAwardSecond = 2;

    @Override
    protected AbstractParameters _copy() {
        return new CalicoGameParameters();
    }

    @Override
    protected boolean _equals(Object o) {
        return false;
    }

    public HashMap<CalicoTypes.Resource, Integer> getMinimumProduction() {
        return minimumProduction;
    }

    public HashMap<CalicoTypes.Resource, Integer> getStartingProduction() {
        return startingProduction;
    }

    public HashMap<CalicoTypes.Resource, Integer> getStartingResources() {
        return startingResources;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public int getnCorpChoiceStart() {
        return nCorpChoiceStart;
    }

    public int getProjectPurchaseCost() {
        return projectPurchaseCost;
    }

    public int getnProjectsResearch() {
        return nProjectsResearch;
    }

    public int getnProjectsStart() {
        return nProjectsStart;
    }

    public HashSet<CalicoTypes.Expansion> getExpansions() {
        return expansions;
    }

    public int getnMCGainedOcean() {
        return nMCGainedOcean;
    }

    public int[] getnCostMilestone() {
        return nCostMilestone;
    }

    public int[] getnCostAwards() {
        return nCostAwards;
    }

    public double getnSteelMC() {
        return nSteelMC;
    }

    public double getnTitaniumMC() {
        return nTitaniumMC;
    }

    public int getnActionsPerPlayer() {
        return nActionsPerPlayer;
    }

    public int getnCostGreeneryPlant() {
        return nCostGreeneryPlant;
    }

    public int getnCostSPCity() {
        return nCostSPCity;
    }

    public int getnCostSPEnergy() {
        return nCostSPEnergy;
    }

    public int getnCostSPGreenery() {
        return nCostSPGreenery;
    }

    public int getnCostSPOcean() {
        return nCostSPOcean;
    }

    public int getnCostSPTemp() {
        return nCostSPTemp;
    }

    public int getnCostTempHeat() {
        return nCostTempHeat;
    }

    public int getnGainCardDiscard() {
        return nGainCardDiscard;
    }

    public int getnPointsAwardFirst() {
        return nPointsAwardFirst;
    }

    public int getnPointsAwardSecond() {
        return nPointsAwardSecond;
    }

    public int getnPointsMilestone() {
        return nPointsMilestone;
    }

    public int getnSPCityMCGain() {
        return nSPCityMCGain;
    }

    public int getSoloCities() {
        return soloCities;
    }

    public int getSoloMaxGen() {
        return soloMaxGen;
    }

    public int getSoloTR() {
        return soloTR;
    }
}
