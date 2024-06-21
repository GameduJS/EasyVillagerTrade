package de.gamedude.evt.utils;

import de.gamedude.evt.EasyVillagerTrade;
import de.gamedude.evt.logic.*;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;

public enum StateType {

    INACTIVE("INACTIVE") {
        @Override
        public State getState(String... parameters) {
            return null;
        }
    },

    WAIT("WAIT") {
        @Override
        public State getState(String... parameters) {
            int seconds = parseParameter(0, Number::intValue, parameters);
            return new WaitState(seconds);
        }
    },

    WAIT_PROFESSION("WAIT_PROFESSION") {
        @Override
        public State getState(String... parameters) {
            String profession = parameters[0];
            return new WaitProfessionState(profession);
        }
    },

    BREAK_WORKSTATION("BREAK") {
        @Override
        public State getState(String... parameters)  {
            return new BreakState();
        }
    },
    
    PLACE("PLACE") {
        @Override
        public State getState(String... parameters) {
            return new PlaceState();
        }
    },

    CHECK_TRADE("CHECK") {
        @Override
        public State getState(String... parameters) {
            return new CheckTradeState();
        }
    },

    LOOK("LOOK") {
        @Override
        public State getState(String... parameters) {
            if(parameters.length == 2) {
                // EXAMPLE: LOOK $CONFIG
            }
            if(parameters.length == 1 && parameters[0].equals("$")) {
                float yaw = EasyVillagerTrade.CONFIG.getProperty("yaw").getAsFloat();
                float pitch = EasyVillagerTrade.CONFIG.getProperty("pitch").getAsFloat();
                return new LookState(yaw, pitch);
            }
            float yaw = parseParameter(0, Number::floatValue, parameters);
            float pitch = parseParameter(1, Number::floatValue, parameters);
            return new LookState(yaw, pitch);
        }
    },

    WALK("WALK") {
        @Override
        public State getState(String... parameters) {
            int dx = parseParameter(0, Number::intValue, parameters);
            int dz = parseParameter(1, Number::intValue, parameters);
            return new WalkState(dx, dz);
        }
    },

    BUY("BUY") {
        @Override
        public State getState(String... parameters) {
            return new BuyState();
        }
    },

    INTERACT("INTERACT") {
        @Override
        public State getState(String... parameters) {
            return new InteractState();
        }
    },

    SELECT("SELECT") {
        @Override
        public State getState(String... parameters) {
            return new SelectState();
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

    public abstract State getState(String... parameters);

    public static StateType getByToken(String token) throws MalformedParameterException {
        return Arrays.stream(VALUES).filter(stateType -> stateType.token.equals(token)).findFirst().orElseThrow(() -> new MalformedParameterException("Cannot find any command for: " + token));
    }

}
