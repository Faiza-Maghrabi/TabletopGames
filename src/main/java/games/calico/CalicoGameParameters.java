package games.calico;

import core.AbstractParameters;
import core.CoreConstants;
import core.components.Deck;
import games.calico.CalicoTypes.BoardTypes;
import games.calico.CalicoTypes.DesignGoalTile;
import games.calico.CalicoTypes.TileColour;
import games.calico.CalicoTypes.TilePattern;
import games.calico.components.CalicoBoard;
import games.calico.components.CalicoCatCard;
import games.calico.components.CalicoTile;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CalicoGameParameters extends AbstractParameters {

    int boardSize = 7;

    double fullButtonModifier = 1.5;
    double partButtonModifier = 1.0;
    double catModifier = 1.6;
    double designTokenMultiplier = 0.6;


    //given a colour and a board, set up the board according to the json 
    public CalicoBoard setupBoard(CalicoBoard board, BoardTypes type){
        //design tiles to be used in playerBoard
        DesignGoalTile[] goalTiles = getRandomDesignTile();

        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("data/calico/files/boards.json")) {
            JSONObject data = (JSONObject) jsonParser.parse(reader);
            JSONArray boardArr = (JSONArray) data.get(type.toString());

            int rowCounter = 0;
            int columnCounter = 0;
            int goalCounter = 0;
            //traverse json array to fill up board
            for (Object row : boardArr) {
                columnCounter = 0;
                JSONArray rowArr = (JSONArray) row;
                for (Object column : rowArr) {
                    if (column.toString().equals("DesignGoal")){
                        board.setBoardTileDesign(columnCounter, rowCounter, goalTiles[goalCounter]);
                        goalCounter++;
                    }
                    else {
                        String[] tileVals = column.toString().split(",");
                        if (tileVals[0].equals("Null")){
                            board.setBoardTile(columnCounter, rowCounter);
                        }
                        else {
                            board.setBoardTilePatch(columnCounter, rowCounter, TileColour.valueOf(tileVals[0]), TilePattern.valueOf(tileVals[1]));
                        }
                    }
                    columnCounter++;
                }
                rowCounter++;
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return board;
    }

    public Deck<CalicoTile> loadTiles(){
        Deck<CalicoTile> allTiles = new Deck<CalicoTile>("all tiles", CoreConstants.VisibilityMode.HIDDEN_TO_ALL);
        for (TileColour colour : TileColour.values()) {
            if (colour != TileColour.Null){
                for (TilePattern pattern : TilePattern.values()) {
                    if (pattern != TilePattern.Null) {
                        allTiles.add(new CalicoTile(colour, pattern));
                        allTiles.add(new CalicoTile(colour, pattern));
                        allTiles.add(new CalicoTile(colour, pattern));
                    }
                }
            }
            
        }
        return allTiles;
    }

    //return 3 random DesignGoalTiles to be used
    public DesignGoalTile[] getRandomDesignTile(){
        ArrayList<DesignGoalTile> allGoalTiles = new ArrayList<DesignGoalTile>();
        for (DesignGoalTile g : DesignGoalTile.values()) {
            allGoalTiles.add(g);
        }
        Collections.shuffle(allGoalTiles);
        DesignGoalTile[] returnObj = new DesignGoalTile[3];
        returnObj[0] = allGoalTiles.get(0);
        returnObj[1] = allGoalTiles.get(1);
        returnObj[2] = allGoalTiles.get(2);
        return returnObj;
    }

    public ArrayList<BoardTypes> getShuffledBoardTypes(){
        ArrayList<BoardTypes> allBoardTypes = new ArrayList<BoardTypes>();
        for (BoardTypes b : BoardTypes.values()) {
            allBoardTypes.add(b);
        }
        Collections.shuffle(allBoardTypes);
        return allBoardTypes;
    }

    //load patterns needed for the cat cards
    public ArrayList<TilePattern> loadPatternTiles(){
        ArrayList<TilePattern> patternTiles = new ArrayList<TilePattern>();

        for (TilePattern t : TilePattern.values()) {
            if (TilePattern.Null != t) {
                patternTiles.add(t);
            }
        }
        
        Collections.shuffle(patternTiles);
        return patternTiles;
    }

    //chose 3 cats and assign patterns to them
    public CalicoCatCard[] loadCats(){
        CalicoCatCard[] catsInPlay = new CalicoCatCard[3];
        ArrayList<TilePattern> patternTiles = loadPatternTiles();

        ArrayList<CalicoTypes.Cat> allCats = new ArrayList<CalicoTypes.Cat>();
        for (CalicoTypes.Cat c : CalicoTypes.Cat.values()) {
            allCats.add(c);
        }
        Collections.shuffle(allCats);

        int iterator = 0;
        int tileCounter = 0;
        int addedCats = 0;
        
        //make sure the locations 0, 1, and 2 in catsInPlay are filled with random cats of difficulty 1, 2, and 3 respectively
        while (addedCats < 3) {
            int fetchedDiff = allCats.get(iterator).getDifficulty();
            if (catsInPlay[fetchedDiff-1] == null){
                catsInPlay[fetchedDiff-1] = new CalicoCatCard(allCats.get(iterator), patternTiles.get(tileCounter), patternTiles.get(tileCounter+1));
                tileCounter+= 2;
                addedCats++;
            }
            iterator++;
        }

        return catsInPlay;
    }


    @Override
    protected AbstractParameters _copy() {
        return new CalicoGameParameters();
    }

    @Override
    protected boolean _equals(Object o) {
        return false;
    }

    public int getBoardSize() {
        return boardSize;
    }
}
