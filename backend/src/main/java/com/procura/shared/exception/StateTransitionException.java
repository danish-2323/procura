package com.procura.shared.exception;

public class StateTransitionException extends RuntimeException {
    public StateTransitionException(String entityName, String currentState, String targetAction) {
        super("Illegal state transition on %s: cannot perform '%s' from state '%s'".formatted(entityName, targetAction, currentState));
    }

    public StateTransitionException(String message) {
        super(message);
    }
}
