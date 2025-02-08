package games.calico.gui;

import core.components.Deck;
import games.calico.components.CalicoTile;
import gui.views.CardView;
import gui.views.DeckView;
import utilities.ImageIO;

import java.awt.*;

import static games.calico.gui.CalicoGUI.*;

public class CalicoDeckView extends DeckView<CalicoTile> {

    String title;

    /**
     * Constructor initialising information and adding key/mouse listener for card highlight (left click or ALT + hover
     * allows showing the highlighted card on top of all others).
     * @param d - deck to draw
     * @param visible - true if whole deck visible
     * @param dataPath - path to assets
     */
    public CalicoDeckView(int human, Deck<CalicoTile> d, Rectangle rect, String title) {
        super(human, d, true, tileRadius*2, tileRadius*2, rect);
        this.title = title;
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

    @Override
    public void drawDeck(Graphics2D g) {
        Font ogFont = g.getFont();
        Font boldFont = new Font(g.getFont().getName(), Font.BOLD, 20);
        g.setFont(boldFont);
        g.drawString(title, rect.x, rect.y - 10);
        g.setFont(ogFont);
        super.drawDeck(g);
    }

}
