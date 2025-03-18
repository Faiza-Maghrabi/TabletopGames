package games.calico.types;

/*
 * Class that holds the count and valid status of each arrangement looked at thrugh findMatchArrangement
 * Both of these values are used for heuristics where if there are no valid arrangements for a cat pattern, the AI Player is discouraged
 * from placing the tile there
 * Count is used to track progress towards a successful cat arrangement
 */
public class CalicoPlacementStatus {
    int sizeFound;
    boolean validPlacement;

        //for init
    public CalicoPlacementStatus (int sizeFound, boolean validPlacement) {
        updateParams(sizeFound, validPlacement);
    }

    //updating value
    public void updateParams(int sizeFound, boolean validArrangement) {
        this.sizeFound = sizeFound;
        this.validPlacement = validArrangement;
    }

    public int getsizeFound() {
        return sizeFound;
    }

    public boolean getvalidPlacement() {
        return validPlacement;
    }
}
