package de.gamedude.evt.handler;

import de.gamedude.evt.config.Config;
import de.gamedude.evt.script.Script;
import de.gamedude.evt.script.ScriptManager;

import java.util.*;

public class TradeWorkflow implements Handler {

    private final Map<Class<? extends Handler>, Handler> handlerMap;
    private boolean enabled;

    public TradeWorkflow() {
        this.handlerMap = new HashMap<>();
        this.handlerMap.put(TradeRequestContainer.class, new TradeRequestContainer());
        this.handlerMap.put(TradeRequestParser.class, new TradeRequestParser());
        this.handlerMap.put(SelectionInterface.class, new SelectionInterface());
        this.handlerMap.put(ScriptManager.class, new ScriptManager(this));
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
        if(script != null)
            script.tick();
    }

    @Override
    public void reloadConfig(Config config) {

    }
}
