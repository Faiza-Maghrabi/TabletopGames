package games.calico.gui;

import core.components.Deck;
import games.calico.components.CalicoTile;
import gui.views.CardView;
import gui.views.DeckView;
import utilities.ImageIO;

import java.awt.*;

import static games.calico.gui.CalicoGUI.*;

public class CalicoHandView extends DeckView<CalicoTile> {

    String dataPath;
    Image backOfCard;

    /**
     * Constructor initialising information and adding key/mouse listener for card highlight (left click or ALT + hover
     * allows showing the highlighted card on top of all others).
     * @param d - deck to draw
     * @param visible - true if whole deck visible
     * @param dataPath - path to assets
     */
    public CalicoHandView(int human, Deck<CalicoTile> d, boolean visible, String dataPath, Rectangle rect) {
        super(human, d, visible, tileRadius*2, tileRadius*2, rect);
        this.dataPath = dataPath;
    }

    /**
     * Draws the specified component at the specified place
     *
     * @param g         Graphics object
     * @param rect      Where the item is to be drawn
     * @param card The item itself
     * @param front     true if the item is visible (e.g. the card details); false if only the card-back
     */
    @Override
    public void drawComponent(Graphics2D g, Rectangle rect, CalicoTile tile, boolean front) {
        Image tileImage = ImageIO.GetInstance().getImage(tile.getImagePath());
        //always visible
        CardView.drawCard(g, rect, tile, tileImage, tileImage, true);
    }

}
