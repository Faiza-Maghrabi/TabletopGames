package games.calico.gui;

import core.*;
import core.actions.AbstractAction;
import core.components.Deck;
import games.calico.CalicoGameState;

import gui.AbstractGUIManager;
import gui.GamePanel;
import gui.IScreenHighlight;
import players.human.ActionController;
import players.human.HumanGUIPlayer;
import utilities.ImageIO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

        CalicoBoardView boardView = new CalicoBoardView(gameState.getPlayerBoards()[0], 0);

        //view = new Connect4BoardView(gameState.getGridBoard());

        // Set width/height of display
        // this.width = Math.max(defaultDisplayWidth, defaultItemSize * gameState.getGridBoard().getWidth());
        // this.height = defaultItemSize * gameState.getGridBoard().getHeight();

        //JComponent actionPanel = createActionPanel(new IScreenHighlight[]{view},
        //        width, defaultActionPanelHeight);

        JPanel wrapper = new JPanel();
        wrapper.setBackground(Color.white);
        parent.setLayout(new FlowLayout());
        parent.add(wrapper);

        JPanel infoPanel = createGameStateInfoPanel("Calico", gameState, width, defaultInfoPanelHeight);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBackground(Color.white);
        wrapper.add(infoPanel);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.add(Box.createRigidArea(new Dimension(5,0)));
        mainPanel.add(boardView);

        wrapper.add(mainPanel, BorderLayout.CENTER);

        //parent.setPreferredSize(new Dimension(width, height + defaultActionPanelHeight + defaultInfoPanelHeight + defaultCardHeight + 20));
        // parent.revalidate();
        // parent.setVisible(true);
        // parent.repaint();
        wrapper.revalidate();
        wrapper.repaint();
    }
    
    @Override
    public int getMaxActionSpace() {
        return 5000;
    }

    @Override
    protected void _update(AbstractPlayer player, AbstractGameState gameState) {
        //parent.repaint();
        return;
    }

}
