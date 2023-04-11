package de.gamedude.easyvillagertrade.utils;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.gamedude.easyvillagertrade.EasyVillagerTrade;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ScriptArgumentType implements ArgumentType<String> {

    private final EasyVillagerTradeBase easyVillagerTradeBase = EasyVillagerTrade.getModBase();
    private final Set<String> suggestions = EasyVillagerTrade.getModBase().getScriptCache().getScriptNames();

    public static ScriptArgumentType scriptArgumentType() {
        return new ScriptArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String possibleName = reader.readString();
        if(suggestions.stream().anyMatch(possibleName::equals)) {
            return possibleName;
        }
        throw new SimpleCommandExceptionType(Text.of("'" + possibleName + "' isn't a valid loaded script")).createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();
        for(String s : suggestions)
            if(s.toLowerCase().startsWith(remaining))
                builder.suggest(s);
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return ArgumentType.super.getExamples();
    }
}
