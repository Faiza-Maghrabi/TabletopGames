package games.calico.components;

import core.components.Component;
import games.calico.CalicoTypes;
import games.calico.CalicoTypes.TileColour;
import games.calico.CalicoTypes.TilePattern;

import java.awt.Point;
import java.awt.Polygon;
import java.util.Objects;

import static core.CoreConstants.ComponentType.BOARD_NODE;

//Odd-r grid for GridBoard
public class CalicoBoardTile extends Component {
    private int x, y;
    private CalicoTile tilePlaced;
    private boolean isEmpty = true;

    private boolean isDesignTile = false;
    private CalicoTypes.DesignGoalTile designGoalTile;

    private boolean hasButton = false;
    private boolean hasButtonGUI = false;
    private boolean hasCat = false;

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
    
    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean isDesignTile() {
        return isDesignTile;
    }

    public boolean hasButton() {
        return hasButton;
    }

    public boolean hasButtonGUI() {
        return hasButtonGUI;
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

    public void addTile(CalicoTile tile) {
        if (!isDesignTile && isEmpty) {
            this.tilePlaced = tile;
            this.isEmpty = false;
        }
    }


    public void addButton(){
        this.hasButton = true;
    }

    public void addButtonGUI(){
        this.hasButtonGUI = true;
    }

    public void addCat(){
        this.hasCat = true;
    }

    public TileColour getTileColour() {
        if (!isDesignTile) {
            return tilePlaced.getColour();
        }
        return TileColour.Null;
    }

    public TilePattern getTilePattern() {
        if (!isDesignTile) {
            return tilePlaced.getPattern();
        }
        return TilePattern.Null;
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

    public boolean getHasButton() {
        return hasButton;
    }

    public String getImagePath() {
        if (!isEmpty){
            if (isDesignTile){
                return designGoalTile.getImagePath();
            }
            return tilePlaced.getImagePath();
        }
        return "";
    }

    //tile functions - based off catanTile's
    public Point getCentreCoords(double radius) {
        // offset used in the even-r representation
        double offset_y = 0;
        double offset_x = 0;

        // width and height of a hexagon in pointy rotation
        double width = Math.sqrt(3) * radius;
        double height = 2 * radius;

        if (y % 2 == 1) {
            // odd rows
            offset_x = width;
            offset_y = height * 0.5;
        } else {
            // even rows
            offset_x = width * 0.5;
            offset_y = height * 0.5;
        }

        if (x % 2 == 1){
            //odd columns
            offset_y = height * 0.1;
        }

        double x_coord = offset_x + x * width;
        double y_coord = offset_y + y * height * 0.75;
        return new Point((int) x_coord, (int) y_coord);
    }

    //returns polygon used for rendering
    public Polygon getHexagon(double radius) {
        Polygon polygon = new Polygon();
        Point centreCoords = getCentreCoords(radius);
        double x_coord = centreCoords.x;
        double y_coord = centreCoords.y;
        for (int i = 0; i < 6; i++) {
            double angle_deg = i * 60 - 30;
            double angle_rad = Math.PI / 180 * angle_deg;
            int xval = (int) (x_coord + radius * Math.cos(angle_rad));
            int yval = (int) (y_coord + radius * Math.sin(angle_rad));
            polygon.addPoint(xval, yval);
        }
        return polygon;
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
