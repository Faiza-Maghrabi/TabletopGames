package games.calico.gui;

import core.*;
import games.calico.CalicoGameParameters;
import games.calico.CalicoGameState;
import games.calico.CalicoTypes.Button;
import gui.AbstractGUIManager;
import gui.GamePanel;
import gui.IScreenHighlight;
import players.human.ActionController;

import javax.swing.*;

import java.awt.*;
import java.util.*;

/*
 * https://tabletopgames.ai/wiki/games/creating/gui
 */
public class CalicoGUI extends AbstractGUIManager {
    final static int boardWidth = 500;
    final static int boardHeight = 420;
    final static int tileRadius = 35;
    final static int deckWidth = 300;
    final static int deckHeight = tileRadius * 2;

    public CalicoGUI(GamePanel parent, Game game, ActionController ac, Set<Integer> human) {
        super(parent, game, ac, human);

        if (game == null) return;

        CalicoGameState gameState = (CalicoGameState) game.getGameState();
        CalicoGameParameters params = (CalicoGameParameters) gameState.getGameParameters();
        //view = new Connect4BoardView(gameState.getGridBoard());

        // Set width/height of display
        this.width = Math.max(defaultDisplayWidth, defaultItemSize * params.getBoardSize());
        this.height = defaultItemSize * params.getBoardSize();

        parent.setPreferredSize(new Dimension(this.width + 1100, this.height + defaultActionPanelHeight + defaultInfoPanelHeight + defaultCardHeight + 200));

        parent.setLayout(new FlowLayout());

        JTabbedPane pane = new JTabbedPane();

        parent.add(pane);


        JPanel gameInfo = new JPanel();
        gameInfo.setLayout(new BoxLayout(gameInfo, BoxLayout.Y_AXIS));
        gameInfo.setPreferredSize(new Dimension(700, boardHeight+100));

        //market should be held here and cats
        CalicoDeckView market = new CalicoDeckView(-1, gameState.getTileMarket(),  new Rectangle(0,boardHeight/2, deckWidth, deckHeight), "Tile Market:", null);
        gameInfo.add(market);

        CalicoCatQuestView catQuestView = new CalicoCatQuestView(gameState.getActiveCats(), new Rectangle(0,0, deckWidth, deckHeight));
        gameInfo.add(catQuestView);

        parent.add(gameInfo);

        // JPanel boardPanel = new JPanel();
        // boardPanel.setLayout(new BorderLayout(10, 10));

        for (int i = 0; i < gameState.getNPlayers(); i++) {
            CalicoBoardView boardView = new CalicoBoardView(gameState.getPlayerBoards()[i], i, params.getBoardSize(), gameState.getActiveCats());
            boardView.setPreferredSize(new Dimension(boardWidth, boardHeight));
            //System.out.println(gameState.getPlayerButtonScore()[i].get(Button.Rainbow).getValueIdx());
            CalicoDeckView handView = new CalicoDeckView(i, gameState.getPlayerTiles()[i], new Rectangle(0,60, deckWidth, deckHeight*3), "Hand:", gameState.getPlayerButtonScore()[i]);
            //CalicoRainbowButtons rainbowView = new CalicoRainbowButtons(, new Rectangle(0,0, tileRadius, tileRadius));

            JPanel playerPanel = new JPanel();
            playerPanel.setLayout(new FlowLayout(FlowLayout.CENTER,0, 0));
            JPanel playerSidePanel = new JPanel();
            playerSidePanel.setLayout(new BoxLayout(playerSidePanel, BoxLayout.Y_AXIS));
            //playerSidePanel.add(new JLabel("<html><h1>Rainbow buttons:</h1></html>"));
            //playerSidePanel.add(rainbowView);
            playerSidePanel.add(handView);
            //playerSidePanel.add(new TextArea("adsadsadsa"));

            playerPanel.add(boardView);
            playerPanel.add(playerSidePanel);

            pane.add("Player "+ i, playerPanel);

            // boardPanel.add(playerPanel, borderLayout[i]);
        }

        // parent.add(boardPanel);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        // wrapper.setBackground(Color.white);
        parent.add(sidePanel);

        JPanel infoPanel = createGameStateInfoPanel("Calico", gameState, width, defaultInfoPanelHeight);
        sidePanel.add(infoPanel);

        JComponent actionPanel = createActionPanel(new IScreenHighlight[0], width, defaultActionPanelHeight, false, true, null, null, null);
        sidePanel.add(actionPanel);

        //parent.setPreferredSize(new Dimension(width, height + defaultActionPanelHeight + defaultInfoPanelHeight + defaultCardHeight + 20));
        // parent.revalidate();

        parent.revalidate();
        parent.setVisible(true);
        parent.repaint();
    }
    
    //one action for each spot on the board
    @Override
    public int getMaxActionSpace() {
        return 25;
    }

    @Override
    protected void _update(AbstractPlayer player, AbstractGameState gameState) {
        parent.revalidate();
        parent.setVisible(true);
        parent.repaint();
    }

}
