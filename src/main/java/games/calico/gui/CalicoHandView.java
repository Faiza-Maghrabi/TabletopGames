package games.calico.gui;

import static gui.GUI.defaultItemSize;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import core.components.Component;
import core.components.Deck;
import core.components.Edge;

import javax.swing.JComponent;

import games.calico.components.CalicoBoard;
import games.calico.components.CalicoBoardTile;
import games.calico.components.CalicoTile;
import gui.IScreenHighlight;
import gui.views.ComponentView;
import utilities.ImageIO;
import utilities.Pair;

public class CalicoHandView extends JComponent implements IScreenHighlight {

    Deck<CalicoTile> hand;
    int playerId;
    int tileRadius = 26;
    Rectangle display;

    // Highlights from clicking on the board
    HashMap<Pair<Point, Integer>, Rectangle> vertexToRectMap;
    Set<Pair<Point, Integer>> vertexHighlight;  // A set to represent one vertex highlighted, because it may be respective to any of the 3 adjacent tiles
    HashMap<Pair<Point, Integer>, Rectangle> edgeToRectMap;
    Set<Pair<Point, Integer>> edgeHighlight;  // A set to represent one edge highlighted, because it may be respective to any of the 2 adjacent tiles
    HashMap<Point, Rectangle> hexToRectMap;
    Set<Point> hexHighlight;

    public CalicoHandView(Deck<CalicoTile> hand, int playerId) {
        this.hand = hand;
        this.playerId = playerId;
        this.display = new Rectangle(10,10, 100, 30);


        //highlight data from catan - look over?
        edgeToRectMap = new HashMap<>();
        vertexToRectMap = new HashMap<>();
        hexToRectMap = new HashMap<>();
        vertexHighlight = new HashSet<>();
        edgeHighlight = new HashSet<>();
        hexHighlight = new HashSet<>();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                vertexHighlight.clear();
                edgeHighlight.clear();
                hexHighlight.clear();
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // Left-click
                    for (Map.Entry<Pair<Point, Integer>, Rectangle> entry: vertexToRectMap.entrySet()) {
                        if (entry.getValue().contains(e.getPoint())) {
                            vertexHighlight.add(entry.getKey());
                        }
                    }
                    for (Map.Entry<Pair<Point, Integer>, Rectangle> entry: edgeToRectMap.entrySet()) {
                        if (entry.getValue().contains(e.getPoint())) {
                            edgeHighlight.add(entry.getKey());
                        }
                    }
                    for (Map.Entry<Point, Rectangle> entry: hexToRectMap.entrySet()) {
                        if (entry.getValue().contains(e.getPoint())) {
                            hexHighlight.add(entry.getKey());
                            break;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void clearHighlights() {
        edgeHighlight.clear();
        vertexHighlight.clear();
        hexHighlight.clear();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g);
        drawDeck(g2);
    }

    //different version of DeckView.java due to use of hexagons
    private void drawDeck(Graphics2D g) {

        //should i have the images preloaded somewhere or is this fine?
        int offset = Math.max((display.width - tileRadius) / hand.getSize(), 10);

        for (int i = 0; i < hand.getSize(); i++) {
            // Get the center coordinates of the tile
            CalicoTile tile = hand.get(i);
            System.out.println("player" + playerId + " tile" + tile.getColour() + tile.getPattern());
            //Polygon tileHex = tile.getHexagon(tileRadius);
            drawImage(g, tile.getImagePath(), display.x + offset * i, display.y, tileRadius * 2, tileRadius *2);

        }
    }


    public static void drawImage(Graphics2D g, String path, int x, int y, int width, int height) {
        Image image = ImageIO.GetInstance().getImage(path);
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        double scaleW = width*1.0/w;
        double scaleH = height*1.0/h;
        g.drawImage(image, x, y, (int) (w*scaleW), (int) (h*scaleH), null);
    }

    //drawSettlement may be needed for buttons and cats
    
}
