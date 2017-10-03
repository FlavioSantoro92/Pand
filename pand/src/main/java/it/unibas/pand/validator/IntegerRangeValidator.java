package it.unibas.pand.validator;

import it.unibas.pand.Pand;
import it.unibas.pand.R;

public class IntegerRangeValidator extends Validator {
    private Integer minLenght;
    private Integer maxLenght;

    public IntegerRangeValidator(Integer minLenght, Integer maxLenght) {
        this.minLenght = minLenght;
        this.maxLenght = maxLenght;
    }

    @Override
    public void validate(Object oValue) {
        try{
            Integer integerValue = Integer.parseInt(oValue.toString());
            if(this.minLenght != null && this.minLenght > integerValue){
                addErrorMessage(0, Pand.getInstance().getCurrentActivity().getResources().getString(R.string.integer_min_value) + " " + this.minLenght);
            }
            if(this.maxLenght != null && this.maxLenght < integerValue){
                addErrorMessage(1, Pand.getInstance().getCurrentActivity().getResources().getString(R.string.integer_max_value) + " " + this.maxLenght);
            }
        } catch (NumberFormatException e){
            addErrorMessage(1, Pand.getInstance().getCurrentActivity().getResources().getString(R.string.integer_invalid_value));
        }
    }
}
