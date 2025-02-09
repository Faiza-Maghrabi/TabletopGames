package games.calico.components;

import core.components.GridBoard;
import games.calico.CalicoTypes;
import games.calico.CalicoTypes.BoardTypes;
import games.calico.CalicoTypes.TileColour;
import games.calico.CalicoTypes.TilePattern;

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
        //setting a tile with a known tile
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
                var direction = CalicoTypes.neighbor_directions[parity][0];
                tileArr[j] = this.getElement(direction);
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

    private int calculateDesignTokenPoints(int x, int y){

    }

    //returns the design points for that board at the end of the game
    public int getDesignPoints(int[][] designLoc){
        for (int i = 0; i < designLoc.length; i++){

        }
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
