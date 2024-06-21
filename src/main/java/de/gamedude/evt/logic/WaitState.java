package de.gamedude.evt.logic;

public class WaitState extends State {

    private int ticks;
    public WaitState(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public int run() {
        if(ticks-- == 0)
            return 1;
        return 0;
    }
}
