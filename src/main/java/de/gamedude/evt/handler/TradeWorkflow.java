package de.gamedude.evt.handler;

import de.gamedude.evt.automation.AutomationProcessor;
import de.gamedude.evt.automation.State;
import de.gamedude.evt.script.ScriptManager;

import java.util.*;

public class TradeWorkflow implements Handler {

    public static final TradeWorkflow INSTANCE = new TradeWorkflow();
    private final Map<Class<? extends Handler>, Handler> handlerMap;
    public boolean enableSelection;


    /**
     * TEST STATE
     */
    public static State testState;
    public static List<State> STATES = new LinkedList<>();

    public TradeWorkflow() {
        this.handlerMap = new HashMap<>();
        this.handlerMap.put(TradeRequestContainer.class, new TradeRequestContainer());
        this.handlerMap.put(TradeRequestParser.class, new TradeRequestParser());
        this.handlerMap.put(SelectionInterface.class, new SelectionInterface());
        this.handlerMap.put(ScriptManager.class, new ScriptManager());
        this.handlerMap.put(TradeWithVillagerHandler.class, new TradeWithVillagerHandler());

        //Automation
        this.handlerMap.put(AutomationProcessor.class, new AutomationProcessor());
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T extends Handler> T getHandler(Class<T> clazz) {
        return (T) handlerMap.get(clazz);
    }

    public void tickWorkflow() {
        if ( testState != null ) {
            if ( testState.isDone() ) {
                testState = null;
                return;
            }
            testState.run();
        }

        if ( !STATES.isEmpty() ) {
            State current = STATES.get(0);
            System.out.println("[DEBUG] PROCESSING STATE; " + current.getClass().getSimpleName());
            current.run();

            if(current.isDone()) {
                System.out.println("[DEBUG] STATE DONE;:" + current.getClass().getSimpleName());
                STATES.remove(0);
            }
        }

    }
}
