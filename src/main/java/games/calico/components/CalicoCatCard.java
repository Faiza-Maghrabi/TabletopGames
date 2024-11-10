package games.calico.components;

import java.util.Arrays;
import java.util.Objects;

import core.components.Card;
import games.calico.CalicoTypes.Cat;
import games.calico.CalicoTypes.TilePattern;

public class CalicoCatCard extends Card{
    Cat cat;

    TilePattern[] patches = new TilePattern[2];

    CalicoCatCard(Cat cat, TilePattern patch1, TilePattern patch2) {
        this.cat = cat;
        this.patches[0] = patch1;
        this.patches[1] = patch2;
    }

    public String getName(){ 
        return cat.getName();
    }

    public int getPoints(){
        return cat.getPoints();
    }

    public int getArrangement(){
        return cat.getArrangement();
    }

    public String getImagePath() {
        return cat.getImagePath();
    }

    public TilePattern[] getPatches(){
        return patches;
    }

    @Override
    public CalicoCatCard copy() {
        CalicoCatCard copy = new CalicoCatCard(cat, patches[0], patches[1]);
        copyComponentTo(copy);
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalicoCatCard)) return false;
        if (!super.equals(o)) return false;
        CalicoCatCard calicoCat = (CalicoCatCard) o;
        return Objects.equals(cat, calicoCat.cat) && Arrays.equals(patches, calicoCat.patches); //will that work?
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), cat, patches[0], patches[1]); //will this work too?
        result = 31 * result + Arrays.hashCode(patches);
        return result;
    }
    
}
