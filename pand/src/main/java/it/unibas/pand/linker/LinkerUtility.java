package it.unibas.pand.linker;

import java.util.ArrayList;
import java.util.List;

import it.unibas.pand.validator.ErrorMessage;
import it.unibas.pand.validator.IValidator;

public class LinkerUtility {
    public static List<ErrorMessage> checkAllConvalidator(List<IValidator> convalidatorList, Object value){
        List<ErrorMessage> errorMessageList = new ArrayList<>();
        for(IValidator validator : convalidatorList){
            validator.validate(value);
            List<ErrorMessage> errorMessageListTmp = validator.getErrorListClean();
            if(errorMessageListTmp != null){
                errorMessageList.addAll(errorMessageListTmp);
            }
        }
        if(errorMessageList.size() == 0){
            return null;
        }
        return  errorMessageList;
    }
}
