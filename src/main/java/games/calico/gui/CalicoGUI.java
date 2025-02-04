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

        parent.setPreferredSize(new Dimension(this.width + 1000, this.height + defaultActionPanelHeight + defaultInfoPanelHeight + defaultCardHeight + 400));

        //JComponent actionPanel = createActionPanel(new IScreenHighlight[]{view},
        //        width, defaultActionPanelHeight);

        parent.setLayout(new FlowLayout());

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new BorderLayout(10, 10));

        //market should be held here and cats
        JPanel a = new JPanel();
        a.setBackground(Color.pink);
        a.setPreferredSize(new Dimension(10, 10));
        //boardPanel.add(a, BorderLayout.CENTER);

        for (int i = 0; i < gameState.getNPlayers(); i++) {
            CalicoBoardView boardView = new CalicoBoardView(gameState.getPlayerBoards()[i], i, params.getBoardSize());
            boardView.setPreferredSize(new Dimension(500, 320));

            CalicoHandView handView = new CalicoHandView(gameState.getPlayerTiles()[i], i);
            //add extra borderlayout to center grid when i == 0 or 1 (south and north)
                JPanel playerPanel = new JPanel();
                playerPanel.setLayout(new FlowLayout());
                playerPanel.add(boardView);
                playerPanel.add(handView);
                boardPanel.add(playerPanel, borderLayout[i]);
        }

        parent.add(boardPanel);

        JPanel wrapper = new JPanel();
        // wrapper.setBackground(Color.white);
        parent.add(wrapper);

        JPanel infoPanel = createGameStateInfoPanel("Calico", gameState, width, defaultInfoPanelHeight);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBackground(Color.white);
        wrapper.add(infoPanel);


        //parent.setPreferredSize(new Dimension(width, height + defaultActionPanelHeight + defaultInfoPanelHeight + defaultCardHeight + 20));
        // parent.revalidate();
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
