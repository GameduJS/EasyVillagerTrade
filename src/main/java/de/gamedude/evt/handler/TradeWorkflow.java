package de.gamedude.evt.handler;

import de.gamedude.evt.script.ScriptManager;

import java.util.*;

public class TradeWorkflow implements Handler {

    public static final TradeWorkflow INSTANCE = new TradeWorkflow();
    private final Map<Class<? extends Handler>, Handler> handlerMap;
    public boolean enableSelection;

    public TradeWorkflow() {
        this.handlerMap = new HashMap<>();
        this.handlerMap.put(TradeRequestContainer.class, new TradeRequestContainer());
        this.handlerMap.put(TradeRequestParser.class, new TradeRequestParser());
        this.handlerMap.put(SelectionInterface.class, new SelectionInterface());
        this.handlerMap.put(ScriptManager.class, new ScriptManager());
        this.handlerMap.put(TradeWithVillagerHandler.class, new TradeWithVillagerHandler());
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T extends Handler> T getHandler(Class<T> clazz) {
        return (T) handlerMap.get(clazz);
    }

    public void tickWorkflow() {

    }
}
