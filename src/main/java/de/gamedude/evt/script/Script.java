package de.gamedude.evt.script;


import de.gamedude.evt.automation.State;

import java.util.HashMap;
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

    private ScriptState currentState = ScriptState.INIT;

    public enum ScriptState {
        INIT,
        FOUND,
        REPEAT
    }

    private final Map<ScriptState, List<State>> script = new HashMap<>();

    public void loadScript(ScriptState state, List<State> subScript) {
        this.script.put(state, subScript);
    }

    public List<State> getScript() {
        return this.script.get(currentState);
    }

    public void switchState() {
        if ( this.currentState == ScriptState.INIT )
            this.currentState = ScriptState.REPEAT;
        else if ( this.currentState == ScriptState.FOUND )
            this.currentState = ScriptState.REPEAT;
    }


    /*
     * NEED TO:
     * - Parse script
     * - Save original content
     * - depending on STATE copy into temp list
     * - go through list: executing states
     */


}
