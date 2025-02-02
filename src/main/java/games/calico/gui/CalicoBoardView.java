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

import core.components.Edge;

import javax.swing.JComponent;

import games.calico.components.CalicoBoard;
import games.calico.components.CalicoBoardTile;
import games.catan.components.CatanTile;
import gui.IScreenHighlight;
import gui.views.ComponentView;
import utilities.Pair;

public class CalicoBoardView extends JComponent implements IScreenHighlight {

    CalicoBoard board;
    int playerId;
    int tileRadius = 20;
    int boardSize;

    // Highlights from clicking on the board
    HashMap<Pair<Point, Integer>, Rectangle> vertexToRectMap;
    Set<Pair<Point, Integer>> vertexHighlight;  // A set to represent one vertex highlighted, because it may be respective to any of the 3 adjacent tiles
    HashMap<Pair<Point, Integer>, Rectangle> edgeToRectMap;
    Set<Pair<Point, Integer>> edgeHighlight;  // A set to represent one edge highlighted, because it may be respective to any of the 2 adjacent tiles
    HashMap<Point, Rectangle> hexToRectMap;
    Set<Point> hexHighlight;

    public CalicoBoardView(CalicoBoard board, int playerId, int boardSize) {
        this.board = board;
        this.playerId = playerId;
        this.boardSize = boardSize;

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
        drawBoard(g2);
    }

    private void drawBoard(Graphics2D g) {

        Font f = g.getFont();
        Font boldFont = new Font(g.getFont().getName(), Font.BOLD, 12);
        

        for (int column = 0; column < boardSize; column++) {
            for (int  row = 0; row < boardSize; row++) {
                // Get the center coordinates of the tile
                CalicoBoardTile tile = board.getElement(column, row);
                Point centreCoords = tile.getCentreCoords(tileRadius);

                // Apply odd-r column offset (shift odd columns down)
                if (tile.getX() % 2 == 1) { 
                    centreCoords.y += tileRadius * 3 / 4; 
                }

                // Store the rectangle mapping for interaction
                hexToRectMap.put(new Point(tile.getX(), tile.getY()),
                    new Rectangle(centreCoords.x - tileRadius / 2, centreCoords.y - tileRadius / 2, tileRadius, tileRadius)
                );

                // Fill the hexagon //TODO put in image here?
                g.setColor(new Color(40, 157, 197));
                Polygon tileHex = tile.getHexagon(tileRadius);
                g.fillPolygon(tileHex);
                g.setColor(Color.BLACK);
                g.drawPolygon(tileHex); //outline in black
                g.setFont(boldFont);
                if (tile.isDesignTile()) {
                    g.drawString(tile.getDesignGoal().toString(), centreCoords.x, centreCoords.y);
                }
                else {
                    g.drawString(tile.getTileColour().toString(), centreCoords.x, centreCoords.y);
                }
                g.setFont(f);

            }
        }

    }

    //drawSettlement may be needed for buttons and cats
    
}
