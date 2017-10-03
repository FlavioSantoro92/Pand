package it.unibas.pand.validator;

import java.util.List;

public interface IValidator {

    /**
     * Validate the given value, returning a List of errors if the validation fail,
     * returning null otherwise.
     *
     * @param value the object to validate
     */
    void validate(Object value);
    List<ErrorMessage> getErrorList();
    List<ErrorMessage> getErrorListClean();
    int getErrorListSize();
}
