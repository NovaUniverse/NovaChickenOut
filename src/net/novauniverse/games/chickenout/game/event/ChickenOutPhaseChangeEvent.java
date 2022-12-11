package net.novauniverse.games.chickenout.game.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChickenOutPhaseChangeEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    int phase;

    public ChickenOutPhaseChangeEvent(int phase) {
        this.phase = phase;
    }

    public int getPhase() {
        return phase;
    }


    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
