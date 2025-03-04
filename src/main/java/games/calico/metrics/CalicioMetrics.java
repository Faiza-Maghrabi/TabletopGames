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
import games.calico.components.CalicoCatCard;


public class CalicioMetrics implements IMetricsCollection {
    /**
     * How many times a cat was collected by a player
     */
    public static class CatsAchievedCount extends AbstractMetric {

        @Override
        protected boolean _run(MetricsGameListener listener, Event e, Map<String, Object> records) {
            CalicoGameState gs = (CalicoGameState) e.state;
            CalicoCatCard[] activeCats = gs.getActiveCats();
            HashMap<Cat, Counter>[] playerCatScore = gs.getPlayerCatScore();

            for (int i = 0; i < gs.getNPlayers(); i++) {
                for (int j = 0; j < gs.getActiveCats().length; j++) {
                    Cat c = activeCats[j].getCat();
                    int catCount = playerCatScore[i].get(c).getValueIdx();
                    records.put("Player " + i + ": " +c.toString() + " Count = ", catCount);
                }
            }
            return true;
        }

        @Override
        public Map<String, Class<?>> getColumns(int nPlayersPerGame, Set<String> playerNames) {
            Map<String, Class<?>> columns = new HashMap<>();
            for (int i = 0; i < nPlayersPerGame; i++){
                for (Cat c: Cat.values()) {
                    columns.put("Player " + i + ": " +c.toString() + " Count = ", Integer.class);
                }
            }
            return columns;
        }

        @Override
        public Set<IGameEvent> getDefaultEventTypes() {
            return Collections.singleton(Event.GameEvent.GAME_OVER);
        }

    }

    public static class ButtonsAchievedCount extends AbstractMetric {

        @Override
        protected boolean _run(MetricsGameListener listener, Event e, Map<String, Object> records) {
            CalicoGameState gs = (CalicoGameState) e.state;
            HashMap<Button, Counter>[] playerButtonScore = gs.getPlayerButtonScore();

            for (int i = 0; i < gs.getNPlayers(); i++) {
                for (Button b : Button.values()) {
                    int buttonCount = playerButtonScore[i].get(b).getValueIdx();
                    records.put("Player " + i + ": " +b.toString() + " Count = ", buttonCount);
                }
            }
            return true;
        }

        @Override
        public Map<String, Class<?>> getColumns(int nPlayersPerGame, Set<String> playerNames) {
            Map<String, Class<?>> columns = new HashMap<>();
            for (int i = 0; i < nPlayersPerGame; i++){
                for (Button b : Button.values()){
                    columns.put("Player " + i + ": " +b.toString() + " Count = ", Integer.class);
                }
            }
            return columns;
        }

        @Override
        public Set<IGameEvent> getDefaultEventTypes() {
            return Collections.singleton(Event.GameEvent.GAME_OVER);
        }

    }

    public static class DesignTokensAchievedCount extends AbstractMetric {

        @Override
        protected boolean _run(MetricsGameListener listener, Event e, Map<String, Object> records) {
            CalicoGameState gs = (CalicoGameState) e.state;
            CalicoCatCard[] activeCats = gs.getActiveCats();
            HashMap<Cat, Counter>[] playerCatScore = gs.getPlayerCatScore();

            for (int i = 0; i < gs.getNPlayers(); i++) {
                for (int j = 0; j < gs.getActiveCats().length; j++) {
                    Cat c = activeCats[j].getCat();
                    int catCount = playerCatScore[i].get(c).getValueIdx();
                    records.put("Player " + i + ": " +c.toString() + " Count = ", catCount);
                }
            }
            return true;
        }

        @Override
        public Map<String, Class<?>> getColumns(int nPlayersPerGame, Set<String> playerNames) {
            Map<String, Class<?>> columns = new HashMap<>();
            for (int i = 0; i < nPlayersPerGame; i++){
                for (Cat c: Cat.values()) {
                    columns.put("Player " + i + ": " +c.toString() + " Count = ", Integer.class);
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
