package it.unibas.pand.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import it.unibas.pand.Pand;
import it.unibas.pand.R;
import it.unibas.pand.validator.ErrorMessage;
import it.unibas.pand.validator.IValidator;
import it.unibas.pand.linker.ILinker;
import it.unibas.pand.linker.LinkerUtility;

public class PandRadioGroup extends RadioGroup implements ILinker {
    public static final String TAG = PandRadioGroup.class.getName();
    private List<IValidator> convalidatorList = new ArrayList<>();
    private String linkToBean;

    public PandRadioGroup(Context context) {
        super(context);
    }

    public PandRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttributes(context, attrs);
    }

    private void getAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PandRadioGroup, 0, 0);
        linkToBean = a.getString(R.styleable.PandEditText_linkToBean);
        a.recycle();
        Log.d(TAG, "LinkToBean: " + linkToBean);
    }

    @Override
    public void addValidator(IValidator c) {
        convalidatorList.add(c);
    }

    private String getRadioButtonSelectedValue(){
        int radioButtonId = getCheckedRadioButtonId();
        if(radioButtonId == -1){
            return "";
        }
        PandRadioButton radioButton = findViewById(radioButtonId);
        return radioButton.getValue();
    }

    @Override
    public List<ErrorMessage> checkAllValidator() {
        List<ErrorMessage> errorMessageList = LinkerUtility.checkAllConvalidator(convalidatorList, getRadioButtonSelectedValue());
        if(errorMessageList != null){
            Log.e(TAG, "Got " + errorMessageList.size() + " errors, not linked to the model");
            for(ErrorMessage errorMessage : errorMessageList){
                Log.e(TAG, errorMessage.toString());
            }
            return errorMessageList;
        }
        return null;
    }

    @Override
    public List<ErrorMessage> link() {
        if(getRadioButtonSelectedValue().equals("")){
            Log.d(TAG, "Set a empty string into the model");
        } else {
            Log.d(TAG, "Set the value " + getRadioButtonSelectedValue() + " into the model");
        }
        Pand.getInstance().getModel().setBeanValue(linkToBean, getRadioButtonSelectedValue());
        return null;
    }

    @Override
    public String getValue() {
        return getRadioButtonSelectedValue();
    }

    @Override
    public void clear() {
        clearCheck();
    }
}
