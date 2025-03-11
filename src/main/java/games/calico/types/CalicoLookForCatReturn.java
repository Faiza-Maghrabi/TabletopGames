package games.calico.types;

import games.calico.components.CalicoCatCard;

//return type for lookForCat in CalicoBoard - returns the catCard and the size found;
//made as I need both size found and the catCard as return values
public class CalicoLookForCatReturn {
    CalicoCatCard catCard;
    CalicoCatArrangementStatus arrangementStatus;

    //for init
    public CalicoLookForCatReturn (CalicoCatCard catCard, int sizeFound, boolean validArrangement) {
        updateParams(catCard, sizeFound, validArrangement);
    }

    //updating value
    public void updateParams(CalicoCatCard catCard, int sizeFound, boolean validArrangement) {
        this.catCard = catCard;
        this.arrangementStatus = new CalicoCatArrangementStatus(sizeFound, validArrangement);
    }

    public CalicoCatCard getCatCard() {
        return catCard;
    }

    public int getsizeFound() {
        return arrangementStatus.getsizeFound();
    }

    public boolean getvalidArrangement() {
        return arrangementStatus.getvalidArrangement();
    }
}
