package games.calico.components;

import java.util.Objects;

import core.components.Card;
import games.calico.CalicoTypes.TileColour;
import games.calico.CalicoTypes.TilePattern;

public class CalicoTile extends Card {

    TileColour colour;
    TilePattern pattern;

    String imagePath;
    String imagePathStart = "data/calico/images/tiles/";

    CalicoTile(TileColour colour, TilePattern pattern) {
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

    @Override
    public CalicoTile copy() {
        CalicoTile copy = new CalicoTile(colour, pattern);
        copyComponentTo(copy);
        copy.imagePath = imagePath;
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalicoTile)) return false;
        if (!super.equals(o)) return false;
        CalicoTile calicoTile = (CalicoTile) o;
        return colour == calicoTile.colour && pattern == calicoTile.pattern && imagePath == calicoTile.imagePath;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), colour, pattern, imagePath);
        result = 31 * result;
        return result;
    }
}
