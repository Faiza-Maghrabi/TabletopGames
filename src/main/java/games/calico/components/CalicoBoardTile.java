package games.calico.components;

import core.components.Component;
import games.calico.CalicoTypes;
import games.calico.CalicoTypes.TileColour;
import games.calico.CalicoTypes.TilePattern;

import java.util.Objects;

import static core.CoreConstants.ComponentType.BOARD_NODE;

public class CalicoBoardTile extends Component {
    int x, y, playerBoard;
    CalicoBoardTileImage tilePlaced;
    boolean isEmpty = true;

    boolean isDesignTile = false;
    CalicoTypes.DesignGoalTile designGoalTile;

    public CalicoBoardTile(int x, int y, int playerBoard) {
        super(BOARD_NODE, "Tile");
        this.x = x;
        this.y = y;
        this.playerBoard = playerBoard;
        this.tilePlaced = new CalicoBoardTileImage(TileColour.Null, TilePattern.Null);
    }

    public CalicoBoardTile(int x, int y, int playerBoard, CalicoTypes.DesignGoalTile designGoalTile) {
        super(BOARD_NODE, "Tile");
        this.x = x;
        this.y = y;
        this.playerBoard = playerBoard;
        this.designGoalTile = designGoalTile;
        this.isDesignTile = true;
        this.isEmpty = false;
    }

    public CalicoBoardTile(int x, int y, int playerBoard, TileColour colour, TilePattern pattern) {
        super(BOARD_NODE, "Tile");
        this.x = x;
        this.y = y;
        this.playerBoard = playerBoard;
        this.tilePlaced = new CalicoBoardTileImage(colour, pattern);
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

    public void addTile(TileColour colour, TilePattern pattern) {
        if (!isDesignTile && isEmpty) {
            this.tilePlaced = new CalicoBoardTileImage(colour, pattern);
            this.isEmpty = false;
        }
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
        CalicoBoardTile copy = new CalicoBoardTile(x, y, playerBoard);
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
    //             // TODO: Ocean (place tile), MegaCredit/-6 (reduce MC by 6)
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
        return playerBoard == calicoMapTile.playerBoard && x == calicoMapTile.x && y == calicoMapTile.y && tilePlaced == calicoMapTile.tilePlaced && isEmpty == calicoMapTile.isEmpty && isDesignTile == calicoMapTile.isDesignTile && designGoalTile == calicoMapTile.designGoalTile;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), ownerId, x, y, tilePlaced, type, isEmpty, isDesignTile, designGoalTile);
        result = 31 * result;
        return result;
    }
}