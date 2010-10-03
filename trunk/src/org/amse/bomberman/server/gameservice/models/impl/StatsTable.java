/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameservice.models.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kirilchuk V.E.
 */
public class StatsTable {
    private final Map<ModelPlayer, Stat> stats;

    public StatsTable(List<ModelPlayer> players) {
        this.stats = new HashMap<ModelPlayer, Stat>(players.size());
        for (ModelPlayer player : players) {
            this.stats.put(player, new Stat());
        }
    }

    public Map<ModelPlayer, Stat> getStats() {
        return stats;
    }

    public static class Stat {

        private int kills;
        private int deaths;
        private int suicides;

        public int getDeaths() {
            return deaths + suicides;
        }

        public void increaseDeaths() {
            this.deaths++;
        }

        public int getKills() {
            return kills + suicides;
        }

        public void increaseKills() {
            this.kills++;
        }

        public int getSuicides() {
            return suicides;
        }

        public void increaseSuicides() {
            this.suicides++;
        }

        public int getPoints() {
            int divisor = deaths + suicides;            
            return (divisor == 0 ? (kills +3) : (kills/divisor));
        }
    }
}
