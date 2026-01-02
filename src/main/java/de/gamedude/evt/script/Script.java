package de.gamedude.evt.script;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.handler.TradeWithVillagerHandler;
import de.gamedude.evt.handler.TradeWorkflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>Ein Skript ist eine Ansammlung von Abfolgen von 'State's.
 * Je nach Kondition wird eine andere Abfolge ausgeführt.
 * Dies ist vergleichbar mit if-else-Blöcken.
 * </p>
 * <p>Man könnte einen größeren Aufwand betreiben, indem man die Skriptsprache
 * vervollständigt und Wahrheitsabfragen implementiert, dennoch ist das für diesen
 * Zweck von 3 definierten Lagen überflüssig. </p>
 * <p>Eine weitere Möglichkeit wäre es - wie schoneinmal probiert - Java Klassen
 *  zu schreiben, welche die Logik ausführen, nur das Laden dieser Klassen in
 *  den Fabric Classloader hat nicht geklappt.</p>
 *  <p>Hier wenden wir uns an die erste "Methode".
 *  Das Skript kann drei Zustände aufweisen: 'Init', 'Repeat' und 'Found' </p>
 *
 *  TODO: Implementierung des Wechsels zwischen den Zuständen.
 *
 */
public class Script {

    private final TradeWithVillagerHandler tradeWithVillagerHandler
            = TradeWorkflow.INSTANCE.getHandler(TradeWithVillagerHandler.class);
    private ScriptPhase phase = ScriptPhase.INIT;

    private Iterator<State> iterator;
    public State currentState;

    public enum ScriptPhase {
        INIT,
        FOUND,
        REPEAT
    }

    private final Map<ScriptPhase, List<State>> script = new HashMap<>();

    /**
     * Loads a single subscript into the script based on its "State"
     * @param state
     * @param subScript
     */
    public void loadScript(ScriptPhase state, List<State> subScript) {
        this.script.put(state, subScript);
    }

    private List<State> getScript() {
        return this.script.get(phase);
    }

    /**
     * Should be called every tick to check whether a new subscript should be "played".
     */
    public void trySwitchPhase() {
        if ( iterator == null ) { // begin script
            this.phase = ScriptPhase.INIT;
            this.iterator = getScript().iterator();
            return;
        }

        // Subscripts should not be interrupted
        if ( iterator.hasNext() )
            return;

        // Go out of repeat as soon as trade is found and subscript finished
        if ( tradeWithVillagerHandler.shouldSwitchState() )
            this.phase = ScriptPhase.FOUND;
        if ( this.phase == ScriptPhase.INIT )
            this.phase = ScriptPhase.REPEAT;
        else if ( this.phase == ScriptPhase.FOUND )
            this.phase = ScriptPhase.REPEAT;
        // Repeat state should be repeated
        this.iterator = getScript().iterator();
    }

    public Iterator<State> getIterator() {
        return iterator;
    }

    /*
     * NEED TO:
     * - Parse script
     * - Save original content
     * - depending on STATE copy into temp list
     * - go through list: executing states
     */


}
