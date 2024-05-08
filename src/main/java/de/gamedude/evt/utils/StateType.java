package de.gamedude.evt.utils;

import com.mojang.logging.LogUtils;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.logic.*;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;

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
            int seconds = parseParameter(0, Integer.class, parameters).intValue();
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
            float yaw = parseParameter(0, Float.class, parameters).floatValue();
            float pitch = parseParameter(1, Float.class, parameters).floatValue();
            return new LookState(tradeWorkflow, yaw, pitch);
        }
    },

    WALK("WALK") {
        @Override
        public State getState(TradeWorkflow tradeWorkflow, String... parameters) {
            int dx = parseParameter(0, Integer.class, parameters).intValue();
            int dz = parseParameter(1, Integer.class, parameters).intValue();
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

    protected  Number parseParameter(int index, Class<?> expected, String... parameters) {
        try {
            Number number = NUMBER_FORMAT.parse(parameters[index]);
            if(!number.getClass().isAssignableFrom(expected)) {
                LogUtils.getLogger().error("Expected:  " + expected.getSimpleName() + " but found " + number.getClass().getSimpleName());
                throw new RuntimeException("Parse failed!!");
            }
            return number;
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
