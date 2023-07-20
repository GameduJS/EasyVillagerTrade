package de.gamedude.easyvillagertrade.scripting.core;

import de.gamedude.easyvillagertrade.scripting.core.script.Script;
import de.gamedude.easyvillagertrade.scripting.movement.InputHandler;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class ScriptCache {

    private final InputHandler inputHandler;
    private final ScriptFactory scriptProcessor;
    private final Map<String, Script> scriptMap;
    private Optional<Script> optionalActiveScript;

    public ScriptCache(ScriptFactory scriptProcessor) {
        this.scriptProcessor = scriptProcessor;
        this.scriptMap = this.scriptProcessor.loadScripts();
        this.inputHandler = new InputHandler();
        this.optionalActiveScript = Optional.empty();
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public boolean setActiveScript(String name) {
        if(name == null) {
            this.optionalActiveScript = Optional.empty();
            return true;
        }
        if(getScriptNames().contains(name)) {
            this.optionalActiveScript = Optional.of(this.scriptMap.get(name));
            return true;
        }
        return false;
    }

    public Script getActiveScript(Consumer<Script> scriptConsumer) {
        if(optionalActiveScript.isEmpty())
            return null;
        Script script = optionalActiveScript.get();
        scriptConsumer.accept(script);
        return script;
    }

    public Set<String> getScriptNames() {
        return this.scriptMap.keySet();
    }

    public void reloadCache() {
        this.scriptMap.clear();
        this.scriptMap.putAll(this.scriptProcessor.loadScripts());
    }

    public void activeScriptTest(Script script) {
        this.optionalActiveScript = Optional.of(script);
    }
}
