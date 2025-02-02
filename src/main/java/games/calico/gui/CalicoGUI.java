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

        parent.setPreferredSize(new Dimension(this.width + 200, this.height + defaultActionPanelHeight + defaultInfoPanelHeight + defaultCardHeight + 100));

        //JComponent actionPanel = createActionPanel(new IScreenHighlight[]{view},
        //        width, defaultActionPanelHeight);

        parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new BorderLayout(10, 10));

        JTextArea a = new JTextArea();
        a.setPreferredSize(new Dimension(200, 300));

        boardPanel.add(a, BorderLayout.CENTER);

        System.out.println(gameState.getNPlayers());

        for (int i = 0; i < gameState.getNPlayers(); i++) {
            CalicoBoardView boardView = new CalicoBoardView(gameState.getPlayerBoards()[i], i, params.getBoardSize());
            boardView.setPreferredSize(new Dimension(200, 300));
            boardPanel.add(boardView, borderLayout[i]);
        }

        // CalicoBoardView boardView = new CalicoBoardView(gameState.getPlayerBoards()[0], 0, params.getBoardSize());
        // boardView.setPreferredSize(new Dimension(200, 300));
        // boardPanel.add(boardView, BorderLayout.CENTER);
        // CalicoBoardView boardView2 = new CalicoBoardView(gameState.getPlayerBoards()[1], 1, params.getBoardSize());
        // boardView2.setPreferredSize(new Dimension(200, 300));
        // boardPanel.add(boardView2, BorderLayout.SOUTH);
        // boardPanel.add(new JButton("Center"),BorderLayout.EAST);
        // boardPanel.add(new JButton("Center"),BorderLayout.NORTH);
        // boardPanel.add(new JButton("Center"),BorderLayout.WEST);

        parent.add(boardPanel);

        JPanel wrapper = new JPanel();
        // wrapper.setBackground(Color.white);
        parent.add(wrapper);

        JPanel infoPanel = createGameStateInfoPanel("Calico", gameState, width, defaultInfoPanelHeight);
        wrapper.setLayout(new BorderLayout());
        wrapper.setBackground(Color.white);
        wrapper.add(infoPanel);



        // JPanel mainPanel = new JPanel();
        // mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        // mainPanel.add(Box.createRigidArea(new Dimension(5,0)));
        // mainPanel.add(boardView);

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
