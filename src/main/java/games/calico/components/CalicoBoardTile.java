package games.calico.components;

import core.components.Component;
import games.calico.CalicoTypes;
import games.calico.CalicoTypes.TileColour;
import games.calico.CalicoTypes.TilePattern;

import java.util.Objects;

import static core.CoreConstants.ComponentType.BOARD_NODE;

//Odd-r grid for GridBoard
public class CalicoBoardTile extends Component {
    int x, y;
    CalicoTile tilePlaced;
    boolean isEmpty = true;

    boolean isDesignTile = false;
    CalicoTypes.DesignGoalTile designGoalTile;

    boolean hasButton = false;
    boolean hasCat = false;

    public CalicoBoardTile(int x, int y) {
        super(BOARD_NODE, "Tile");
        this.x = x;
        this.y = y;
        this.tilePlaced = new CalicoTile(TileColour.Null, TilePattern.Null);
    }

    public CalicoBoardTile(int x, int y, CalicoTypes.DesignGoalTile designGoalTile) {
        super(BOARD_NODE, "Tile");
        this.x = x;
        this.y = y;
        this.designGoalTile = designGoalTile;
        this.isDesignTile = true;
        this.isEmpty = false;
    }

    public CalicoBoardTile(int x, int y, TileColour colour, TilePattern pattern) {
        super(BOARD_NODE, "Tile");
        this.x = x;
        this.y = y;
        this.tilePlaced = new CalicoTile(colour, pattern);
        this.isEmpty = false;
    }

    // protected CalicoMapTile(int x, int y, int componentID) {
    //     super(BOARD_NODE, "Tile", componentID);
    //     this.x = x;
    //     this.y = y;
    // }

    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean isDesignTile() {
        return isDesignTile;
    }

    public boolean hasButton() {
        return hasButton;
    }

    public boolean hasCat() {
        return hasCat;
    }

    public void addTile(TileColour colour, TilePattern pattern) {
        if (!isDesignTile && isEmpty) {
            this.tilePlaced = new CalicoTile(colour, pattern);
            this.isEmpty = false;
        }
    }

    public void addButton(){
        this.hasButton = true;
    }

    public void addCat(){
        this.hasCat = true;
    }

    public TileColour getTileColour() {
        if (!isDesignTile && !isEmpty) {
            return tilePlaced.getColour();
        }
        return null;
    }

    public TilePattern getTilePattern() {
        if (!isDesignTile && !isEmpty) {
            return tilePlaced.getPattern();
        }
        return null;
    }

    public CalicoTypes.DesignGoalTile getDesignGoal() {
        if (isDesignTile) {
            return designGoalTile;
        }
        return null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public CalicoBoardTile copy() {
        CalicoBoardTile copy = new CalicoBoardTile(x, y);
        copyComponentTo(copy);
        copy.tilePlaced = tilePlaced;
        copy.isEmpty = isEmpty;
        copy.isDesignTile = isDesignTile;
        copy.designGoalTile = designGoalTile;
        return copy;
    }

    /*
     * Not sure what this is used for yet
     */

    // public static CalicoBoardTile parseMapTile(String s) {
    //     return parseMapTile(s, -1, -1);
    // }

    // public static CalicoBoardTile parseMapTile(String s, int x, int y) {
    //     if (s.equals("0")) return null;

    //     CalicoBoardTile mt = new CalicoBoardTile(x, y);

    //     String[] split = s.split(":");

    //     // First element is tile type
    //     TMTypes.MapTileType type = Utils.searchEnum(TMTypes.MapTileType.class, split[0]);
    //     if (type == null) {
    //         type = TMTypes.MapTileType.City;
    //         mt.setComponentName(split[0]); // Keep city name
    //     } else if (type == TMTypes.MapTileType.Volcanic) {
    //         type = TMTypes.MapTileType.Ground;
    //         mt.setVolcanic(true);
    //     }
    //     mt.setType(type);

    //     // The rest are resources existing here
    //     int nResources = split.length-1;
    //     TMTypes.Resource[] resources = new TMTypes.Resource[nResources];
    //     for (int i = 1; i < split.length; i++) {
    //         TMTypes.Resource res = Utils.searchEnum(TMTypes.Resource.class, split[i]);
    //         if (res != null) {
    //             resources[i - 1] = res;
    //         } else {
    //         }
    //     }
    //     mt.setResources(resources);

    //     return mt;
    // }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalicoBoardTile)) return false;
        if (!super.equals(o)) return false;
        CalicoBoardTile calicoMapTile = (CalicoBoardTile) o;
        return x == calicoMapTile.x && y == calicoMapTile.y && tilePlaced == calicoMapTile.tilePlaced && isEmpty == calicoMapTile.isEmpty && isDesignTile == calicoMapTile.isDesignTile && designGoalTile == calicoMapTile.designGoalTile && hasButton == calicoMapTile.hasButton && hasCat == calicoMapTile.hasCat;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), ownerId, x, y, tilePlaced, type, isEmpty, isDesignTile, designGoalTile, hasButton, hasCat);
        result = 31 * result;
        return result;
    }
}
