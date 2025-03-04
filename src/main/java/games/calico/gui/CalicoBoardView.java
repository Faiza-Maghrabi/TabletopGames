package games.calico.gui;

import java.awt.*;

import javax.swing.JComponent;

import games.calico.CalicoTypes.TilePattern;
import games.calico.components.CalicoBoard;
import games.calico.components.CalicoBoardTile;
import games.calico.components.CalicoCatCard;

import static games.calico.gui.CalicoGUI.*;

import utilities.ImageIO;

public class CalicoBoardView extends JComponent {

    CalicoBoard board;
    int playerId;
    int boardSize;
    CalicoCatCard[] activeCats;

    public CalicoBoardView(CalicoBoard board, int playerId, int boardSize, CalicoCatCard[] activeCats) {
        this.board = board;
        this.playerId = playerId;
        this.boardSize = boardSize;
        this.activeCats = activeCats;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g);
        drawBoard(g2);
    }

    private void drawBoard(Graphics2D g) {

        Font f = g.getFont();
        // Font boldFont = new Font(g.getFont().getName(), Font.BOLD, 12);  
        
        //should i have the images preloaded somewhere or is this fine?

        for (int column = 0; column < boardSize; column++) {
            for (int  row = 0; row < boardSize; row++) {
                // Get the center coordinates of the tile
                CalicoBoardTile tile = board.getElement(column, row);
                Point centreCoords = tile.getCentreCoords(tileRadius);

                // Apply odd-r column offset (shift odd columns down)
                if (tile.getX() % 2 == 1) { 
                    centreCoords.y += tileRadius * 3 / 4; 
                }

                //Need to add button images and cat images to board when required

                if (!tile.isEmpty()) {
                    drawImage(g, tile.getImagePath(), centreCoords.x, centreCoords.y, tileRadius * 2, tileRadius *2);
                    if (tile.hasButtonGUI()){
                        drawImage(g, tile.getTileColour().getButton().getImagePath(), centreCoords.x + tileRadius/2, centreCoords.y + tileRadius/2, tileRadius, tileRadius);
                    }
                    if (tile.hasCatGUI()){
                        drawImage(g, findCatPath(tile.getTilePattern()), centreCoords.x + tileRadius/2, centreCoords.y + tileRadius/2, tileRadius, tileRadius);
                    }
                }
                else {
                    drawImage(g, board.getEmptyImagePath(), centreCoords.x, centreCoords.y, tileRadius * 2, tileRadius *2);
                    g.setColor(Color.BLACK);
                    g.setFont(f);
                    g.drawString("["+column+","+row+"]", centreCoords.x + tileRadius, centreCoords.y + tileRadius);
                }

            }
        }

    }

    //find image 
    private String findCatPath(TilePattern pattern){
        for (CalicoCatCard catCard : activeCats) {
            TilePattern[] catPatches = catCard.getPatches();
            if (catPatches[0] == pattern || catPatches[1] == pattern) {return catCard.getTokenImagePath();}
        }
        //should not reach this as this method is only called if a cat is found
        return null;
    }

    private static void drawImage(Graphics2D g, String path, int x, int y, int width, int height) {
        Image image = ImageIO.GetInstance().getImage(path);
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        double scaleW = width*1.0/w;
        double scaleH = height*1.0/h;
        g.drawImage(image, x, y, (int) (w*scaleW), (int) (h*scaleH), null);
    }

    
}
