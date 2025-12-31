package de.gamedude.evt.automation;

import de.gamedude.evt.script.ParsingContext;

@FunctionalInterface
public interface StateFactory {
    /**
     * @param args Only the arguments without the command
     * @param ctx context of the entire script
     * @return instance of new state
     * @throws Exception if arguments are wrong
     */
    State create(String[] args, ParsingContext ctx) throws Exception;
}
