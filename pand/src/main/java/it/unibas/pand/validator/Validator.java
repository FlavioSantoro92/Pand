package it.unibas.pand.validator;

import java.util.ArrayList;
import java.util.List;

public abstract class Validator implements IValidator {
    private List<ErrorMessage> errorList = new ArrayList<>();

    public void addErrorMessage(String message){
        errorList.add(new ErrorMessage(message));
    }

    public void addErrorMessage(int errorIndex, String message){
        errorList.add(new ErrorMessage(errorIndex, message));
    }

    public void addErrorMessage(ErrorMessage errorMessage){
        errorList.add(errorMessage);
    }

    public List<ErrorMessage> getErrorList() {
        if(this.getErrorListSize() == 0){
            return null;
        }
        return errorList;
    }

    public List<ErrorMessage> getErrorListClean() {
        List<ErrorMessage> errorList = getErrorList();
        this.errorList = new ArrayList<>();
        return errorList;
    }

    public int getErrorListSize(){
        return errorList.size();
    }
}
