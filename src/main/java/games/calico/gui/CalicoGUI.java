package games.calico.gui;

import core.*;
import core.actions.AbstractAction;
import core.components.Deck;
import games.calico.CalicoGameParameters;
import games.calico.CalicoGameState;

import gui.AbstractGUIManager;
import gui.GamePanel;
import gui.IScreenHighlight;
import players.human.ActionController;
import players.human.HumanGUIPlayer;
import utilities.ImageIO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;

/*
 * https://tabletopgames.ai/wiki/games/creating/gui
 */
public class CalicoGUI extends AbstractGUIManager {
    final static int boardWidth = 350;
    final static int boardHeight = 320;
    final static int tileRadius = 26;
    final static int deckWidth = 220;
    final static int deckHeight = 70;

    public CalicoGUI(GamePanel parent, Game game, ActionController ac, Set<Integer> human) {
        super(parent, game, ac, human);
        if (game == null) return;

        CalicoGameState gameState = (CalicoGameState) game.getGameState();
        CalicoGameParameters params = (CalicoGameParameters) gameState.getGameParameters();

        String [] borderLayout = params.getBorderLayout();


        //view = new Connect4BoardView(gameState.getGridBoard());

        // Set width/height of display
        this.width = Math.max(defaultDisplayWidth, defaultItemSize * params.getBoardSize());
        this.height = defaultItemSize * params.getBoardSize();



        parent.setPreferredSize(new Dimension(this.width + 700, this.height + defaultActionPanelHeight + defaultInfoPanelHeight + defaultCardHeight + 400));

        //JComponent actionPanel = createActionPanel(new IScreenHighlight[]{view},
        //        width, defaultActionPanelHeight);

        parent.setLayout(new FlowLayout());

        JPanel childPanel = new JPanel(new FlowLayout());

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new BorderLayout(10, 10));


        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setPreferredSize(new Dimension(400, boardHeight));

        //market should be held here and cats
        JLabel title = new JLabel("<html><h2>Tile Market:</h2></html>");
        title.setLocation(0, 90);
        centerPanel.add(title);
        CalicoDeckView market = new CalicoDeckView(-1, gameState.getTileMarket(),  new Rectangle(0,60, tileRadius*10, tileRadius*2));
        centerPanel.add(market);

        CalicoCatQuestView catQuestView = new CalicoCatQuestView(gameState.getActiveCats(), new Rectangle(0,0, deckWidth, deckHeight));
        centerPanel.add(catQuestView);

        boardPanel.add(centerPanel, BorderLayout.CENTER);

        


        for (int i = 0; i < gameState.getNPlayers(); i++) {
            CalicoBoardView boardView = new CalicoBoardView(gameState.getPlayerBoards()[i], i, params.getBoardSize());
            boardView.setPreferredSize(new Dimension(boardWidth, boardHeight));

            CalicoDeckView handView = new CalicoDeckView(i, gameState.getPlayerTiles()[i], new Rectangle(0,0, deckWidth, deckHeight));
            //add extra borderlayout to center grid when i == 0 or 1 (south and north)
                JPanel playerPanel = new JPanel();
                playerPanel.setLayout(new FlowLayout(FlowLayout.CENTER,0, 0));
                if (i == 3) {
                    playerPanel.add(handView);
                    playerPanel.add(boardView);
                }
                else {
                    playerPanel.add(boardView);
                    playerPanel.add(handView);
                }

                boardPanel.add(playerPanel, borderLayout[i]);
        }

        childPanel.add(boardPanel);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        // wrapper.setBackground(Color.white);
        childPanel.add(sidePanel);

        JPanel infoPanel = createGameStateInfoPanel("Calico", gameState, width, defaultInfoPanelHeight);
        sidePanel.add(infoPanel);

        JComponent actionPanel = createActionPanel(new IScreenHighlight[0], width, defaultActionPanelHeight, false, true, null, null, null);
        sidePanel.add(actionPanel);

        //parent.setPreferredSize(new Dimension(width, height + defaultActionPanelHeight + defaultInfoPanelHeight + defaultCardHeight + 20));
        // parent.revalidate();

        childPanel.setPreferredSize(new Dimension(1200, 600));
        JScrollPane scrollPane = new JScrollPane(childPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        parent.add(scrollPane);
        parent.revalidate();
        parent.setVisible(true);
        parent.repaint();
    }
    
    @Override
    public int getMaxActionSpace() {
        return 5000;
    }

    @Override
    protected void _update(AbstractPlayer player, AbstractGameState gameState) {
        parent.repaint();
    }

}
