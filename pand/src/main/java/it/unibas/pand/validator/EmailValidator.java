package it.unibas.pand.validator;

import java.util.ArrayList;
import java.util.List;

import it.unibas.pand.Pand;
import it.unibas.pand.R;

public class EmailValidator extends Validator {
    @Override
    public void validate(Object oValue) {
        List<ErrorMessage> errorList = new ArrayList<>();
        if(oValue == null) {
            addErrorMessage(0, Pand.getInstance().getCurrentActivity().getResources().getString(R.string.string_not_null));
        } else {
            String value = oValue.toString();
            if(value.equals("")){
                addErrorMessage(1, Pand.getInstance().getCurrentActivity().getResources().getString(R.string.string_not_null));
            }
            if(!isValidEmailAddress(value)){
                addErrorMessage(2, Pand.getInstance().getCurrentActivity().getResources().getString(R.string.email_not_valid));
            }
        }
    }

    private boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
}
