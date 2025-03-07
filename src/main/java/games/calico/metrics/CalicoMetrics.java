package games.calico.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import core.components.Counter;
import core.interfaces.IGameEvent;
import evaluation.listeners.MetricsGameListener;
import evaluation.metrics.AbstractMetric;
import evaluation.metrics.Event;
import evaluation.metrics.IMetricsCollection;

import games.calico.CalicoGameState;
import games.calico.CalicoTypes.Button;
import games.calico.CalicoTypes.Cat;
import games.calico.CalicoTypes.DesignGoalTile;
import games.calico.components.CalicoCatCard;


public class CalicoMetrics implements IMetricsCollection {
    /**
     * How many cats were achieved by players and its type
     */
    public static class CatsAchievedCount extends AbstractMetric {

        @Override
        protected boolean _run(MetricsGameListener listener, Event e, Map<String, Object> records) {
            CalicoGameState gs = (CalicoGameState) e.state;
            CalicoCatCard[] activeCats = gs.getActiveCats();
            HashMap<Cat, Counter>[] playerCatScore = gs.getPlayerCatScore();
            //System.out.println("CatsAchievedCount run");
            for (int i = 0; i < gs.getNPlayers(); i++) {
                for (int j = 0; j < gs.getActiveCats().length; j++) {
                    Cat c = activeCats[j].getCat();
                    int catCount = playerCatScore[i].get(c).getValueIdx();
                    //System.out.println("Player " + i + ": " +c.toString());
                    records.put("Player " + i + ": " +c.toString(), catCount);
                }
            }
            //System.out.println("--------------------------------------------------------");
            return true;
        }

        @Override
        public Map<String, Class<?>> getColumns(int nPlayersPerGame, Set<String> playerNames) {
            Map<String, Class<?>> columns = new HashMap<>();
            System.out.println("CatsAchievedCount getColumns");
            for (int i = 0; i < nPlayersPerGame; i++){
                for (Cat c: Cat.values()) {
                    System.out.println("Player " + i + ": " +c.toString());
                    columns.put("Player " + i + ": " +c.toString(), Integer.class);
                }
            }
            System.out.println("--------------------------------------------------------");
            return columns;
        }

        @Override
        public Set<IGameEvent> getDefaultEventTypes() {
            return Collections.singleton(Event.GameEvent.GAME_OVER);
        }

    }

    /*
     * How many buttons were achieved by players and its type
     */
    public static class ButtonsAchievedCount extends AbstractMetric {

        @Override
        protected boolean _run(MetricsGameListener listener, Event e, Map<String, Object> records) {
            CalicoGameState gs = (CalicoGameState) e.state;
            HashMap<Button, Counter>[] playerButtonScore = gs.getPlayerButtonScore();

            for (int i = 0; i < gs.getNPlayers(); i++) {
                for (Button b : Button.values()) {
                    int buttonCount = playerButtonScore[i].get(b).getValueIdx();
                    records.put("Player " + i + ": " +b.toString(), buttonCount);
                }
            }
            return true;
        }

        @Override
        public Map<String, Class<?>> getColumns(int nPlayersPerGame, Set<String> playerNames) {
            Map<String, Class<?>> columns = new HashMap<>();
            for (int i = 0; i < nPlayersPerGame; i++){
                for (Button b : Button.values()){
                    columns.put("Player " + i + ": " +b.toString(), Integer.class);
                }
            }
            return columns;
        }

        @Override
        public Set<IGameEvent> getDefaultEventTypes() {
            return Collections.singleton(Event.GameEvent.GAME_OVER);
        }

    }

    /*
     * Design Tiles and the levels achieved by players
     */
    public static class DesignTokensAchievedLevel extends AbstractMetric {

        @Override
        protected boolean _run(MetricsGameListener listener, Event e, Map<String, Object> records) {
            CalicoGameState gs = (CalicoGameState) e.state;

            for (int i = 0; i < gs.getNPlayers(); i++) {
                for (int j = 0; j < 3; j++) {
                    String[] goalVals = gs.getPlayerDesignGoalReached(i, j);
                    records.put("Player " + i + ": " + goalVals[0], Integer.valueOf(goalVals[1]));
                }
            }
            return true;
        }

        @Override
        public Map<String, Class<?>> getColumns(int nPlayersPerGame, Set<String> playerNames) {
            Map<String, Class<?>> columns = new HashMap<>();
            for (int i = 0; i < nPlayersPerGame; i++){
                for (DesignGoalTile d: DesignGoalTile.values()) {
                    columns.put("Player " + i + ": " +d.toString(), Integer.class);
                }
            }
            return columns;
        }

        @Override
        public Set<IGameEvent> getDefaultEventTypes() {
            return Collections.singleton(Event.GameEvent.GAME_OVER);
        }

    }
}
