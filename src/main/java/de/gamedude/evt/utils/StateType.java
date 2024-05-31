package de.gamedude.evt.utils;

import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.logic.*;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;

public enum StateType {

    INACTIVE("WAIT") {
        @Override
        public State getState(TradeWorkflow tradeWorkflow, String... parameters) {
            return null;
        }
    },

    WAIT("WAIT") {
        @Override
        public State getState(TradeWorkflow tradeWorkflow, String... parameters) {
            int seconds = parseParameter(0, Number::intValue, parameters);
            return new WaitState(tradeWorkflow, seconds);
        }
    },

    BREAK_WORKSTATION("BREAK") {
        @Override
        public State getState(TradeWorkflow tradeWorkflow, String... parameters)  {
            return new BreakState(tradeWorkflow);
        }
    },
    
    PLACE("PLACE") {
        @Override
        public State getState(TradeWorkflow tradeWorkflow, String... parameters) {
            return new PlaceState(tradeWorkflow);
        }
    },

    CHECK_TRADE("CHECK") {
        @Override
        public State getState(TradeWorkflow tradeWorkflow, String... parameters) {
            return new CheckTradeState(tradeWorkflow);
        }
    },

    LOOK("LOOK") {
        @Override
        public State getState(TradeWorkflow tradeWorkflow, String... parameters) {
            if(parameters.length == 1 && parameters[0].equals("$"))
                return new LookState(tradeWorkflow, true);
            float yaw = parseParameter(0, Number::floatValue, parameters);
            float pitch = parseParameter(1, Number::floatValue, parameters);
            return new LookState(tradeWorkflow, yaw, pitch);
        }
    },

    WALK("WALK") {
        @Override
        public State getState(TradeWorkflow tradeWorkflow, String... parameters) {
            int dx = parseParameter(0, Number::intValue, parameters);
            int dz = parseParameter(1, Number::intValue, parameters);
            return new WalkState(tradeWorkflow, dx, dz);
        }
    },

    BUY("BUY") {
        @Override
        public State getState(TradeWorkflow tradeWorkflow, String... parameters) {
            return new BuyState(tradeWorkflow);
        }
    },

    INTERACT("INTERACT") {
        @Override
        public State getState(TradeWorkflow tradeWorkflow, String... parameters) {
            return new InteractState(tradeWorkflow);
        }
    }
    ;

    protected  <T> T parseParameter(int index, Function<Number, T> function, String... parameters) {
        try {
            Number number = NUMBER_FORMAT.parse(parameters[index]);
            return function.apply(number);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US);
    private static final StateType[] VALUES = StateType.values();
    private final String token;

    StateType(String token) {
        this.token = token;
    }

    public abstract State getState(TradeWorkflow tradeWorkflow, String... parameters);

    public static StateType getByToken(String token) throws MalformedParameterException {
        return Arrays.stream(VALUES).filter(stateType -> stateType.token.equals(token)).findFirst().orElseThrow(() -> new MalformedParameterException("Cannot find any command for: " + token));
    }

}
