package it.unibas.pand.validator;

import it.unibas.pand.Pand;
import it.unibas.pand.R;

public class StringNotNullValidator extends Validator {
    private Integer minLenght;
    private Integer maxLenght;

    public StringNotNullValidator() {}

    public StringNotNullValidator(int minLenght){
        this.minLenght = minLenght;
    }

    public StringNotNullValidator(int minLenght, int maxLenght) {
        this.minLenght = minLenght;
        this.maxLenght = maxLenght;
    }

    @Override
    public void validate(Object oValue) {
        if(oValue == null || oValue.toString().equals("")) {
            addErrorMessage(0, Pand.getInstance().getCurrentActivity().getResources().getString(R.string.string_not_null));
        } else {
            String value = oValue.toString();
            if(minLenght != null && value.length() < minLenght){
                addErrorMessage(1, String.format(Pand.getInstance().getCurrentActivity().getResources().getString(R.string.string_min_length), minLenght));
            }
            if(maxLenght != null && value.length() > maxLenght){
                addErrorMessage(2, String.format(Pand.getInstance().getCurrentActivity().getResources().getString(R.string.string_max_length), maxLenght));
            }
        }
    }
}
