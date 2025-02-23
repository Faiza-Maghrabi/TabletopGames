package games.calico;

import games.calico.components.CalicoCatCard;

//return type for lookForCat in CalicoBoard - returns the catCard and the size found;
//made as I need both size found and the catCard as return values
public class CalicoLookForCatReturn {
    CalicoCatCard catCard;
    int sizeFound;

    //for init
    public CalicoLookForCatReturn (CalicoCatCard catCard, int sizeFound) {
        updateParams(catCard, sizeFound);
    }

    //updating value
    public void updateParams(CalicoCatCard catCard, int sizeFound) {
        this.catCard = catCard;
        this.sizeFound = sizeFound;
    }

    public CalicoCatCard getCatCard() {
        return catCard;
    }

    public int getsizeFound() {
        return sizeFound;
    }
}
