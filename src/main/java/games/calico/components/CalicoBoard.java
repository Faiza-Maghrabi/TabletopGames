package games.calico.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import core.components.GridBoard;
import games.calico.CalicoTypes;
import games.calico.CalicoTypes.BoardTypes;
import games.calico.CalicoTypes.TileColour;
import games.calico.CalicoTypes.TilePattern;
import games.calico.types.CalicoPlacementStatus;
import games.calico.types.CalicoLookForCatReturn;
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
        return this.setElement(x, y, boardtile);
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
    //axial evenr conversions for grid calculations col is x and row is y
    //^grid uses oddr but the calculations work with evenr values - could use some optimisation
    public int[] evenr_to_axial(int col, int row) {
        int q = col - (row + (row & 1)) / 2;
        int r = row;  // Adjust for even/odd columns
        return new int[]{q, r};
    }
    
    public int[] axial_to_evenr(int q, int r) {
        int col = q + (r + (r & 1)) / 2;
        int row = r;  // Reverse the adjustment
        return new int[]{col, row};
    }

    public int[] oddr_to_axial(int col, int row) {
        int q = col - (row - (row & 1)) / 2;
        int r = row;  // Adjust for even/odd columns
        return new int[]{q, r};
    }
    
    public int[] axial_to_oddr(int q, int r) {
        int col = q + (r - (r & 1)) / 2;
        int row = r;  // Reverse the adjustment
        return new int[]{col, row};
    }
    

    /*
     * Use results from getNeighbouringTiles to create a hashmap of each colour and pattern occurances
     * Use these hashmaps to create the 2nd hashmap of unique occurances - 2 pairs of 2 etc
     * Compare against the designTile's objective
     * designTokenMultiplier is only passed in properly from evaluateBoard, otherwise it will be 0.0
     */
    public double calculateDesignTokenPoints(int x, int y, double designTokenMultiplier){
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
        //step 2: count occurances - accounting for null colour and null pattern
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
        double score = 0;
        if (colourMatchCount == designGoalOrder.length) score = designGoalTile.getGoalOne();
        if (patternMatchCount == designGoalOrder.length) {
            if (score > 0) {
                if (designTokenMultiplier != 0.0) {
                    return designGoalTile.getGoalTwo() * (2.0 - designTokenMultiplier);
                }
                return designGoalTile.getGoalTwo();
            }
            else {
                if (designTokenMultiplier != 0.0) {
                    score = designGoalTile.getGoalOne() *  (2.0 - designTokenMultiplier);
                }
                else {
                    score = designGoalTile.getGoalOne();
                }
            }
        }

        if (designTokenMultiplier != 0.0){
            score = heuristicDesignTokenPoints(score, designGoalTile, colourMatchCount, patternMatchCount, designTokenMultiplier);
        }
        return score;
    }

    private double heuristicDesignTokenPoints(double score, DesignGoalTile designGoalTile, int colourMatchCount, int patternMatchCount, double designTokenMultiplier) {
        int goalOne = designGoalTile.getGoalOne();
        int goalTwo = designGoalTile.getGoalTwo();
        int designGoalOrderLength = designGoalTile.getOrderArr().length;

        //if one goal was fulfilled
        //multiplier of params.designTokenMultiplier added - prevents ai from believing its the actual score
        if (score > 0.0){
            //System.out.println("Score is NOT 0");
            double goalDifValue = (double) ((goalTwo - goalOne) * designTokenMultiplier);
            if (colourMatchCount == designGoalOrderLength) {
                score += (double)(patternMatchCount/designGoalOrderLength) * goalDifValue;
            }
            else {
                score += (double)(colourMatchCount/designGoalOrderLength) * goalDifValue;
            }
        }   //no goals have been fulfilled
        else {
            score += ((double)patternMatchCount/(double)designGoalOrderLength) * (goalOne * designTokenMultiplier);
            score += ((double)colourMatchCount/(double)designGoalOrderLength) * (goalOne * designTokenMultiplier);
            score = Math.min(goalTwo-1, score); //prevent from going over the limit (goalTwo) and appearing as 'complete'
        }

        //System.out.println("Score for " + designGoalTile.name() + " is: " + score);

        return score;
    }

    //returns the design points for that board at the end of the game
    public int getDesignPoints(int[][] designLoc){
        int totalPoints = 0;
        for (int i = 0; i < designLoc.length; i++){
            // int a = calculateDesignTokenPoints(designLoc[i][0], designLoc[i][1]);
            // totalPoints += a;
            // System.out.println("points for "+ designLoc[i][0] + ", " + designLoc[i][1] + " = " + a);
            totalPoints += (int) calculateDesignTokenPoints(designLoc[i][0], designLoc[i][1], 0.0);
        }
        return totalPoints;
    }

    public String[] getBoardDesignGoalReached(int[] designLoc){
        //get type of design tile and return the goal score that was reached
        DesignGoalTile designGoalTile = getElement(designLoc[0], designLoc[1]).getDesignGoal();
        int points = (int) calculateDesignTokenPoints(designLoc[0], designLoc[1], 0.0);
        if (points == designGoalTile.getGoalOne()) return new String[] {designGoalTile.name(), "1"};
        else if (points == designGoalTile.getGoalTwo()) return new String[] {designGoalTile.name(), "2"};
        return new String[] {designGoalTile.name(), "0"};
    }

    public int lookForButton(int x, int y){
        CalicoBoardTile focusTile = getElement(x, y);
        CalicoBoardTile[] buttonTiles = new CalicoBoardTile[3];
        buttonTiles[0] = focusTile;
        return lookForPatches(focusTile, buttonTiles, 1, new HashSet<Integer>(), focusTile.getTileColour(), null, 3);
    }

    //go through activeCats and check if a cat criteria has been met to add a cat token to the board
    public CalicoLookForCatReturn lookForCat(int x, int y, CalicoCatCard[] activeCats){
        CalicoBoardTile focusTile = getElement(x, y);
        CalicoLookForCatReturn returnVals = new CalicoLookForCatReturn(null, 0, false);
        for (CalicoCatCard catCard : activeCats){
            //System.out.println("Looking for " + catCard.getName());
            for (TilePattern pattern : catCard.getPatches()){
                if (pattern == focusTile.getTilePattern()){    //check if the cat card is for a patch instead of specific shape
                    //System.out.println("Cat is patch ver");
                    if (catCard.getPatchVer()){
                        //System.out.println("looking at pattern: " + pattern.name());
                        int patchSize = catCard.getSize();
                        CalicoBoardTile[] buttonTiles = new CalicoBoardTile[patchSize];
                        buttonTiles[0] = focusTile;
                        int foundSize = lookForPatches(focusTile, buttonTiles, 1, new HashSet<Integer>(), null, pattern, patchSize);
                        //System.out.println("found:"  + found);
                        //System.out.println("found:"  + foundSize + " for " + catCard.getName());
                        //TODO: validArrangement for patch cats?
                        if (foundSize == patchSize) return new CalicoLookForCatReturn(catCard, foundSize, true);
                        if (foundSize > returnVals.getsizeFound()){returnVals.updateParams(catCard, foundSize, true);}
                    }
                    else {  //use the cat arrangements to find matches
                        //System.out.println("looking for arrangement");
                        CalicoPlacementStatus foundArrangementStatus = lookForCatArrangement(focusTile, catCard.getArrangement());
                        int foundSize = foundArrangementStatus.getsizeFound();
                        //System.out.println("found:"  + foundSize + " for " + catCard.getName());
                        if (foundSize == catCard.getSize()) return new CalicoLookForCatReturn(catCard, foundSize, foundArrangementStatus.getvalidPlacement());
                        if (foundSize > returnVals.getsizeFound()){returnVals.updateParams(catCard, foundSize, returnVals.getvalidPlacement() || foundArrangementStatus.getvalidPlacement());}
                    }
                }
            }
        }
        return returnVals;
    }


    private CalicoPlacementStatus lookForCatArrangement(CalicoBoardTile focusTile, int[][][][] fullArrangement){
        int[][][] arrangement = fullArrangement[focusTile.getY() & 1];
        boolean validArrangement = false;
        int maxMatchSize = 0;
        for (int i = 0; i < arrangement.length; i++){
            //System.out.println("arrangement num " + i);
            CalicoPlacementStatus foundArrangementStatus = rotateAndFindArrangement(focusTile, arrangement[i]);
            int foundMatchSize = foundArrangementStatus.getsizeFound();
            validArrangement = validArrangement || foundArrangementStatus.getvalidPlacement();
            if (foundMatchSize == arrangement[i].length){return foundArrangementStatus;}
            if (foundMatchSize > maxMatchSize) {maxMatchSize = foundMatchSize;}
        }
        return new CalicoPlacementStatus(maxMatchSize, validArrangement);
    }

    //given an arrangement, rotate and look for matches
    private CalicoPlacementStatus rotateAndFindArrangement(CalicoBoardTile focusTile, int[][] pattern){ 
        //check initial rotation and then rotate til a match is found or loop ends
        // StringBuilder sp = new StringBuilder();
        //         for (int[] coords : pattern) {
        //             sp.append("[").append(coords[0] + focusTile.getX()).append(",").append(coords[1] + focusTile.getY()).append("] ");
        //         }
        // System.out.println(sp.toString().trim());

        CalicoPlacementStatus firstArrangementStatus = findMatchArrangement(focusTile, pattern);
        int maxMatchSize = firstArrangementStatus.getsizeFound();
        boolean validArrangement = firstArrangementStatus.getvalidPlacement();

        if (maxMatchSize == pattern.length) {return firstArrangementStatus;}
        for (int i = 1; i < 6; i++) {
            // System.out.println("arrangement checking " + i * 60 + " degrees");
            pattern = rotate60(pattern, focusTile.getY() & 1);
            // StringBuilder sb = new StringBuilder();
            // for (int[] coords : pattern) {
            //     sb.append("[").append(coords[0] + focusTile.getX()).append(",").append(coords[1] + focusTile.getY()).append("] ");
            // }
            // System.out.println(sb.toString().trim());
            CalicoPlacementStatus newArrangementCheck = findMatchArrangement(focusTile, pattern);
            validArrangement = validArrangement || newArrangementCheck.getvalidPlacement();
            int newMatchCheck = newArrangementCheck.getsizeFound();

            if (newMatchCheck == pattern.length) {return newArrangementCheck;}
            if (newMatchCheck > maxMatchSize) {maxMatchSize = newMatchCheck;}
        }
        return new CalicoPlacementStatus(maxMatchSize, validArrangement);

    }

    private int[][] rotate60(int[][] pattern, int parity) {
        int[][] rotated = new int[pattern.length][2];
        for (int i = 0; i < pattern.length; i++) {
            //if parity == 1 then use evenr, if == 0 then odd-r
            int[] axial_coords;
            if (parity == 1) axial_coords = evenr_to_axial(pattern[i][0], pattern[i][1]);
            else axial_coords = oddr_to_axial(pattern[i][0], pattern[i][1]);
            int q = axial_coords[0];
            int r = axial_coords[1];
            int [] odd_coords;
            if (parity == 1) odd_coords  = axial_to_evenr(q+r, -q);
            else odd_coords  = axial_to_oddr(q+r, -q);
            rotated[i][0] = odd_coords[0];
            rotated[i][1] = odd_coords[1];
        }
        return rotated;
    }


    /*  look through a given pattern and return if a match is found
     * 
     * returns max size of arrangement found for heuristics
     * the old version returned false when a match wasn't found for a location, less computation
     */
    private CalicoPlacementStatus findMatchArrangement(CalicoBoardTile focusTile, int[][] pattern) {
        //System.out.println("finding matches in pattern!");
        CalicoBoardTile[] arrangeTiles = new CalicoBoardTile[pattern.length];
        int count = 0;
        //used for heuristics - is this arrangement valid for a cat placement according to the board state
        boolean validArrangement = true;
        for (int i = 0; i< pattern.length; i++){
            // System.out.println((focusTile.getX()+ pattern[i][0]) + "," + (focusTile.getY()+ pattern[i][1]));
            // System.out.println((pattern[i][0]) + "," + (pattern[i][1]));
            //no need to check origin tile as that will always be valid
            if (pattern[i][0] == 0 && pattern[i][1] == 0){
                //System.out.println("add base tile");
                arrangeTiles[i] = focusTile;
                count++;
            }
            else {
                CalicoBoardTile iTile = getElement(focusTile.getX() + pattern[i][0], focusTile.getY() + pattern[i][1]);
                if (iTile != null) {
                    if (iTile.getTilePattern() == focusTile.getTilePattern() && !iTile.hasCat()){
                        arrangeTiles[i] = iTile;
                        count++;
                    }
                    else if (!iTile.isEmpty()){ //if there is no cat-less match and the tile is not empty - arrangement is not valid
                        validArrangement = false;
                    }
                };
                // System.out.println(iTile.getTilePattern());
                // System.out.println(iTile.getTileColour());
            }
        }
        //System.out.println("applying cats! MATCH FOUND");
        if (count == pattern.length) {applyCats(arrangeTiles);}
        return new CalicoPlacementStatus(count, validArrangement);
    }


    /*
     * Recursive function to find tiles on a player's board that is applicable for a button
     * Find surrounding tiles, and travel through array to find tiles with the correct colour and are not part of a button group already
     * prevPatch is used in recursive calls to make sure the previous patch was not counted again - not likely to be an issue for buttons
     * due to max of 3 tiles needed.
     * if 3 tiles were found, then apply buttons and add in points
     * 
     * Returns the largest valid patch size found, for use in heuristic calculations, if counter = patchSize then the patch is valid
     */
    private int lookForPatches(CalicoBoardTile searchTile, CalicoBoardTile[] patchTiles, int counter, Set<Integer> visitedPatches, TileColour findColour, TilePattern findPattern, int patchSize){
        // System.out.println("look for function called");
        // System.out.println("colour: " + findColour);
        // System.out.println("pattern: " + findPattern);
        // System.out.println("counter: " + counter);
        CalicoBoardTile[] surroundingTiles = getNeighbouringTiles(searchTile.getX(), searchTile.getY());
        for (int i = 0; i< surroundingTiles.length; i++) {
            if (surroundingTiles[i] != null) {
                //System.out.println(surroundingTiles[i].getTileColour() + "," + surroundingTiles[i].getTilePattern());
                if (findColour != null) {
                    if (surroundingTiles[i].getTileColour() == findColour && !surroundingTiles[i].hasButton() && !visitedPatches.contains(surroundingTiles[i].getComponentID())) {
                        // System.out.println("Match Found");
                        patchTiles[counter] = surroundingTiles[i];
                        counter++;
                        if (counter == patchSize){
                            applyButtons(patchTiles);

                            return counter;
                        }
                        visitedPatches.add(searchTile.getComponentID());
                        //System.out.println(visitedPatches);
                        if (lookForPatches(surroundingTiles[i], patchTiles, counter, visitedPatches, findColour, findPattern, patchSize) == patchSize) return patchSize;
                    }
                }
                else {
                    //System.out.println("looking for a CAT!!");
                    if (surroundingTiles[i].getTilePattern() == findPattern && !surroundingTiles[i].hasCat() && !visitedPatches.contains(surroundingTiles[i].getComponentID())) {
                        //System.out.println("Match Found");
                        patchTiles[counter] = surroundingTiles[i];
                        counter++;
                        if (counter == patchSize){
                            applyCats(patchTiles);
                            return counter;
                        }
                        visitedPatches.add(searchTile.getComponentID());
                        //System.out.println(visitedPatches);
                        if (lookForPatches(surroundingTiles[i], patchTiles, counter, visitedPatches, findColour, findPattern, patchSize) == patchSize) return patchSize;
                    }
                }
                
            }
        }
        //System.out.println("before return counter: " + counter);
        return counter;
    }

    private void applyButtons(CalicoBoardTile[] buttonTiles){
        //System.out.println("applyButtons function");
        for (CalicoBoardTile t : buttonTiles) {
            t.addButton();
        }
        buttonTiles[0].addButtonGUI();
    }

    private void applyCats(CalicoBoardTile[] patternTiles){
        //System.out.println("applyButtons function");
        for (CalicoBoardTile t : patternTiles) {
            t.addCat();
        }
        patternTiles[0].addCatGUI();
    }

    @Override
    public CalicoBoard copy() {
        GridBoard<CalicoBoardTile> board = super.copy();
        CalicoBoard copy = new CalicoBoard(board, this.type);
        copyComponentTo(copy);
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 7; y++) {
                copy.setElement(x, y, this.getElement(x, y).copy());
            }
        }
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
