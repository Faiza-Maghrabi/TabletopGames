package games.calico.components;

import games.calico.CalicoTypes.TileColour;
import games.calico.CalicoTypes.TilePattern;

public class CalicoBoardTileImage {

    TileColour colour;
    TilePattern pattern;

    String imagePath;
    String imagePathStart = "data/calico/images/tiles/";

    CalicoBoardTileImage(TileColour colour, TilePattern pattern) {
        this.colour = colour;
        this.pattern = pattern;
        this.imagePath = imagePathStart + colour + "/" + pattern + ".png";
    }

    public String getImagePath() {
        return imagePath;
    }

    public TileColour getColour() {
        return colour;
    }

    public TilePattern getPattern() {
        return pattern;
    }
}
