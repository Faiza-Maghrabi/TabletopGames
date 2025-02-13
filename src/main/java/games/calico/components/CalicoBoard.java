package games.calico.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import core.components.GridBoard;
import games.calico.CalicoTypes;
import games.calico.CalicoTypes.BoardTypes;
import games.calico.CalicoTypes.TileColour;
import games.calico.CalicoTypes.TilePattern;
import games.calico.CalicoTypes.DesignGoalTile;

//Exstension of Gridboard to include more functions needed in Calico
public class CalicoBoard extends GridBoard<CalicoBoardTile> {

    BoardTypes type;

    public CalicoBoard(int sideLength, BoardTypes type){
        super(sideLength, sideLength);
        this.type = type;
    }

    public CalicoBoard(GridBoard<CalicoBoardTile> grid, BoardTypes type){
        super(grid);
        this.type = type;
    }

    //setting an empty tile
    public boolean setBoardTile(int x, int y){
        CalicoBoardTile tile = new CalicoBoardTile(x, y);
        return this.setElement(x, y, tile);
    }
    //setting a tile with a known colour and pattern
    public boolean setBoardTilePatch(int x, int y, TileColour colour, TilePattern pattern){
        CalicoBoardTile tile = new CalicoBoardTile(x, y, colour, pattern);
        return this.setElement(x, y, tile);
    }
    //setting a tile with a known tile and look for buttons
    public boolean setBoardTilePatch(int x, int y, CalicoTile tile){
        CalicoBoardTile boardtile = new CalicoBoardTile(x, y, tile.getColour(), tile.getPattern());
        CalicoBoardTile[] buttonTiles = new CalicoBoardTile[3];
        buttonTiles[0] = boardtile;
        boolean buttonPlaced = lookForButton(boardtile, buttonTiles, 1, new HashSet<Integer>());
        this.setElement(x, y, boardtile);
        return buttonPlaced;
    }

    //setting a tile with a design goal
    public boolean setBoardTileDesign(int x, int y, CalicoTypes.DesignGoalTile designGoalTile){
        CalicoBoardTile tile = new CalicoBoardTile(x, y, designGoalTile);
        return this.setElement(x, y, tile);
    }

    public String getEmptyImagePath() {
        return type.getImagePath();
    }

    //get the locations of the neighbouring tiles
    public CalicoBoardTile[] getNeighbouringTiles(int x, int y) {
        // int parity;
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()){
            int parity = y & 1;
            CalicoBoardTile[] tileArr = new CalicoBoardTile[6];
            for(int j = 0; j < 6; j++) {
                var direction = CalicoTypes.neighbor_directions[parity][j];
                //System.out.println(new Vector2D(direction.getX() + x, direction.getY() + y));
                tileArr[j] = this.getElement(direction.getX() + x, direction.getY() + y);
            }
            return tileArr;
        }
        return null;
    }
    //axial oddr conversions for grid calculations col is x and row is y
    public int[] oddr_to_axial(int col, int row){
        var q = col - (row - (row&1)) / 2;
        var r = row;
        int[] returnArr = {q, r};
        return returnArr;
    }

    public int[] axial_to_oddr(int q, int r) {
        int col = q + (r - (r&1)) / 2;
        int row = r;
        int[] returnArr = {col, row};
        return returnArr;
    }

    /*
     * Use results from getNeighbouringTiles to create a hashmap of each colour and pattern occurances
     * Use these hashmaps to create the 2nd hashmap of unique occurances - 2 pairs of 2 etc
     * Compare against the designTile's objective
     */
    private int calculateDesignTokenPoints(int x, int y){
        DesignGoalTile designGoalTile = getElement(x, y).getDesignGoal();
        if (designGoalTile == null) return 0;

        CalicoBoardTile[] surroundingTiles = getNeighbouringTiles(x, y);
            // System.out.println("surroundingTiles for "+ x + ", " + y + " = " + surroundingTiles);
        HashMap<TileColour, Integer> colourMap = new HashMap<TileColour, Integer>();
        HashMap<TilePattern, Integer> patternMap = new HashMap<TilePattern, Integer>();
        //step 1: find the number of times a tile attribute appears
        //System.out.println("----------------------------------------");
        //System.out.println(designGoalTile);
        for (CalicoBoardTile tile : surroundingTiles) {
            //System.out.println(" "+ tile.getTileColour() + ", " + tile.getTilePattern());
            TileColour tileColour = tile.getTileColour();
            TilePattern tilePattern = tile.getTilePattern();
            int val = colourMap.containsKey(tileColour) ? colourMap.get(tileColour) : 0;
            colourMap.put(tileColour, val + 1);
            val = patternMap.containsKey(tilePattern) ? patternMap.get(tilePattern) : 0;
            patternMap.put(tilePattern, val + 1);
        }
        //step 2: count occurances - accounting for null colour and null pattern - eventhough everything should be filled by this stage anyway
        HashMap<Integer, Integer> colourOccuranceMap = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> patternOccuranceMap = new HashMap<Integer, Integer>();
        for (TileColour c: TileColour.values()){
            if (c != TileColour.Null){
                Integer occurances = colourMap.get(c);
                if (occurances != null) {
                    int val = colourOccuranceMap.containsKey(occurances) ? colourOccuranceMap.get(occurances) : 0;
                    colourOccuranceMap.put(occurances, val +1);
                }
            }
        }
        for (TilePattern p: TilePattern.values()){
            if (p != TilePattern.Null){
                Integer occurances = patternMap.get(p);
                if (occurances != null) {
                    int val = patternOccuranceMap.containsKey(occurances) ? patternOccuranceMap.get(occurances) : 0;
                    patternOccuranceMap.put(occurances, val +1);
                }
            }
        }
        //step 3: make sure it supports the designTile objectives
        int colourMatchCount = 0;
        int patternMatchCount = 0;
        int[] designGoalOrder = designGoalTile.getOrderArr();
        for (int i = 0; i< designGoalOrder.length; i++) {
            int colourCount = colourOccuranceMap.containsKey(designGoalOrder[i]) ? colourOccuranceMap.get(designGoalOrder[i]) : 0;
            int patternCount = patternOccuranceMap.containsKey(designGoalOrder[i]) ? patternOccuranceMap.get(designGoalOrder[i]) : 0;
            if (colourCount > 0) colourMatchCount++; colourOccuranceMap.put(designGoalOrder[i], colourCount -= 1);
            if (patternCount > 0) patternMatchCount++; patternOccuranceMap.put(designGoalOrder[i], patternCount -= 1);
        }
        
        // System.out.println("colourMatchCount: "+ colourMatchCount);
        // System.out.println("patternMatchCount: "+ patternMatchCount);
        // System.out.println("orderLength : "+ designGoalOrder.length);
        int score = 0;
        if (colourMatchCount == designGoalOrder.length) score = designGoalTile.getGoalOne();
        if (patternMatchCount == designGoalOrder.length) {
            if (score > 0) {return designGoalTile.getGoalTwo();}
            else {
                score = designGoalTile.getGoalOne();
            }
        }
        return score;
    }

    //returns the design points for that board at the end of the game
    public int getDesignPoints(int[][] designLoc){
        int totalPoints = 0;
        for (int i = 0; i < designLoc.length; i++){
            // int a = calculateDesignTokenPoints(designLoc[i][0], designLoc[i][1]);
            // totalPoints += a;
            // System.out.println("points for "+ designLoc[i][0] + ", " + designLoc[i][1] + " = " + a);
            totalPoints += calculateDesignTokenPoints(designLoc[i][0], designLoc[i][1]);
        }
        return totalPoints;
    }


    /*
     * Recursive function to find tiles on a player's board that is applicable for a button
     * Find surrounding tiles, and travel through array to find tiles with the correct colour and are not part of a button group already
     * prevPatch is used in recursive calls to make sure the previous patch was not counted again - not likely to be an issue for buttons
     * due to max of 3 tiles needed.
     * if 3 tiles were found, then apply buttons and add in points
     */
    public boolean lookForButton(CalicoBoardTile searchTile, CalicoBoardTile[] buttonTiles, int counter, Set<Integer> visitedPatches){
        //System.out.println("look for Button function called");
        CalicoBoardTile[] surroundingTiles = getNeighbouringTiles(searchTile.getX(), searchTile.getY());
        for (int i = 0; i< surroundingTiles.length; i++) {
            if (surroundingTiles[i] != null) {
                //System.out.println(surroundingTiles[i].getTileColour() + "," + surroundingTiles[i].getTilePattern());
                if (surroundingTiles[i].getTileColour() == searchTile.getTileColour() && !surroundingTiles[i].getHasButton() && !visitedPatches.contains(surroundingTiles[i].getComponentID())) {
                    //System.out.println("Match Found");
                    buttonTiles[counter] = surroundingTiles[i];
                    counter++;
                    if (counter == 3){
                        applyButtons(buttonTiles);
                        return true;
                    }
                    visitedPatches.add(searchTile.getComponentID());
                    //System.out.println(visitedPatches);
                    if (lookForButton(surroundingTiles[i], buttonTiles, counter, visitedPatches)) return true;
                }   //TODO test the result part a bit more
            }
        }
        return false;
    }

    public void applyButtons(CalicoBoardTile[] buttonTiles){
        //System.out.println("applyButtons function");
        for (CalicoBoardTile t : buttonTiles) {
            t.addButton();
        }
        buttonTiles[0].addButtonGUI();
    }

    @Override
    public CalicoBoard copy() {
        GridBoard<CalicoBoardTile> board = super.copy();
        CalicoBoard copy = new CalicoBoard(board, this.type);
        copyComponentTo(copy);
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CalicoBoard) {
            if (super.equals(o)){
                CalicoBoard other = (CalicoBoard) o;
                return type == other.type;
            }
        }
        return false;
    }

    //hash is final in GridBoard

}
