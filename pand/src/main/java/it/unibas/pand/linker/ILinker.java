package it.unibas.pand.linker;

import java.util.List;

import it.unibas.pand.validator.ErrorMessage;
import it.unibas.pand.validator.IValidator;

public interface ILinker {
    void addValidator(IValidator c);
    List<ErrorMessage> checkAllValidator();
    List<ErrorMessage> link();
    Object getValue();
    void clear();
}
