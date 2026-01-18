package de.gamedude.evt.handler;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.script.Script;
import de.gamedude.evt.script.ScriptManager;
import org.jetbrains.annotations.Nullable;

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


    @Nullable
    private Script activeScript;
    private boolean enabled;

    public void setActiveScript(@Nullable Script activeScript) {
        this.activeScript = activeScript;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Class<? extends State> getActiveStateClass() {
        if ( this.activeScript == null )
            return State.class;
        if ( this.activeScript.currentState == null )
            return State.class;
        return this.activeScript.currentState.getClass();
    }

    public Script.ScriptPhase getScriptPhase() {
        if ( activeScript == null )
            return Script.ScriptPhase.NONE;
        return this.activeScript.getPhase();
    }

    public void tick() {
        // Script might be loaded; still we don't want to run it (yet).
        if ( !enabled )
            return;
        // No script has been loaded (should not be the case - default script)
        if ( activeScript == null )
            return;

        activeScript.trySwitchPhase();
        Iterator<State> iterator = activeScript.getIterator();

        if ( iterator.hasNext() ) {
            State current = activeScript.currentState;
            if ( current == null ) {
                current = iterator.next();
                current.initState();
                activeScript.currentState = current;
                System.out.println("[DEBUG] STATE INIT: " + current.getClass().getSimpleName());
            }
            current.run();
            if ( current.isDone() ) {
                activeScript.currentState = null;
                System.out.println("[DEBUG] STATE DONE: " + current.getClass().getSimpleName());
            }
        }

    }


    /// TESTING STUFF
    /**
     * TEST STATE
     */
    public static State testState;
    public static List<State> STATES = new LinkedList<>();

    public void tickDebug() {
        if ( testState != null ) {
            if ( testState.isDone() ) {
                testState = null;
                return;
            }
            testState.run();
        }

        if ( !STATES.isEmpty() ) {
            State current = STATES.get(0);
            System.out.println("[DEBUG] PROCESSING STATE: " + current.getClass().getSimpleName());
            current.run();

            if(current.isDone()) {
                System.out.println("[DEBUG] STATE DONE: " + current.getClass().getSimpleName());
                STATES.remove(0);

                // INIT NEW STATE
                if ( !STATES.isEmpty() ) {
                    STATES.get(0).initState();
                }
            }
        }

    }
}
