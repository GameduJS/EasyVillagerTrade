package de.gamedude.easyvillagertrade.scripting.core;

import de.gamedude.easyvillagertrade.scripting.core.script.Script;

import java.util.Map;
import java.util.Set;

public class ScriptCache {

    private final ScriptProcessor scriptProcessor;
    private final Map<String, Script> scriptMap;
    private Script activeScript;

    public ScriptCache(ScriptProcessor scriptProcessor) {
        this.scriptProcessor = scriptProcessor;
        this.scriptMap = this.scriptProcessor.loadScripts();
    }

    public void setActiveScript(String name) {
        this.activeScript = this.scriptMap.get(name);
    }

    public Script getActiveScript() {
        return activeScript;
    }

    public boolean isScriptActive() {
        return activeScript != null;
    }

    public Set<String> getScriptNames() {
        return this.scriptMap.keySet();
    }

    public void reloadCache() {
        this.scriptMap.clear();
        this.scriptMap.putAll(this.scriptProcessor.loadScripts());
    }
}
