package games.calico.gui;

import core.*;
import core.actions.AbstractAction;
import core.components.Deck;

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
            //TODO Auto-generated constructor stub
        }
    
        @Override
    public int getMaxActionSpace() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMaxActionSpace'");
    }

    @Override
    protected void _update(AbstractPlayer player, AbstractGameState gameState) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method '_update'");
    }

}
