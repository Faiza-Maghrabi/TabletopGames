package games.calico.gui;

import games.calico.CalicoTypes.TilePattern;
import games.calico.components.CalicoCatCard;
import utilities.ImageIO;

import java.awt.*;

import javax.swing.JComponent;

import static games.calico.gui.CalicoGUI.*;

//view that shows the current active cats and their patterns in the GUI - not interactable
public class CalicoCatQuestView extends JComponent {
    CalicoCatCard[] activeCats;
    Rectangle display;

    public CalicoCatQuestView(CalicoCatCard[] activeCats, Rectangle rect) {
        this.activeCats = activeCats;
        this.display = rect;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g);
        drawQuest(g2);
    }

    //draw out each cat alongside their patterns
    private void drawQuest(Graphics2D g) {
        int offset = Math.max((display.width - (tileRadius * 5)) / activeCats.length, 200);
        for (int i = 0; i < activeCats.length; i++) {
            Rectangle catRect = new Rectangle(display.x + offset * i, display.y - 2, 0, 0);
            Image catImg = ImageIO.GetInstance().getImage(activeCats[i].getImagePath());
            g.drawImage(catImg, catRect.x, catRect.y, tileRadius * 5, tileRadius * 3, null, null);
            TilePattern[] patch = activeCats[i].getPatches();
            Image pattern1 = ImageIO.GetInstance().getImage("data/calico/images/tiles/Black/"+patch[0]+".png");
            g.drawImage(pattern1, catRect.x + (int)((float)tileRadius * 0.4), catRect.y + (int)((float)tileRadius * 2.5), tileRadius * 2, tileRadius * 2, null, null);
            Image pattern2 = ImageIO.GetInstance().getImage("data/calico/images/tiles/Black/"+patch[1]+".png");
            g.drawImage(pattern2, catRect.x + (int)((float)tileRadius * 2.5), catRect.y + (int)((float)tileRadius * 2.5), tileRadius * 2, tileRadius * 2, null, null);
        }
    }

}
