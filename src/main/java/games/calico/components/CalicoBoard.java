package games.calico.components;

import core.components.GridBoard;
import games.calico.CalicoTypes;

//Exstension of Gridboard to include more functions needed in Calico
public class CalicoBoard extends GridBoard<CalicoBoardTile> {

    //get the locations of the neighbouring tiles
    public int[][] getNeighbouringTiles(int x, int y) {
        // int parity;
        if (x >= 0 && x < getWidth() && y >= 0 && y < getHeight()){
            int parity = y & 1;
            int[][] tileCoordArr = new int[6][2];
            for(int j = 0; j < 6; j++) {
                var direction = CalicoTypes.neighbor_directions[parity][0];
                tileCoordArr[j][0] = x + direction.getX();
                tileCoordArr[j][1] = y + direction.getY();
            }
            return tileCoordArr;
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

}
