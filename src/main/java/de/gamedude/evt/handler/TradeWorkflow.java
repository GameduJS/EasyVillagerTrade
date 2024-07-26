package de.gamedude.evt.handler;

import de.gamedude.evt.config.Config;
import de.gamedude.evt.script.Script;
import de.gamedude.evt.script.ScriptManager;

import java.util.*;

public class TradeWorkflow implements Handler {

    public static final TradeWorkflow INSTANCE = new TradeWorkflow();
    private final Map<Class<? extends Handler>, Handler> handlerMap;
    private boolean enabled;
    public boolean enableSelection;

    public TradeWorkflow() {
        this.handlerMap = new HashMap<>();
        this.handlerMap.put(TradeRequestContainer.class, new TradeRequestContainer());
        this.handlerMap.put(TradeRequestParser.class, new TradeRequestParser());
        this.handlerMap.put(SelectionInterface.class, new SelectionInterface());
        this.handlerMap.put(ScriptManager.class, new ScriptManager());
        this.handlerMap.put(TradeWithVillagerHandler.class, new TradeWithVillagerHandler());
    }

    public void toggle(boolean state) {
        this.enabled = state;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Handler> T getHandler(Class<T> clazz) {
        return (T) handlerMap.get(clazz);
    }

    /**
     * INACTIVE
     * BREAK
     * WAIT 10
     *
     */
    public void tickWorkflow() {
        if(!enabled)
            return;
        Script script = getHandler(ScriptManager.class).getScript();
        if(script != null) {
            script.tick();
        }
    }
}
