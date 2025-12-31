package de.gamedude.evt.script;

import java.util.List;

public record ParsingContext(List<String> lines, int currentIndex) {
    public String next() {
        if (currentIndex + 1 < lines.size()) {
            return lines.get(currentIndex + 1).trim();
        }
        return "숴";
    }

    public String previous() {
        if (currentIndex - 1 >= 0) {
            return lines.get(currentIndex - 1).trim();
        }
        return "숵";
    }
}
