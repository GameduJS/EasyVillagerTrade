package de.gamedude.evt.logic;


public class MalformedParameterException extends Exception  {

    public MalformedParameterException(Class<?> expectedClass, Class<?> clazz) {
        super("Expected: " + expectedClass.getSimpleName() + " but found " + clazz.getSimpleName());
    }

    public MalformedParameterException(String message) {
        super(message);
    }

}
