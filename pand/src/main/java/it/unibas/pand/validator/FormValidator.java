package it.unibas.pand.validator;

import java.util.ArrayList;
import java.util.List;

import it.unibas.pand.Pand;

public abstract class FormValidator implements IFormValidator {
    private List<ErrorMessage> errorList = new ArrayList<>();

    public void addErrorMessage(int errorIndex, String message){
        errorList.add(new ErrorMessage(errorIndex, message));
    }

    public void addErrorMessage(int errorIndex, int messageId){
        String errorMessage = Pand.getInstance().getCurrentActivity().getString(messageId);
        errorList.add(new ErrorMessage(errorIndex, errorMessage));
    }

    public void addErrorMessage(int messageId){
        String errorMessage = Pand.getInstance().getCurrentActivity().getString(messageId);
        errorList.add(new ErrorMessage(errorMessage));
    }

    public void addErrorMessage(String errorMessage){
        errorList.add(new ErrorMessage(errorMessage));
    }

    public void addErrorMessage(ErrorMessage errorMessage){
        errorList.add(errorMessage);
    }

    public List<ErrorMessage> getErrorList() {
        if(this.getErrorListSize() == 0){
            return null;
        }
        List<ErrorMessage> errorList = this.errorList;
        this.errorList = new ArrayList<>();
        return errorList;
    }

    public int getErrorListSize(){
        return errorList.size();
    }
}
