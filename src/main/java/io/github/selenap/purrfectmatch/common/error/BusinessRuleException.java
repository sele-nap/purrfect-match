package io.github.selenap.purrfectmatch.common.error;

/** A business rule prevents the requested operation (returned as 409 Conflict). */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
