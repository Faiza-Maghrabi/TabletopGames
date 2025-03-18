package games.calico.types;

import games.calico.components.CalicoCatCard;

//return type for lookForCat in CalicoBoard - returns the catCard and the size found;
//made as I need both size found and the catCard as return values
public class CalicoLookForCatReturn {
    CalicoCatCard catCard;
    CalicoPlacementStatus placementStatus;

    //for init
    public CalicoLookForCatReturn (CalicoCatCard catCard, int sizeFound, boolean validArrangement) {
        updateParams(catCard, sizeFound, validArrangement);
    }

    //updating value
    public void updateParams(CalicoCatCard catCard, int sizeFound, boolean validArrangement) {
        this.catCard = catCard;
        this.placementStatus = new CalicoPlacementStatus(sizeFound, validArrangement);
    }

    public CalicoCatCard getCatCard() {
        return catCard;
    }

    public int getsizeFound() {
        return placementStatus.getsizeFound();
    }

    public boolean getvalidPlacement() {
        return placementStatus.getvalidPlacement();
    }
}
